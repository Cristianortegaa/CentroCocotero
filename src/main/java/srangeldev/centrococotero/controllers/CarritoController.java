package srangeldev.centrococotero.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import srangeldev.centrococotero.models.ItemCarrito;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.services.CarritoService;
import srangeldev.centrococotero.services.FavoritoService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final FavoritoService favoritoService;

    @GetMapping
    public String verCarrito(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<ItemCarrito> items = carritoService.obtenerCarrito(usuario.getId());
        BigDecimal total = carritoService.calcularTotal(usuario.getId());

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("cantidadItems", items.size());

        return "app/producto/carrito";
    }

    @PostMapping("/agregar/{productoId}")
    public String agregarProducto(@PathVariable String productoId,
                                   @RequestParam(defaultValue = "1") Integer cantidad,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes,
                                   @RequestParam(required = false) String from) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para agregar productos al carrito");
            return "redirect:/auth/login";
        }

        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            
            // Agregar al carrito
            carritoService.agregarProducto(usuario.getId(), productoId, cantidad);
            
            // Si viene de favoritos, quitarlo de favoritos
            if ("favoritos".equals(from)) {
                favoritoService.toggleFavorito(usuario.getId(), productoId);
            }
            
            redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito";
    }

    @PostMapping("/actualizar/{itemId}")
    public String actualizarCantidad(@PathVariable String itemId,
                                      @RequestParam Integer cantidad,
                                      RedirectAttributes redirectAttributes) {
        try {
            carritoService.actualizarCantidad(itemId, cantidad);
            redirectAttributes.addFlashAttribute("success", "Cantidad actualizada");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito";
    }

    @GetMapping("/eliminar/{itemId}")
    public String eliminarItem(@PathVariable String itemId,
                                RedirectAttributes redirectAttributes) {
        try {
            carritoService.eliminarItem(itemId);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito";
    }

    @GetMapping("/vaciar")
    public String vaciarCarrito(Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            carritoService.vaciarCarrito(usuario.getId());
            redirectAttributes.addFlashAttribute("success", "Carrito vaciado");
        }

        return "redirect:/carrito";
    }

    @GetMapping("/cantidad")
    @ResponseBody
    public int obtenerCantidad(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            return carritoService.contarItems(usuario.getId());
        }
        return 0;
    }
}
