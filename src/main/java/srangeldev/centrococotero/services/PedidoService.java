package srangeldev.centrococotero.services;

import org.springframework.data.domain.Page;
import srangeldev.centrococotero.models.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de pedidos
 */
public interface PedidoService {
    
    List<Pedido> buscarPorUsuario(Long usuarioId);
    
    Page<Pedido> buscarPorUsuarioPaginado(Long usuarioId, int page, int size);
    
    Optional<Pedido> buscarPorId(String id);
    
    Optional<Pedido> buscarPorIdYUsuario(String id, Long usuarioId);
    
    Pedido guardar(Pedido pedido);
    
    long contarPorUsuario(Long usuarioId);
}
