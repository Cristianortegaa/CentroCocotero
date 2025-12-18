package srangeldev.centrococotero.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srangeldev.centrococotero.models.ItemCarrito;
import srangeldev.centrococotero.models.Producto;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.repositories.ItemCarritoRepository;
import srangeldev.centrococotero.repositories.ProductoRepository;
import srangeldev.centrococotero.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ItemCarrito agregarProducto(Long usuarioId, String productoId, Integer cantidad) {
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar stock disponible
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }

        // Buscar si ya existe el producto en el carrito
        var itemExistente = itemCarritoRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);

        if (itemExistente.isPresent()) {
            // Si ya existe, actualizar la cantidad
            ItemCarrito item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
            }
            
            item.setCantidad(nuevaCantidad);
            return itemCarritoRepository.save(item);
        } else {
            // Si no existe, crear nuevo item
            ItemCarrito nuevoItem = ItemCarrito.builder()
                    .id(UUID.randomUUID().toString().substring(0, 11))
                    .usuario(usuario)
                    .producto(producto)
                    .cantidad(cantidad)
                    .precioUnitario(producto.getPrecio())
                    .build();

            return itemCarritoRepository.save(nuevoItem);
        }
    }

    @Override
    public ItemCarrito actualizarCantidad(String itemId, Integer cantidad) {
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (cantidad <= 0) {
            itemCarritoRepository.delete(item);
            return null;
        }

        // Verificar stock
        if (item.getProducto().getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + item.getProducto().getStock());
        }

        item.setCantidad(cantidad);
        return itemCarritoRepository.save(item);
    }

    @Override
    public void eliminarItem(String itemId) {
        itemCarritoRepository.deleteById(itemId);
    }

    @Override
    public void vaciarCarrito(Long usuarioId) {
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        itemCarritoRepository.deleteByUsuario(usuario);
    }

    @Override
    public List<ItemCarrito> obtenerCarrito(Long usuarioId) {
        return itemCarritoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<ItemCarrito> listarPorUsuario(Long usuarioId) {
        return itemCarritoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public BigDecimal calcularTotal(Long usuarioId) {
        List<ItemCarrito> items = obtenerCarrito(usuarioId);
        return items.stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public int contarItems(Long usuarioId) {
        return (int) itemCarritoRepository.countByUsuarioId(usuarioId);
    }
}
