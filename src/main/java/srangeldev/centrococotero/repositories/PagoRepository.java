package srangeldev.centrococotero.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import srangeldev.centrococotero.models.Pago;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, String> {
    
    /**
     * Busca todos los pagos de un usuario
     */
    List<Pago> findByUsuarioIdOrderByPedidoCreatedAtDesc(Long usuarioId);
    
    /**
     * Busca un pago por su transacción externa (Stripe, PayPal, etc.)
     */
    Optional<Pago> findByTransaccionExternaId(String transaccionExternaId);
    
    /**
     * Busca el pago de un pedido específico
     */
    Optional<Pago> findByPedidoId(String pedidoId);
}
