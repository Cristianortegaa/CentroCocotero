package srangeldev.centrococotero.services;

import com.itextpdf.text.DocumentException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srangeldev.centrococotero.models.*;
import srangeldev.centrococotero.repositories.PagoRepository;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de pagos
 * Maneja la integración con Stripe, creación de pagos y coordinación del proceso de compra
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final PdfService pdfService;
    private final EmailService emailService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    /**
     * Procesa el pago completo y crea el pedido
     * 
     * @param usuario Usuario que realiza la compra
     * @param direccionEnvio Dirección de envío
     * @param notas Notas adicionales del pedido
     * @param paymentMethodId ID del método de pago de Stripe
     * @return Pedido creado
     * @throws Exception Si hay error en el proceso
     */
    @Override
    public Pedido procesarPagoYCrearPedido(
            Usuario usuario,
            String direccionEnvio,
            String notas,
            String paymentMethodId
    ) throws Exception {
        // 1. Obtener items del carrito
        List<ItemCarrito> itemsCarrito = carritoService.listarPorUsuario(usuario.getId());

        if (itemsCarrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // 2. Calcular total
        BigDecimal total = itemsCarrito.stream()
                .map(item -> item.getProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Procesar pago con Stripe
        String transaccionId = procesarPagoStripe(
                total,
                "eur",
                "Pedido Centro Cocotero - Usuario: " + usuario.getEmail()
        );

        // 4. Crear el pedido
        Pedido pedido = crearPedido(usuario, itemsCarrito, total, direccionEnvio, notas);

        // 5. Crear el registro de pago
        Pago pago = crearRegistroPago(pedido, usuario, total, transaccionId);
        pedido.setPago(pago);

        // 6. Guardar el pedido (cascada guarda líneas y pago)
        pedido = pedidoService.guardar(pedido);

        // 7. Vaciar el carrito
        carritoService.vaciarCarrito(usuario.getId());

        // 8. Generar PDF y enviar email
        enviarConfirmacion(pedido);

        log.info("Pedido {} procesado exitosamente - Pago: {} - Usuario: {}",
                pedido.getId(), pago.getId(), usuario.getEmail());

        return pedido;
    }

    /**
     * Procesa el pago con Stripe
     */
    private String procesarPagoStripe(BigDecimal amount, String currency, String description) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        // Stripe trabaja con centavos
        long amountInCents = amount.multiply(new BigDecimal(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency.toLowerCase())
                .setDescription(description)
                .addPaymentMethodType("card")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        log.info("Pago procesado con Stripe - Payment Intent: {} - Monto: {} {}",
                paymentIntent.getId(), amount, currency.toUpperCase());

        return paymentIntent.getId();
    }

    /**
     * Crea el pedido con sus líneas
     */
    private Pedido crearPedido(
            Usuario usuario,
            List<ItemCarrito> itemsCarrito,
            BigDecimal total,
            String direccionEnvio,
            String notas
    ) {
        Pedido pedido = new Pedido();
        pedido.setId(generarIdPedido());
        pedido.setUsuario(usuario);
        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setNotas(notas);
        pedido.setCreatedAt(LocalDateTime.now());
        pedido.setUpdatedAt(LocalDateTime.now());

        // Crear líneas de pedido
        List<LineaPedido> lineas = new ArrayList<>();
        for (ItemCarrito item : itemsCarrito) {
            LineaPedido linea = LineaPedido.builder()
                    .id(UUID.randomUUID().toString().substring(0, 11))
                    .pedido(pedido)
                    .producto(item.getProducto())
                    .precioUnitario(item.getProducto().getPrecio())
                    .cantidad(item.getCantidad())
                    .subtotal(item.getProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
                    .build();
            lineas.add(linea);
        }
        pedido.setLineas(lineas);

        return pedido;
    }

    /**
     * Crea el registro de pago en la base de datos
     */
    private Pago crearRegistroPago(
            Pedido pedido,
            Usuario usuario,
            BigDecimal total,
            String transaccionId
    ) {
        return Pago.builder()
                .id(UUID.randomUUID().toString().substring(0, 11))
                .pedido(pedido)
                .usuario(usuario)
                .cantidad(total)
                .metodoPago(MetodoPago.STRIPE)
                .estado(EstadoPago.COMPLETADO)
                .transaccionExternaId(transaccionId)
                .build();
    }

    /**
     * Genera PDF y envía email de confirmación
     */
    private void enviarConfirmacion(Pedido pedido) {
        try {
            byte[] pdfBytes = pdfService.generarFacturaPdf(pedido);
            emailService.enviarConfirmacionPedido(pedido, pdfBytes);
            log.info("Confirmación enviada para pedido: {}", pedido.getId());
        } catch (DocumentException e) {
            log.error("Error al generar PDF para pedido {}: {}", pedido.getId(), e.getMessage());
        } catch (MessagingException e) {
            log.error("Error al enviar email para pedido {}: {}", pedido.getId(), e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.error("Error en la codificación del correo {}: {}", pedido.getId(), e.getMessage());
        }
    }

    /**
     * Busca un pago por su ID
     */
    @Override
    public Pago buscarPorId(String id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + id));
    }

    /**
     * Verifica si un pago de Stripe fue exitoso
     */
    @Override
    public boolean verificarPagoStripe(String paymentIntentId) {
        try {
            Stripe.apiKey = stripeApiKey;
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return "succeeded".equals(paymentIntent.getStatus());
        } catch (StripeException e) {
            log.error("Error al verificar Payment Intent: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Genera un ID único para el pedido
     */
    private String generarIdPedido() {
        return UUID.randomUUID().toString().substring(0, 11).toUpperCase();
    }
}
