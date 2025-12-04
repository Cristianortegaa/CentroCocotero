package srangeldev.centrococotero.producto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import srangeldev.centrococotero.producto.models.Categoria;
import srangeldev.centrococotero.producto.models.TipoCategoria;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, String> {
    Optional<Categoria> findByTipo(TipoCategoria tipo);
}
