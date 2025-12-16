package srangeldev.centrococotero.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import srangeldev.centrococotero.models.Producto;
import srangeldev.centrococotero.models.TipoCategoria;
import srangeldev.centrococotero.repositories.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public void run(String... args) throws Exception {

        if (productoRepository.count() == 0) {
            System.out.println("🥥 Iniciando carga de productos de prueba...");

            productoRepository.saveAll(List.of(
                    // PRODUCTO 1: Bebida
                    Producto.builder()
                            .nombre("Coco Fresco Premium")
                            .descripcion("Coco recién caído de la palmera, lleno de agua refrescante y electrolitos naturales. Perfecto para hidratarse en verano.")
                            .precio(new BigDecimal("4.50"))
                            .stock(50)
                            .categoria(TipoCategoria.BEBIDAS)
                            .imagenes(List.of("/images/logo.png"))
                            .build(),

                    // PRODUCTO 2: Aceite
                    Producto.builder()
                            .nombre("Aceite de Coco Virgen Extra")
                            .descripcion("Ideal para cocinar, hidratar la piel o cuidar tu cabello. 100% orgánico y prensado en frío.")
                            .precio(new BigDecimal("12.99"))
                            .stock(30)
                            .categoria(TipoCategoria.ACEITES_DERIVADOS)
                            .imagenes(List.of("/images/logo.png"))
                            .build(),

                    // PRODUCTO 3: Fruta
                    Producto.builder()
                            .nombre("Pack Piña Gold Hawaiana")
                            .descripcion("Tres piñas dulces y jugosas traídas directamente de islas volcánicas. Sabor intenso garantizado.")
                            .precio(new BigDecimal("8.75"))
                            .stock(20)
                            .categoria(TipoCategoria.ALIMENTOS_TROPICALES)
                            .imagenes(List.of("/images/logo.png"))
                            .build(),

                    // PRODUCTO 4: Cosmética
                    Producto.builder()
                            .nombre("Jabón Artesanal de Coco y Vainilla")
                            .descripcion("Suavidad extrema para tu piel con aroma relajante tropical. Hecho a mano sin parabenos.")
                            .precio(new BigDecimal("6.20"))
                            .stock(100)
                            .categoria(TipoCategoria.COSMETICA_NATURAL)
                            .imagenes(List.of("/images/logo.png"))
                            .build(),

                    // PRODUCTO 5: Ropa
                    Producto.builder()
                            .nombre("Camisa Hawaiana 'Sunset'")
                            .descripcion("Estilo y frescura para el verano. Tela transpirable y diseño único de palmeras al atardecer.")
                            .precio(new BigDecimal("25.00"))
                            .stock(15)
                            .categoria(TipoCategoria.TEXTIL_VERANO)
                            .imagenes(List.of("/images/logo.png"))
                            .build()
            ));

            System.out.println("¡PRODUCTOS CARGADOS EN LA BASE DE DATOS!");
        } else {
            System.out.println("La base de datos ya tiene productos. No se han creado nuevos.");
        }
    }
}