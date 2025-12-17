package srangeldev.centrococotero.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.repositories.UserRepository;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = userRepository.findFirstByEmail(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("User no encontrado");
        }

        return usuario;
    }
}
