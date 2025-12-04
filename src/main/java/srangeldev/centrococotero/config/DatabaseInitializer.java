package srangeldev.centrococotero.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import srangeldev.centrococotero.data.DataFactory;
import srangeldev.centrococotero.producto.models.Categoria;
import srangeldev.centrococotero.producto.models.Producto;
import srangeldev.centrococotero.producto.models.TipoCategoria;
import srangeldev.centrococotero.producto.repositories.CategoriaRepository;
import srangeldev.centrococotero.producto.repositories.ProductoRepository;
import srangeldev.centrococotero.usuario.models.Usuario;
import srangeldev.centrococotero.usuario.repositories.UsuarioRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public DatabaseInitializer(UsuarioRepository usuarioRepository, ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            log.info("Iniciando carga de datos de prueba...");

            // 1. Crear Categorías
            Map<TipoCategoria, Categoria> categorias = new HashMap<>();
            for (TipoCategoria tipo : TipoCategoria.values()) {
                Categoria categoria = Categoria.builder().tipo(tipo).build();
                categorias.put(tipo, categoriaRepository.save(categoria));
            }
            log.info("Categorías creadas: {}", categorias.size());

            // 2. Crear Usuarios
            List<Usuario> usuarios = DataFactory.createTestUsers();
            usuarioRepository.saveAll(usuarios);
            log.info("Usuarios creados: {}", usuarios.size());

            // 3. Crear Productos
            List<Producto> productos = DataFactory.createTestProducts(categorias);
            productoRepository.saveAll(productos);
            log.info("Productos creados: {}", productos.size());

            log.info("Carga de datos completada.");
        } else {
            log.info("La base de datos ya contiene datos. Se omite la carga inicial.");
        }
    }
}
