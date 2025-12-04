package srangeldev.centrococotero.producto.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srangeldev.centrococotero.producto.exceptions.ProductoException;
import srangeldev.centrococotero.producto.models.Categoria;
import srangeldev.centrococotero.producto.models.Producto;
import srangeldev.centrococotero.producto.repositories.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> getAllProductos() {
        try {
            return productoRepository.findAll();
        } catch (Exception e) {
            log.error("Error al obtener todos los productos: ", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new ProductoException("Error al obtener todos los productos: " + errorMsg, e);
        }
    }

    @Override
    public Producto getProductoById(String id) {
        try {
            return productoRepository.findById(id)
                    .orElseThrow(() -> new ProductoException("Producto no encontrado con ID: " + id));
        } catch (ProductoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al obtener el producto por ID: ", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new ProductoException("Error al obtener el producto por ID: " + errorMsg, e);
        }
    }

    @Override
    @Transactional
    public Producto createProducto(Producto producto) {
        try {
            return productoRepository.save(producto);
        } catch (Exception e) {
            log.error("Error al crear el producto: {}", e.getMessage());
            throw new ProductoException("Error al crear el producto: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Producto updateProducto(String id, Producto productoDetails) {
        try {
            Producto producto = getProductoById(id);
            producto.setNombre(productoDetails.getNombre());
            producto.setDescripcion(productoDetails.getDescripcion());
            producto.setPrecio(productoDetails.getPrecio());
            producto.setStock(productoDetails.getStock());
            producto.setCategoria(productoDetails.getCategoria());
            producto.setImagenes(productoDetails.getImagenes());
            // No actualizamos 'deleted' aquí normalmente, se usa deleteProducto
            return productoRepository.save(producto);
        } catch (Exception e) {
            log.error("Error al actualizar el producto: ", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new ProductoException("Error al actualizar el producto: " + errorMsg, e);
        }
    }

    @Override
    @Transactional
    public void deleteProducto(String id) {
        try {
            Producto producto = getProductoById(id);
            producto.setDeleted(true); // Borrado lógico
            productoRepository.save(producto);
        } catch (Exception e) {
            log.error("Error al eliminar el producto: {}", e.getMessage());
            throw new ProductoException("Error al eliminar el producto: " + e.getMessage());
        }
    }

    // --- Implementación de métodos para CLIENTES ---

    @Override
    public Page<Producto> findAllAvailable(Pageable pageable) {
        return productoRepository.findAllAvailablePaginated(pageable);
    }

    @Override
    public Page<Producto> findByCategoriaAvailable(Categoria categoria, Pageable pageable) {
        return productoRepository.findByCategoriaAvailablePaginated(categoria, pageable);
    }

    @Override
    public Page<Producto> findByNombreAvailable(String nombre, Pageable pageable) {
        return productoRepository.findByNombreAvailablePaginated(nombre, pageable);
    }

    @Override
    public Page<Producto> findByPrecioAvailable(BigDecimal min, BigDecimal max, Pageable pageable) {
        return productoRepository.findByPrecioAvailablePaginated(min, max, pageable);
    }

    // --- Implementación de métodos para ADMIN ---

    @Override
    public Page<Producto> findAllActive(Pageable pageable) {
        return productoRepository.findAllActivePaginated(pageable);
    }

    @Override
    public Page<Producto> findByNombreActive(String nombre, Pageable pageable) {
        return productoRepository.findByNombreActivePaginated(nombre, pageable);
    }

    @Override
    public Page<Producto> findByCategoriaActive(Categoria categoria, Pageable pageable) {
        return productoRepository.findByCategoriaActivePaginated(categoria, pageable);
    }

    @Override
    public Page<Producto> findByNombreAndCategoriaActive(String nombre, Categoria categoria, Pageable pageable) {
        return productoRepository.findByNombreAndCategoriaActivePaginated(nombre, categoria, pageable);
    }

    @Override
    public List<Producto> findRelatedProducts(String productoId, int limit) {
        try {
            Producto producto = getProductoById(productoId);
            if (producto.getCategoria() == null) {
                return List.of();
            }
            Pageable pageable = PageRequest.of(0, limit);
            return productoRepository.findRelatedProducts(producto.getCategoria(), productoId, pageable);
        } catch (Exception e) {
            log.error("Error al obtener productos relacionados: ", e);
            return List.of();
        }
    }
}
