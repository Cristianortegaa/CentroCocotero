package srangeldev.centrococotero.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class
CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                       AuthenticationException exception) throws IOException, ServletException {
        
        String errorMessage = "Error de autenticación";
        
        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "El email ingresado no está registrado";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "La contraseña es incorrecta";
        } else if (exception instanceof DisabledException) {
            errorMessage = "Tu cuenta está deshabilitada";
        } else if (exception instanceof LockedException) {
            errorMessage = "Tu cuenta está bloqueada";
        }

        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        setDefaultFailureUrl("/auth/login?error=" + encodedMessage);
        super.onAuthenticationFailure(request, response, exception);
    }
}
