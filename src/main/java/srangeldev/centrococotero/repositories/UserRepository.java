package srangeldev.centrococotero.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import srangeldev.centrococotero.models.Usuario;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Long> {
    Usuario findFirstByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.deleted = false ORDER BY u.id DESC")
    List<Usuario> findAllActive();

    @Query("SELECT u FROM Usuario u WHERE u.id = :id AND u.deleted = false")
    Optional<Usuario> findActiveById(@Param("id") Long id);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.deleted = false")
    long countActive();

    @Query("SELECT u FROM Usuario u WHERE (u.nombre LIKE %:search% OR u.apellidos LIKE %:search% OR u.email LIKE %:search%) AND u.deleted = false")
    List<Usuario> findBySearchActive(@Param("search") String search);

    // Pagination methods
    @Query("SELECT u FROM Usuario u WHERE u.deleted = false ORDER BY u.id DESC")
    Page<Usuario> findAllActivePaginated(Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE (LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND u.deleted = false ORDER BY u.id DESC")
    Page<Usuario> findBySearchActivePaginated(@Param("search") String search, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.deleted = false ORDER BY u.id DESC")
    Page<Usuario> findByRolActivePaginated(@Param("rol") String rol, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE (LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND u.rol = :rol AND u.deleted = false ORDER BY u.id DESC")
    Page<Usuario> findBySearchAndRolActivePaginated(@Param("search") String search, @Param("rol") String rol, Pageable pageable);
}