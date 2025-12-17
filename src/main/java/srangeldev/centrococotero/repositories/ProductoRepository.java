package srangeldev.centrococotero.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import srangeldev.centrococotero.models.Producto;
import srangeldev.centrococotero.models.TipoCategoria;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {
    // Buscar productos activos
    List<Producto> findAll();

    // Buscar por categoría
    List<Producto> findByCategoria(TipoCategoria categoria);

    // Buscador por nombre ignorando mayúsculas y minúsculas
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}