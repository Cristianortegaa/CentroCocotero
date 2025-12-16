package srangeldev.centrococotero.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import srangeldev.centrococotero.models.Producto;
import srangeldev.centrococotero.models.TipoCategoria;
import srangeldev.centrococotero.services.ProductoService;

@Controller
@RequestMapping("/admin/productos")
@RequiredArgsConstructor
public class AdminProductoController {

    private final ProductoService service;

    @GetMapping
    public String listarGestion(Model model) {
        model.addAttribute("productos", service.findAll());
        return "admin/lista";
    }

    @GetMapping("/nuevo")
    public String formularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", TipoCategoria.values());
        return "admin/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable String id, Model model) {
        model.addAttribute("producto", service.findById(id));
        model.addAttribute("categorias", TipoCategoria.values());
        return "admin/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Producto producto,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", TipoCategoria.values());
            return "admin/formulario";
        }
        service.save(producto);
        return "redirect:/admin/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id) {
        service.deleteById(id);
        return "redirect:/admin/productos";
    }
}