package srangeldev.centrococotero.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import srangeldev.centrococotero.models.*;
import srangeldev.centrococotero.services.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador para el proceso de checkout y pago
 */
@Slf4j
@Controller
@RequestMapping("/app/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final PagoService pagoService;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    /**
     * Muestra la página de checkout
     */
    @GetMapping
    public String mostrarCheckout(@AuthenticationPrincipal Usuario usuario, Model model) {
        // Obtener items del carrito
        List<ItemCarrito> itemsCarrito = carritoService.listarPorUsuario(usuario.getId());

        if (itemsCarrito.isEmpty()) {
            return "redirect:/app/productos/carrito?error=carrito-vacio";
        }

        // Calcular total
        BigDecimal total = itemsCarrito.stream()
                .map(item -> item.getProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("itemsCarrito", itemsCarrito);
        model.addAttribute("total", total);
        model.addAttribute("stripePublicKey", stripePublicKey);

        return "app/checkout/checkout";
    }

    /**
     * Procesa el pago y crea el pedido
     */
    @PostMapping("/procesar")
    public String procesarCheckout(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam("direccionEnvio") String direccionEnvio,
            @RequestParam(value = "notas", required = false) String notas,
            @RequestParam("paymentMethodId") String paymentMethodId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Procesar pago y crear pedido (todo en el servicio)
            Pedido pedido = pagoService.procesarPagoYCrearPedido(
                    usuario,
                    direccionEnvio,
                    notas,
                    paymentMethodId
            );

            log.info("Checkout completado exitosamente - Pedido: {} - Usuario: {}",
                    pedido.getId(), usuario.getEmail());

            redirectAttributes.addFlashAttribute("pedidoId", pedido.getId());
            return "redirect:/app/checkout/confirmacion";

        } catch (Exception e) {
            log.error("Error en checkout: {}", e.getMessage(), e);
            
            String errorMsg = e.getMessage();
            if (errorMsg.contains("carrito")) {
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/app/productos/carrito";
            }
            
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pedido: " + errorMsg);
            return "redirect:/app/checkout";
        }
    }

    /**
     * Muestra la página de confirmación del pedido
     */
    @GetMapping("/confirmacion")
    public String mostrarConfirmacion(
            @ModelAttribute("pedidoId") String pedidoId,
            @AuthenticationPrincipal Usuario usuario,
            Model model
    ) {
        if (pedidoId == null || pedidoId.isEmpty()) {
            return "redirect:/";
        }

        // Buscar el pedido
        Pedido pedido = pedidoService.buscarPorIdYUsuario(pedidoId, usuario.getId())
                .orElse(null);

        model.addAttribute("pedido", pedido);
        return "app/checkout/confirmacion";
    }
}
