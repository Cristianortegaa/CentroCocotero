package srangeldev.centrococotero.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import srangeldev.centrococotero.models.Pedido;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, String> {
    
    List<Pedido> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
    
    Page<Pedido> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    Optional<Pedido> findByIdAndUsuarioId(String id, Long usuarioId);
    
    long countByUsuarioId(Long usuarioId);
}
