package srangeldev.centrococotero.services;

import srangeldev.centrococotero.models.ItemCarrito;
import srangeldev.centrococotero.models.Usuario;

import java.math.BigDecimal;
import java.util.List;

public interface CarritoService {
    
    ItemCarrito agregarProducto(Long usuarioId, String productoId, Integer cantidad);
    
    ItemCarrito actualizarCantidad(String itemId, Integer cantidad);
    
    void eliminarItem(String itemId);
    
    void vaciarCarrito(Long usuarioId);
    
    List<ItemCarrito> obtenerCarrito(Long usuarioId);
    
    List<ItemCarrito> listarPorUsuario(Long usuarioId);
    
    BigDecimal calcularTotal(Long usuarioId);
    
    int contarItems(Long usuarioId);
}
