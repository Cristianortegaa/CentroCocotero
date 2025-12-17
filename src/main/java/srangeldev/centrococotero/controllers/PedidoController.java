package srangeldev.centrococotero.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import srangeldev.centrococotero.models.Pedido;
import srangeldev.centrococotero.models.Usuario;
import srangeldev.centrococotero.repositories.UserRepository;
import srangeldev.centrococotero.services.PedidoService;

import java.util.Optional;

@Controller
@RequestMapping("/app/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable String id,
                             Authentication authentication,
                             Model model) {

        Usuario usuario = userRepository.findFirstByEmail(authentication.getName());
        if (usuario == null) {
            return "redirect:/login";
        }

        Optional<Pedido> pedidoOpt = pedidoService.buscarPorIdYUsuario(id, usuario.getId());

        if (pedidoOpt.isEmpty()) {
            return "redirect:/app/perfil";
        }

        model.addAttribute("pedido", pedidoOpt.get());
        return "app/pedido/detalle";
    }
}
