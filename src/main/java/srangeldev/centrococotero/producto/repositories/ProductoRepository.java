package srangeldev.centrococotero.producto.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import srangeldev.centrococotero.producto.models.Categoria;
import srangeldev.centrococotero.producto.models.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {

    // Buscar productos activos (no borrados)
    List<Producto> findByDeletedFalse();

    // Buscar productos activos y con stock (disponibles para venta)
    // Equivalente a findByCompraIsNull del ejemplo, pero usando stock
    List<Producto> findByDeletedFalseAndStockGreaterThan(Integer stock);

    // Buscar por nombre (ignorando mayúsculas/minúsculas) y activos
    List<Producto> findByNombreContainsIgnoreCaseAndDeletedFalse(String nombre);

    // Buscar por categoría y activos
    List<Producto> findByCategoriaAndDeletedFalse(Categoria categoria);

    // Buscar por rango de precio y activos
    List<Producto> findByPrecioBetweenAndDeletedFalse(BigDecimal min, BigDecimal max);


    // --- Consultas personalizadas con JPQL ---

    // Buscar por categoría, activos y con stock (para el catálogo público)
    @Query("SELECT p FROM Producto p WHERE p.categoria = :categoria AND p.deleted = false AND p.stock > 0 ORDER BY p.id DESC")
    List<Producto> findByCategoriaAvailable(@Param("categoria") Categoria categoria);

    // Buscar producto activo por ID
    @Query("SELECT p FROM Producto p WHERE p.id = :id AND p.deleted = false")
    Optional<Producto> findActiveById(@Param("id") String id);

    // Buscador general activo
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :search, '%')) AND p.deleted = false")
    List<Producto> findByNombreContainingActive(@Param("search") String search);

    // Buscar productos relacionados por categoría, excluyendo el producto actual
    @Query("SELECT p FROM Producto p WHERE p.categoria = :categoria AND p.id <> :excludeId AND p.deleted = false AND p.stock > 0 ORDER BY RAND()")
    List<Producto> findRelatedProducts(@Param("categoria") Categoria categoria, @Param("excludeId") String excludeId, Pageable pageable);


    // --- Paginación para Clientes (Solo disponibles: stock > 0 y no borrados) ---

    @Query("SELECT p FROM Producto p WHERE p.deleted = false AND p.stock > 0 ORDER BY p.id DESC")
    Page<Producto> findAllAvailablePaginated(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.categoria = :categoria AND p.deleted = false AND p.stock > 0 ORDER BY p.id DESC")
    Page<Producto> findByCategoriaAvailablePaginated(@Param("categoria") Categoria categoria, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.deleted = false AND p.stock > 0 ORDER BY p.id DESC")
    Page<Producto> findByNombreAvailablePaginated(@Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :min AND :max AND p.deleted = false AND p.stock > 0 ORDER BY p.id DESC")
    Page<Producto> findByPrecioAvailablePaginated(@Param("min") BigDecimal min, @Param("max") BigDecimal max, Pageable pageable);


    // --- Paginación para Admin (Incluye sin stock, pero no borrados lógicamente) ---

    @Query("SELECT p FROM Producto p WHERE p.deleted = false ORDER BY p.id DESC")
    Page<Producto> findAllActivePaginated(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.deleted = false ORDER BY p.id DESC")
    Page<Producto> findByNombreActivePaginated(@Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.categoria = :categoria AND p.deleted = false ORDER BY p.id DESC")
    Page<Producto> findByCategoriaActivePaginated(@Param("categoria") Categoria categoria, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.categoria = :categoria AND p.deleted = false ORDER BY p.id DESC")
    Page<Producto> findByNombreAndCategoriaActivePaginated(@Param("nombre") String nombre, @Param("categoria") Categoria categoria, Pageable pageable);
}
