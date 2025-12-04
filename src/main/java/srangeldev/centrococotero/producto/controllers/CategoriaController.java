package srangeldev.centrococotero.producto.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import srangeldev.centrococotero.producto.models.Categoria;
import srangeldev.centrococotero.producto.models.TipoCategoria;
import srangeldev.centrococotero.producto.services.CategoriaService;

import java.util.List;

@Controller
@RequestMapping("/app/categorias")
@Slf4j
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // --- LISTADO DE CATEGORÍAS ---
    @GetMapping
    public String list(Model model) {
        try {
            List<Categoria> categorias = categoriaService.getAllCategorias();
            model.addAttribute("categorias", categorias);
            return "app/categoria/lista";
        } catch (Exception e) {
            log.error("Error al listar categorías: ", e);
            model.addAttribute("error", "Error al cargar las categorías");
            return "app/categoria/lista";
        }
    }

    // --- FORMULARIO NUEVA CATEGORÍA ---
    @GetMapping("/nueva")
    public String nuevaCategoriaForm(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("tiposCategoria", TipoCategoria.values());
        return "app/categoria/ficha";
    }

    // --- GUARDAR NUEVA CATEGORÍA ---
    @PostMapping("/nueva/submit")
    public String nuevaCategoriaSubmit(@Valid @ModelAttribute Categoria categoria,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tiposCategoria", TipoCategoria.values());
            return "app/categoria/ficha";
        }

        try {
            categoriaService.createCategoria(categoria);
            redirectAttributes.addFlashAttribute("success", "Categoría creada correctamente.");
            return "redirect:/app/categorias";
        } catch (Exception e) {
            log.error("Error al crear categoría: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al crear la categoría: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
            return "redirect:/app/categorias/nueva";
        }
    }

    // --- FORMULARIO EDITAR CATEGORÍA ---
    @GetMapping("/editar/{id}")
    public String editarCategoriaForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Categoria categoria = categoriaService.getCategoriaById(id);
            model.addAttribute("categoria", categoria);
            model.addAttribute("tiposCategoria", TipoCategoria.values());
            return "app/categoria/ficha";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Categoría no encontrada.");
            return "redirect:/app/categorias";
        }
    }

    // --- GUARDAR EDICIÓN CATEGORÍA ---
    @PostMapping("/editar/submit")
    public String editarCategoriaSubmit(@Valid @ModelAttribute Categoria categoria,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tiposCategoria", TipoCategoria.values());
            return "app/categoria/ficha";
        }

        try {
            categoriaService.updateCategoria(categoria.getId(), categoria);
            redirectAttributes.addFlashAttribute("success", "Categoría actualizada correctamente.");
            return "redirect:/app/categorias";
        } catch (Exception e) {
            log.error("Error al actualizar categoría: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la categoría: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
            return "redirect:/app/categorias/editar/" + categoria.getId();
        }
    }

    // --- ELIMINAR CATEGORÍA ---
    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.deleteCategoria(id);
            redirectAttributes.addFlashAttribute("success", "Categoría eliminada correctamente.");
        } catch (Exception e) {
            log.error("Error al eliminar categoría: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la categoría. Puede que tenga productos asociados.");
        }
        return "redirect:/app/categorias";
    }
}
