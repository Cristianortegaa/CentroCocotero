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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalDataController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private FavoritoService favoritoService;

    // ============ CSRF TOKEN ============
    @ModelAttribute("_csrf")
    public CsrfToken csrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }

    @ModelAttribute("csrfToken")
    public String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrfToken != null ? csrfToken.getToken() : "";
    }

    @ModelAttribute("csrfParamName")
    public String getCsrfParamName(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrfToken != null ? csrfToken.getParameterName() : "_csrf";
    }

    @ModelAttribute("csrfHeaderName")
    public String getCsrfHeaderName(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrfToken != null ? csrfToken.getHeaderName() : "X-CSRF-TOKEN";
    }

    // ============ USUARIO Y AUTENTICACIÓN ============
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

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)
                && !(auth.getPrincipal() instanceof String);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        Usuario usuario = getUsuarioLogueado();
        if (usuario != null) {
            return "ADMIN".equals(usuario.getRol());
        }
        return false;
    }

    @ModelAttribute("username")
    public String getUsername() {
        Usuario usuario = getUsuarioLogueado();
        if (usuario != null) {
            return usuario.getNombre() + " " + usuario.getApellidos();
        }
        return null;
    }

    @ModelAttribute("userRole")
    public String getUserRole() {
        Usuario usuario = getUsuarioLogueado();
        if (usuario != null) {
            return usuario.getRol();
        }
        return null;
    }

    // ============ CARRITO ============
    @ModelAttribute("cantidadCarrito")
    public int getCantidadCarrito() {
        Usuario usuario = getUsuarioLogueado();
        if (usuario != null) {
            return carritoService.contarItems(usuario.getId());
        }
        return 0;
    }

    @ModelAttribute("hasCartItems")
    public boolean hasCartItems() {
        return getCantidadCarrito() > 0;
    }

    // ============ FAVORITOS ============
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

    // ============ CONSTANTES GLOBALES ============
    @ModelAttribute("appName")
    public String getAppName() {
        return "Centro Cocotero";
    }

    @ModelAttribute("appDescription")
    public String getAppDescription() {
        return "Tu tienda de productos tropicales";
    }

    @ModelAttribute("filesPath")
    public String getFilesPath() {
        return "/files/";
    }

    // ============ FECHA Y HORA ============
    @ModelAttribute("currentDateTime")
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    @ModelAttribute("currentYear")
    public int getCurrentYear() {
        return java.time.Year.now().getValue();
    }

    @ModelAttribute("currentMonth")
    public String getCurrentMonth() {
        return java.time.LocalDate.now().getMonth().getDisplayName(
                java.time.format.TextStyle.FULL,
                new java.util.Locale("es", "ES")
        );
    }
}