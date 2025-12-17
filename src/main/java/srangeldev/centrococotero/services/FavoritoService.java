package srangeldev.centrococotero.services;

import srangeldev.centrococotero.models.Favorito;
import srangeldev.centrococotero.models.Usuario;

import java.util.List;

public interface FavoritoService {
    
    boolean toggleFavorito(Long usuarioId, String productoId);
    
    boolean esFavorito(Long usuarioId, String productoId);
    
    List<Favorito> obtenerFavoritos(Long usuarioId);
    
    int contarFavoritos(Long usuarioId);
}
