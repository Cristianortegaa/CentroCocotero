package srangeldev.centrococotero.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import srangeldev.centrococotero.models.Pedido;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.services.PedidoService;
import srangeldev.centrococotero.services.UserService;
import srangeldev.centrococotero.storage.StorageService;

import java.util.List;

@Controller
@RequestMapping("/app/perfil")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public String showProfile(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario user = userService.buscarPorEmail(email);
        
        // Obtener los pedidos del usuario
        List<Pedido> pedidos = pedidoService.buscarPorUsuario(user.getId());
        
        model.addAttribute("usuario", user);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("currentYear", java.time.Year.now().getValue());
        
        return "app/perfil/perfil";
    }

    @PostMapping("/editar")
    public String updateProfile(@RequestParam("nombre") String nombre,
                                @RequestParam("apellidos") String apellidos,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                RedirectAttributes redirectAttributes) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario existingUser = userService.buscarPorEmail(email);

        // Update only allowed fields
        existingUser.setNombre(nombre);
        existingUser.setApellidos(apellidos);

        // Handle avatar upload - ONLY process if a new file is uploaded
        if (file != null && !file.isEmpty()) {
            // Delete old avatar if exists and is NOT an external URL
            if (existingUser.getAvatar() != null && !existingUser.getAvatar().isEmpty()
                    && !isExternalUrl(existingUser.getAvatar())) {
                storageService.delete(existingUser.getAvatar());
            }
            // Upload new avatar
            String avatar = storageService.store(file);
            existingUser.setAvatar(MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "serveFile", avatar).build().toUriString());
        }

        userService.editar(existingUser);
        
        // Actualizar el contexto de seguridad con los datos actualizados del usuario
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
            existingUser,
            existingUser.getPassword(),
            existingUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        
        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");

        return "redirect:/app/perfil";
    }


    private boolean isExternalUrl(String path) {
        return path != null && (path.startsWith("http://") || path.startsWith("https://"));
    }
}
