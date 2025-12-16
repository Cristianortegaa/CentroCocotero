package srangeldev.centrococotero.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import srangeldev.centrococotero.exceptions.ProductoNoEncontradoException;
import srangeldev.centrococotero.models.Comentario;
import srangeldev.centrococotero.models.Producto;
import srangeldev.centrococotero.models.TipoCategoria;
import srangeldev.centrococotero.repositories.ComentarioRepository;
import srangeldev.centrococotero.repositories.ProductoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ComentarioRepository comentarioRepository;

    //Productos

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
        return productoRepository.findByNombreIgnoreCase(nombre);
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

    // Comentarios

    @Override
    public List<Comentario> obtenerComentarios(String productoId) {
        return comentarioRepository.findByProductoId(productoId);
    }

    @Override
    public Comentario guardarComentario(Comentario comentario) {
        comentario.setFecha(LocalDateTime.now());
        return comentarioRepository.save(comentario);
    }
}