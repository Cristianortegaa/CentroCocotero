package srangeldev.centrococotero.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.services.UserService;
import srangeldev.centrococotero.storage.StorageService;

@Controller
public class LoginController {

    final UserService usuarioServicio;
    final StorageService storageService;

    @Autowired
    public LoginController(UserService usuarioServicio, StorageService storageService) {
        this.usuarioServicio = usuarioServicio;
        this.storageService = storageService;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "redirect:/";
    }

    @GetMapping("/auth/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "registered", required = false) String registered,
                       @RequestParam(value = "emailExists", required = false) String emailExists,
                       Model model) {
        // CSRF token is handled by GlobalControllerAdvice
        // Para el formulario de registro
        model.addAttribute("usuario", new Usuario());
        if (error != null) {
            model.addAttribute("loginError", error);
        }
        if (registered != null) {
            model.addAttribute("registroExitoso", "true");
        }
        if (emailExists != null) {
            model.addAttribute("emailExistente", "true");
        }
        return "login";
    }

    @PostMapping("/auth/register")
    public String register(@ModelAttribute Usuario usuario,
                           @RequestParam(value = "file", required = false) MultipartFile file) {
        
        System.out.println("=== REGISTRO DE USUARIO ===");
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Apellidos: " + usuario.getApellidos());
        System.out.println("Password (sin encriptar): " + usuario.getPassword());
        
        // Validar si el email ya existe
        if (usuarioServicio.buscarPorEmail(usuario.getEmail()) != null) {
            System.out.println("❌ Email ya existe en la base de datos");
            return "redirect:/auth/login?emailExists=true";
        }
        
        // Establecer valores por defecto
        usuario.setDeleted(false);
        
        if (file != null && !file.isEmpty()) {
            String imagen = storageService.store(file);
            usuario.setAvatar(MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "serveFile", imagen)
                    .build().toUriString());
        } else {
            // Avatar por defecto si no se sube imagen
            usuario.setAvatar("/images/logo.png");
        }

        Usuario usuarioGuardado = usuarioServicio.registrar(usuario);
        System.out.println("✅ Usuario registrado con ID: " + usuarioGuardado.getId());
        System.out.println("Password encriptado: " + usuarioGuardado.getPassword());
        System.out.println("Deleted: " + usuarioGuardado.getDeleted());
        
        return "redirect:/auth/login?registered=true";
    }
}