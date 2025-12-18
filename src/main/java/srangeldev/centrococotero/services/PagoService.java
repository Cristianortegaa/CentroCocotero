package srangeldev.centrococotero.services;

import srangeldev.centrococotero.models.Pago;
import srangeldev.centrococotero.models.Pedido;
import srangeldev.centrococotero.models.Usuario;

/**
 * Interfaz del servicio de pagos
 */
public interface PagoService {
    
    /**
     * Procesa el pago y crea el pedido completo
     */
    Pedido procesarPagoYCrearPedido(
            Usuario usuario,
            String direccionEnvio,
            String notas,
            String paymentMethodId
    ) throws Exception;
    
    /**
     * Busca un pago por su ID
     */
    Pago buscarPorId(String id);
    
    /**
     * Verifica si un pago de Stripe fue exitoso
     */
    boolean verificarPagoStripe(String paymentIntentId);
}
