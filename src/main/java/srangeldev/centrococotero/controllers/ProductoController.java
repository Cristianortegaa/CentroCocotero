package srangeldev.centrococotero.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import srangeldev.centrococotero.models.Comentario;
import srangeldev.centrococotero.models.Producto;
import srangeldev.centrococotero.models.TipoCategoria;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.repositories.UserRepository;
import srangeldev.centrococotero.services.ProductoService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;
    private final UserRepository usuarioRepository;

    @GetMapping("/")
    public String inicio(Model model,
                         @RequestParam(required = false) String busqueda,
                         @RequestParam(required = false) TipoCategoria categoria,
                         @RequestParam(required = false) java.math.BigDecimal precioMin,
                         @RequestParam(required = false) java.math.BigDecimal precioMax,
                         @RequestParam(defaultValue = "8") int cantidad) {

        List<Producto> productos;

        // 1. OBTENER TODOS LOS PRODUCTOS FILTRADOS
        if (busqueda != null && !busqueda.isEmpty()) {
            productos = service.buscarPorNombre(busqueda);
        } else if (categoria != null) {
            productos = service.findByCategoria(categoria);
        } else {
            productos = service.findAll();
        }

        // Filtros de precio
        if (precioMin != null) {
            productos = productos.stream().filter(p -> p.getPrecio().compareTo(precioMin) >= 0).toList();
        }
        if (precioMax != null) {
            productos = productos.stream().filter(p -> p.getPrecio().compareTo(precioMax) <= 0).toList();
        }


        int total = productos.size();
        boolean hayMas = total > cantidad;

        List<Producto> productosParaMostrar = productos.stream()
                .limit(cantidad)
                .toList();

        // ENVIAR A LA VISTA
        model.addAttribute("productos", productosParaMostrar);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);
        model.addAttribute("categorias", TipoCategoria.values());
        model.addAttribute("cantidad", cantidad);
        model.addAttribute("hayMasProductos", hayMas);

        return "index";
    }

    // DETALLE DE PRODUCTO
    @GetMapping("/producto/{id}")
    public String verDetalle(@PathVariable String id, Model model) {
        Producto producto = service.findById(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", TipoCategoria.values());
        model.addAttribute("comentarios", service.obtenerComentarios(id));
        model.addAttribute("nuevoComentario", new Comentario());
        return "app/producto/detalle";
    }

    // GUARDAR COMENTARIO
    @PostMapping("/producto/{id}/comentar")
    public String publicarComentario(@PathVariable String id,
                                     @ModelAttribute Comentario comentario) {
        try {
            comentario.setProductoId(id);

            if (comentario.getUsername() == null || comentario.getUsername().trim().isEmpty()) {
                comentario.setUsername("Anónimo");
            }

            service.guardarComentario(comentario);

        } catch (Exception e) {
            System.err.println("⚠️ Error guardando comentario: " + e.getMessage());
        }

        return "redirect:/producto/" + id;
    }
}