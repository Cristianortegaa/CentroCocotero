package srangeldev.centrococotero.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import srangeldev.centrococotero.models.Favorito;
import srangeldev.centrococotero.models.Producto;
import srangeldev.centrococotero.models.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, String> {
    List<Favorito> findByUsuario(Usuario usuario);

    // Para comprobar si ya le dio a favorito
    Optional<Favorito> findByUsuarioAndProducto(Usuario usuario, Producto producto);

    //Para borrar
    void deleteByUsuarioAndProducto(Usuario usuario, Producto producto);
}