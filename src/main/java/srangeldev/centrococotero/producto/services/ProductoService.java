package srangeldev.centrococotero.producto.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import srangeldev.centrococotero.producto.models.Categoria;
import srangeldev.centrococotero.producto.models.Producto;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoService {
    // Métodos básicos CRUD
    List<Producto> getAllProductos(); // Admin (todos)
    Producto getProductoById(String id);
    Producto createProducto(Producto producto);
    Producto updateProducto(String id, Producto producto);
    void deleteProducto(String id); // Borrado lógico

    // Métodos de búsqueda y filtrado para CLIENTES (Stock > 0 y No borrados)
    Page<Producto> findAllAvailable(Pageable pageable);
    Page<Producto> findByCategoriaAvailable(Categoria categoria, Pageable pageable);
    Page<Producto> findByNombreAvailable(String nombre, Pageable pageable);
    Page<Producto> findByPrecioAvailable(BigDecimal min, BigDecimal max, Pageable pageable);

    // Métodos de búsqueda y filtrado para ADMIN (Incluye sin stock, solo no borrados)
    Page<Producto> findAllActive(Pageable pageable);
    Page<Producto> findByNombreActive(String nombre, Pageable pageable);
    Page<Producto> findByCategoriaActive(Categoria categoria, Pageable pageable);
    Page<Producto> findByNombreAndCategoriaActive(String nombre, Categoria categoria, Pageable pageable);
    
    // Productos relacionados
    List<Producto> findRelatedProducts(String productoId, int limit);
}
