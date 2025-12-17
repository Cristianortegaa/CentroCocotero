package srangeldev.centrococotero.services;

import srangeldev.centrococotero.models.*;

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

    //Favoritos
    // 1. Para el botón: Si existe lo borra, si no existe lo crea
    void toggleFavorito(String productoId, Usuario usuario);

    // 2. Para el menú del header: Devuelve la lista de favoritos de ese usuario
    List<Favorito> obtenerFavoritosDelUsuario(Usuario usuario);


}