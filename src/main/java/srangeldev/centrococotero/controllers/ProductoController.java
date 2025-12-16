package srangeldev.centrococotero.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import srangeldev.centrococotero.models.Comentario;
import srangeldev.centrococotero.models.Producto;
import srangeldev.centrococotero.models.TipoCategoria;
import srangeldev.centrococotero.services.ProductoService;

import java.util.List;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;

    // Página de Inicio
    @GetMapping
    public String inicio(Model model,
                         @RequestParam(required = false) String busqueda,
                         @RequestParam(required = false) TipoCategoria categoria) {

        List<Producto> productos;

        if (busqueda != null && !busqueda.isEmpty()) {
            productos = service.buscarPorNombre(busqueda);
        } else if (categoria != null) {
            productos = service.findByCategoria(categoria);
        } else {
            productos = service.findAll();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("categorias", TipoCategoria.values());
        return "index";
    }

    @GetMapping("/producto/{id}")
    public String verDetalle(@PathVariable String id, Model model) {
        Producto producto = service.findById(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", TipoCategoria.values());
        model.addAttribute("comentarios", java.util.Collections.emptyList());
        model.addAttribute("nuevoComentario", new Comentario());
        return "app/producto/detalle";
    }

    // Guardar comentario (A PRUEBA DE FALLOS)
    @PostMapping("/producto/{id}/comentar")
    public String publicarComentario(@PathVariable String id,
                                     @ModelAttribute Comentario comentario) {
        try {
            // Preparamos los datos del comentario
            comentario.setProductoId(id);

            // Si el nombre viene vacío, ponemos Anónimo
            if (comentario.getUsername() == null || comentario.getUsername().trim().isEmpty()) {
                comentario.setUsername("Anónimo");
            }

            // Intentamos guardar en Mongo
            service.guardarComentario(comentario);

        } catch (Exception e) {
            // Si falla (Mongo apagado), imprimimos el error en la consola pero NO rompemos la web
            System.err.println("⚠️ NO SE PUDO GUARDAR EL COMENTARIO. MONGODB PARECE APAGADO.");
            System.err.println("Error: " + e.getMessage());
        }

        // Pase lo que pase, volvemos a la página del producto
        return "redirect:/producto/" + id;
    }

}