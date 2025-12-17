package srangeldev.centrococotero.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srangeldev.centrococotero.models.Favorito;
import srangeldev.centrococotero.models.Producto;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.repositories.FavoritoRepository;
import srangeldev.centrococotero.repositories.ProductoRepository;
import srangeldev.centrococotero.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FavoritoServiceImpl implements FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public boolean toggleFavorito(Long usuarioId, String productoId) {
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Optional<Favorito> favoritoExistente = favoritoRepository.findByUsuarioAndProducto(usuario, producto);

        if (favoritoExistente.isPresent()) {
            // Si ya existe, lo eliminamos
            favoritoRepository.deleteByUsuarioAndProducto(usuario, producto);
            return false; // Eliminado
        } else {
            // Si no existe, lo creamos
            Favorito nuevoFavorito = Favorito.builder()
                    .id(UUID.randomUUID().toString().substring(0, 11))
                    .usuario(usuario)
                    .producto(producto)
                    .build();
            favoritoRepository.save(nuevoFavorito);
            return true; // Agregado
        }
    }

    @Override
    public boolean esFavorito(Long usuarioId, String productoId) {
        Usuario usuario = userRepository.findById(usuarioId).orElse(null);
        Producto producto = productoRepository.findById(productoId).orElse(null);
        
        if (usuario == null || producto == null) {
            return false;
        }
        
        return favoritoRepository.findByUsuarioAndProducto(usuario, producto).isPresent();
    }

    @Override
    public List<Favorito> obtenerFavoritos(Long usuarioId) {
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return favoritoRepository.findByUsuario(usuario);
    }

    @Override
    public int contarFavoritos(Long usuarioId) {
        return obtenerFavoritos(usuarioId).size();
    }
}
