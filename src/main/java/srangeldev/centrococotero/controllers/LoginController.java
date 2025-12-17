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

    @GetMapping("/")
    public String welcome() {
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/login")
    public String login(Model model) {
        // CSRF token is handled by GlobalControllerAdvice
        // Para el formulario de registro
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    @PostMapping("/auth/register")
    public String register(@ModelAttribute Usuario usuario,
                           @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            usuario.setAvatar(MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "serveFile", imagen)
                    .build().toUriString());
        }

        usuarioServicio.registrar(usuario);
        return "redirect:/auth/login";
    }
}