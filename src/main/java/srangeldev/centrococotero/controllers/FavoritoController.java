package srangeldev.centrococotero.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import srangeldev.centrococotero.models.Favorito;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.services.FavoritoService;

import java.util.List;

@Controller
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    @GetMapping
    public String verFavoritos(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<Favorito> favoritos = favoritoService.obtenerFavoritos(usuario.getId());

        model.addAttribute("favoritos", favoritos);
        model.addAttribute("cantidadFavoritos", favoritos.size());

        return "app/favoritos/lista";
    }

    @PostMapping("/toggle/{productoId}")
    public String toggleFavorito(@PathVariable String productoId,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes,
                                   @RequestHeader(value = "Referer", required = false) String referer) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para agregar favoritos");
            return "redirect:/auth/login";
        }

        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            boolean agregado = favoritoService.toggleFavorito(usuario.getId(), productoId);
            
            if (agregado) {
                redirectAttributes.addFlashAttribute("success", "Producto agregado a favoritos");
            } else {
                redirectAttributes.addFlashAttribute("success", "Producto eliminado de favoritos");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // Redirigir a la página anterior o al inicio
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer.substring(referer.indexOf("/", 8)); // Elimina el dominio
        }
        return "redirect:/";
    }

    @GetMapping("/cantidad")
    @ResponseBody
    public int obtenerCantidad(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            return favoritoService.contarFavoritos(usuario.getId());
        }
        return 0;
    }

    @GetMapping("/esfavorito/{productoId}")
    @ResponseBody
    public boolean esFavorito(@PathVariable String productoId, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            return favoritoService.esFavorito(usuario.getId(), productoId);
        }
        return false;
    }
}
