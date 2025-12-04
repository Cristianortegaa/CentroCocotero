package srangeldev.centrococotero.producto.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import srangeldev.centrococotero.controllers.FilesController;
import srangeldev.centrococotero.producto.models.Producto;
import srangeldev.centrococotero.producto.services.CategoriaService;
import srangeldev.centrococotero.producto.services.ProductoService;
import srangeldev.centrococotero.storage.StorageService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/app/productos")
@Slf4j
public class ProductoController {

    private final ProductoService productoService;
    private final StorageService storageService;
    private final CategoriaService categoriaService;

    @Autowired
    public ProductoController(ProductoService productoService, StorageService storageService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.storageService = storageService;
        this.categoriaService = categoriaService;
    }

    // --- LISTADO DE PRODUCTOS (ADMIN/VENDEDOR) ---
    @GetMapping
    public String list(Model model,
                       @RequestParam(name = "q", required = false) String query,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Producto> productosPage;

        if (query != null && !query.isEmpty()) {
            productosPage = productoService.findByNombreActive(query, pageable);
            model.addAttribute("query", query);
        } else {
            productosPage = productoService.findAllActive(pageable);
        }

        model.addAttribute("productosPage", productosPage);
        return "app/producto/lista";
    }

    // --- FORMULARIO NUEVO PRODUCTO ---
    @GetMapping("/nuevo")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.getAllCategorias());
        return "app/producto/ficha";
    }

    // --- GUARDAR NUEVO PRODUCTO ---
    @PostMapping("/nuevo/submit")
    public String nuevoProductoSubmit(@Valid @ModelAttribute Producto producto,
                                      BindingResult bindingResult,
                                      @RequestParam("file") MultipartFile file,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaService.getAllCategorias());
            return "app/producto/ficha";
        }

        try {
            if (!file.isEmpty()) {
                String imagen = storageService.store(file);
                String imageUrl = MvcUriComponentsBuilder
                        .fromMethodName(FilesController.class, "serveFile", imagen).build().toUriString();
                // Asumiendo que Producto tiene una lista de imágenes, añadimos la primera
                producto.setImagenes(new ArrayList<>(List.of(imageUrl)));
            } else {
                // Imagen por defecto si no se sube nada
                producto.setImagenes(new ArrayList<>(List.of("https://via.placeholder.com/150")));
            }
            
            productoService.createProducto(producto);
            redirectAttributes.addFlashAttribute("success", "Producto creado correctamente.");
            return "redirect:/app/productos";
            
        } catch (Exception e) {
            log.error("Error al crear producto: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al crear el producto: " + e.getMessage());
            return "redirect:/app/productos/nuevo";
        }
    }

    // --- FORMULARIO EDITAR PRODUCTO ---
    @GetMapping("/editar/{id}")
    public String editarProductoForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.getProductoById(id);
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.getAllCategorias());
            return "app/producto/ficha";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/app/productos";
        }
    }

    // --- GUARDAR EDICIÓN PRODUCTO ---
    @PostMapping("/editar/submit")
    public String editarProductoSubmit(@Valid @ModelAttribute("producto") Producto producto,
                                       BindingResult bindingResult,
                                       @RequestParam("file") MultipartFile file,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {

        if (bindingResult.hasErrors()) {
            // model.addAttribute("categorias", categoriaService.findAll());
            return "app/producto/ficha";
        }

        try {
            // Recuperamos el producto original para mantener datos como la imagen si no se cambia
            Producto productoOriginal = productoService.getProductoById(producto.getId());
            
            if (!file.isEmpty()) {
                // Borrar imagen anterior si es necesario (si es local)
                if (productoOriginal.getImagenes() != null && !productoOriginal.getImagenes().isEmpty()) {
                    String oldUrl = productoOriginal.getImagenes().get(0);
                    if (oldUrl != null && !oldUrl.isEmpty() && oldUrl.contains("/files/")) {
                        String oldFilename = oldUrl.substring(oldUrl.lastIndexOf("/") + 1);
                        storageService.delete(oldFilename);
                    }
                }
                
                // Subir nueva imagen
                String imagen = storageService.store(file);
                String imageUrl = MvcUriComponentsBuilder
                        .fromMethodName(FilesController.class, "serveFile", imagen).build().toUriString();
                producto.setImagenes(new ArrayList<>(List.of(imageUrl)));
            } else {
                // Mantener imagen original
                producto.setImagenes(productoOriginal.getImagenes());
            }

            productoService.updateProducto(producto.getId(), producto);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado correctamente.");
            return "redirect:/app/productos";

        } catch (Exception e) {
            log.error("Error al actualizar producto: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el producto: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
            return "redirect:/app/productos/editar/" + producto.getId();
        }
    }

    // --- VER DETALLES DEL PRODUCTO ---
    @GetMapping("/detalle/{id}")
    public String verDetalleProducto(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.getProductoById(id);
            List<Producto> productosRelacionados = productoService.findRelatedProducts(id, 4);
            model.addAttribute("producto", producto);
            model.addAttribute("productosRelacionados", productosRelacionados);
            return "app/producto/detalle";
        } catch (Exception e) {
            log.error("Error al obtener producto: ", e);
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/app/productos";
        }
    }

    // --- ELIMINAR PRODUCTO (BORRADO LÓGICO) ---
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            productoService.deleteProducto(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente.");
        } catch (Exception e) {
            log.error("Error al eliminar producto: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto.");
        }
        return "redirect:/app/productos";
    }
}
