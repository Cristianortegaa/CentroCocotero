package srangeldev.centrococotero.controllers;

import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;

    // Inyectamos el repo de usuarios para saber quién da like
    private final UserRepository usuarioRepository;

    @GetMapping("/public")
    public String inicio(Model model,
                         @RequestParam(required = false) String busqueda,
                         @RequestParam(required = false) TipoCategoria categoria,
                         @RequestParam(required = false) java.math.BigDecimal precioMin,
                         @RequestParam(required = false) java.math.BigDecimal precioMax,
                         // CAMBIO: Usamos 'cantidad' en vez de 'page'. Por defecto 8.
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

        // 2. CORTAR LA LISTA (Aquí está el truco sencillo)
        // Si hay 20 productos y pides 8, cortamos en 8.
        // Si pides 16, cortamos en 16.
        int total = productos.size();
        boolean hayMas = total > cantidad; // ¿Quedan productos ocultos?

        List<Producto> productosParaMostrar = productos.stream()
                .limit(cantidad) // Solo mostramos la cantidad que nos piden
                .toList();

        // 3. ENVIAR A LA VISTA
        model.addAttribute("productos", productosParaMostrar);

        // Guardamos los filtros para no perderlos al recargar
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);
        model.addAttribute("categorias", TipoCategoria.values());

        // Variables para el botón
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
        // Ajusta esto si ya tienes comentarios reales funcionando
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

    // FAVORITOS
    // FAVORITOS
    @PostMapping("/favoritos/toggle/{id}")
    public String toggleFavorito(@PathVariable("id") String productoId,
                                 Authentication auth, // Esto vendrá null si no hay seguridad
                                 HttpServletRequest request) {

        Usuario usuario;

        // 1. INTENTAMOS OBTENER USUARIO LOGUEADO
        if (auth != null && auth.isAuthenticated()) {
            usuario = usuarioRepository.findFirstByEmail(auth.getName());
        } else {
            // 2. MODO "SIN SEGURIDAD": Usamos un usuario de prueba
            // Busca un usuario por defecto o crea uno al vuelo para pruebas
            String emailPrueba = "user@prueba.com";
            usuario = usuarioRepository.findFirstByEmail(emailPrueba);

            // Si no existe el de prueba, créalo temporalmente (Opcional, solo para dev)
            if (usuario == null) {
                System.out.println("⚠️ Creando usuario temporal para pruebas...");
                usuario = Usuario.builder()
                        .email(emailPrueba)
                        .nombre("Usuario Test")
                        .password("1234") // Da igual ahora
                        // .roles(...) // si tienes roles
                        .build();
                usuarioRepository.save(usuario);
            }
        }

        // 3. Ejecutar la lógica
        if (usuario != null) {
            service.toggleFavorito(productoId, usuario);
        }

        // 4. Redirigir a la misma página donde estaba (Referer)
        // Esto es mejor que redirigir siempre a /producto/{id} porque si le das like
        // desde el HOME, quieres quedarte en el HOME.
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}