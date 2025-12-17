package srangeldev.centrococotero.services.impl; // Asegúrate de que el paquete sea correcto

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para bases de datos
import srangeldev.centrococotero.exceptions.ProductoNoEncontradoException;
import srangeldev.centrococotero.models.*; // Importamos Usuario, Favorito, etc.
import srangeldev.centrococotero.repositories.ComentarioRepository;
import srangeldev.centrococotero.repositories.FavoritoRepository; // Nuevo import
import srangeldev.centrococotero.repositories.ProductoRepository;
import srangeldev.centrococotero.services.ProductoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ComentarioRepository comentarioRepository;
    private final FavoritoRepository favoritoRepository;

    // PRODUCTOS
    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Producto findById(String id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con ID: " + id));
    }

    @Override
    public List<Producto> findByCategoria(TipoCategoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void deleteById(String id) {
        Producto producto = findById(id);
        producto.setDeleted(true);
        productoRepository.save(producto);
    }

    // COMENTARIOS
    @Override
    public List<Comentario> obtenerComentarios(String productoId) {
        return comentarioRepository.findByProductoId(productoId);
    }

    @Override
    public Comentario guardarComentario(Comentario comentario) {
        comentario.setFecha(LocalDateTime.now());
        return comentarioRepository.save(comentario);
    }

    // FAVORITOS
    @Override
    @Transactional
    public void toggleFavorito(String productoId, Usuario usuario) {
        Producto producto = this.findById(productoId);

        Optional<Favorito> favoritoExistente = favoritoRepository.findByUsuarioAndProducto(usuario, producto);

        if (favoritoExistente.isPresent()) {
            favoritoRepository.delete(favoritoExistente.get());
        } else {
            Favorito nuevoFavorito = Favorito.builder()
                    .id(UUID.randomUUID().toString().substring(0, 11))
                    .usuario(usuario)
                    .producto(producto)
                    .build();

            favoritoRepository.save(nuevoFavorito);
        }
    }

    @Override
    public List<Favorito> obtenerFavoritosDelUsuario(Usuario usuario) {
        return favoritoRepository.findByUsuario(usuario);
    }
}