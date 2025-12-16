package srangeldev.centrococotero.services;

import srangeldev.centrococotero.models.Comentario;
import srangeldev.centrococotero.models.Producto;
import srangeldev.centrococotero.models.TipoCategoria;

import java.util.List;

public interface ProductoService {
    // Productos
    List<Producto> findAll();
    Producto findById(String id);
    List<Producto> findByCategoria(TipoCategoria categoria);
    List<Producto> buscarPorNombre(String nombre);
    Producto save(Producto producto);
    void deleteById(String id);

    // Comentarios
    List<Comentario> obtenerComentarios(String productoId);
    Comentario guardarComentario(Comentario comentario);
}