package srangeldev.centrococotero.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import srangeldev.centrococotero.models.Favorito;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.services.CarritoService;
import srangeldev.centrococotero.services.FavoritoService;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalDataController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private FavoritoService favoritoService;

    @ModelAttribute("_csrf")
    public CsrfToken csrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }

    @ModelAttribute("usuarioLogueado")
    public Usuario getUsuarioLogueado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            if (principal instanceof Usuario) {
                return (Usuario) principal;
            }
        }
        return null;
    }

    @ModelAttribute("cantidadCarrito")
    public int getCantidadCarrito() {
        Usuario usuario = getUsuarioLogueado();
        if (usuario != null) {
            return carritoService.contarItems(usuario.getId());
        }
        return 0;
    }

    @ModelAttribute("cantidadFavoritos")
    public int getCantidadFavoritos() {
        Usuario usuario = getUsuarioLogueado();
        if (usuario != null) {
            return favoritoService.contarFavoritos(usuario.getId());
        }
        return 0;
    }

    @ModelAttribute("favoritos")
    public List<Favorito> getFavoritos() {
        Usuario usuario = getUsuarioLogueado();
        if (usuario != null) {
            return favoritoService.obtenerFavoritos(usuario.getId());
        }
        return new ArrayList<>();
    }
}