package srangeldev.centrococotero.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
 ;

    @Autowired
    public UserService(UserRepository userRepository ) {
        this.userRepository = userRepository;
    }


    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario registrar(Usuario u) {
       // u.setPassword(passwordEncoder.encode(u.getPassword())); TODO hacer el encoder de password
        return userRepository.save(u);
    }

    @Cacheable(value = "usuarios", key = "#id")
    public Usuario findById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "usuarios", key = "#email")
    public Usuario buscarPorEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    @Cacheable(value = "usuarios")
    public java.util.List<Usuario> findAll() {
        return userRepository.findAllActive();
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario editar(Usuario u) {
        return userRepository.save(u);
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public void borrar(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<Usuario> findByIdOptional(Long id) {
        return userRepository.findActiveById(id);
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public void softDelete(Long id, String deletedBy) {
        Usuario user = findById(id);
        if (user != null) {
            user.softDelete(deletedBy);
            userRepository.save(user);
        }
    }

    public Optional<Usuario> findByEmail(String email) {
        return Optional.ofNullable(buscarPorEmail(email));
    }

    public Page<Usuario> findAllPaginated(Pageable pageable) {
        return userRepository.findAllActivePaginated(pageable);
    }

    public Page<Usuario> findBySearchPaginated(String search, Pageable pageable) {
        return userRepository.findBySearchActivePaginated(search, pageable);
    }

    public Page<Usuario> findByRolPaginated(String rol, Pageable pageable) {
        return userRepository.findByRolActivePaginated(rol, pageable);
    }

    public Page<Usuario> findBySearchAndRolPaginated(String search, String rol, Pageable pageable) {
        return userRepository.findBySearchAndRolActivePaginated(search, rol, pageable);
    }
}
