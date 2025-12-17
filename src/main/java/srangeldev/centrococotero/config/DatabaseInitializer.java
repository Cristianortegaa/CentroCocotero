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

        // 1. ¡IMPORTANTE! BORRAMOS TODO LO ANTIGUO PARA EMPEZAR LIMPIO
        System.out.println("🧹 Borrando productos antiguos...");
        productoRepository.deleteAll();

        // 2. AHORA SÍ, CARGAMOS LOS 20 NUEVOS
        System.out.println("🥥 Iniciando carga masiva de productos (20 items)...");

        productoRepository.saveAll(List.of(
                // --- BEBIDAS ---
                Producto.builder()
                        .nombre("Coco Fresco Premium")
                        .descripcion("Coco recién caído de la palmera, lleno de agua refrescante y electrolitos naturales.")
                        .precio(new BigDecimal("4.50"))
                        .stock(50)
                        .categoria(TipoCategoria.BEBIDAS)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Leche de Coco Orgánica")
                        .descripcion("Leche vegetal cremosa, ideal para currys, batidos o café. Sin azúcares añadidos.")
                        .precio(new BigDecimal("3.20"))
                        .stock(40)
                        .categoria(TipoCategoria.BEBIDAS)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Agua de Coco con Piña")
                        .descripcion("La combinación perfecta de hidratación y sabor tropical. Pack de 1 Litro.")
                        .precio(new BigDecimal("2.99"))
                        .stock(60)
                        .categoria(TipoCategoria.BEBIDAS)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Licor de Coco Suave")
                        .descripcion("Bebida espirituosa dulce con esencia de coco caribeño. Perfecto para cócteles.")
                        .precio(new BigDecimal("14.50"))
                        .stock(15)
                        .categoria(TipoCategoria.BEBIDAS)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),

                // --- ACEITES Y DERIVADOS ---
                Producto.builder()
                        .nombre("Aceite de Coco Virgen Extra")
                        .descripcion("Ideal para cocinar o hidratar la piel. 100% orgánico y prensado en frío.")
                        .precio(new BigDecimal("12.99"))
                        .stock(30)
                        .categoria(TipoCategoria.ACEITES_DERIVADOS)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Harina de Coco Keto")
                        .descripcion("Alternativa sin gluten rica en fibra. Perfecta para repostería saludable.")
                        .precio(new BigDecimal("6.50"))
                        .stock(25)
                        .categoria(TipoCategoria.ACEITES_DERIVADOS)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Azúcar de Flor de Coco")
                        .descripcion("Endulzante natural de bajo índice glucémico con sabor a caramelo.")
                        .precio(new BigDecimal("5.80"))
                        .stock(20)
                        .categoria(TipoCategoria.ACEITES_DERIVADOS)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Chips de Coco Tostados")
                        .descripcion("Snack crujiente y saludable. Ideal para picar entre horas o añadir al yogur.")
                        .precio(new BigDecimal("2.50"))
                        .stock(80)
                        .categoria(TipoCategoria.ACEITES_DERIVADOS)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),

                // --- ALIMENTOS TROPICALES ---
                Producto.builder()
                        .nombre("Pack Piña Gold")
                        .descripcion("Tres piñas dulces y jugosas traídas directamente de islas volcánicas.")
                        .precio(new BigDecimal("8.75"))
                        .stock(20)
                        .categoria(TipoCategoria.ALIMENTOS_TROPICALES)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Mango Maduro Importado")
                        .descripcion("Mango de gran calibre, sin hebras y con una dulzura excepcional.")
                        .precio(new BigDecimal("3.99"))
                        .stock(35)
                        .categoria(TipoCategoria.ALIMENTOS_TROPICALES)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Caja de Papayas")
                        .descripcion("Caja de 2kg de papayas frescas. Ricas en vitaminas y digestivas.")
                        .precio(new BigDecimal("11.00"))
                        .stock(10)
                        .categoria(TipoCategoria.ALIMENTOS_TROPICALES)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Plátano Macho (Kg)")
                        .descripcion("El rey de la cocina tropical. Ideal para freír o hacer tostones.")
                        .precio(new BigDecimal("1.80"))
                        .stock(50)
                        .categoria(TipoCategoria.ALIMENTOS_TROPICALES)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),

                // --- COSMÉTICA NATURAL ---
                Producto.builder()
                        .nombre("Jabón Artesanal de Coco")
                        .descripcion("Suavidad extrema para tu piel con aroma relajante. Hecho a mano.")
                        .precio(new BigDecimal("6.20"))
                        .stock(100)
                        .categoria(TipoCategoria.COSMETICA_NATURAL)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Exfoliante Corporal Café y Coco")
                        .descripcion("Elimina células muertas y deja la piel radiante y suave.")
                        .precio(new BigDecimal("15.00"))
                        .stock(25)
                        .categoria(TipoCategoria.COSMETICA_NATURAL)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Mascarilla Capilar Reparadora")
                        .descripcion("Tratamiento intensivo a base de aceite de coco para cabello dañado.")
                        .precio(new BigDecimal("18.90"))
                        .stock(15)
                        .categoria(TipoCategoria.COSMETICA_NATURAL)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Bálsamo Labial Tropical")
                        .descripcion("Hidratación profunda para tus labios con sabor a coco y vainilla.")
                        .precio(new BigDecimal("3.50"))
                        .stock(200)
                        .categoria(TipoCategoria.COSMETICA_NATURAL)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),

                // --- TEXTIL VERANO ---
                Producto.builder()
                        .nombre("Camisa Hawaiana 'Sunset'")
                        .descripcion("Estilo y frescura. Tela transpirable y diseño único de palmeras.")
                        .precio(new BigDecimal("25.00"))
                        .stock(15)
                        .categoria(TipoCategoria.TEXTIL_VERANO)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Sombrero de Paja")
                        .descripcion("Protégete del sol con estilo. Ala ancha y cinta decorativa.")
                        .precio(new BigDecimal("12.00"))
                        .stock(40)
                        .categoria(TipoCategoria.TEXTIL_VERANO)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Pareo Playa Palmeras")
                        .descripcion("Pareo ligero y versátil, sirve como vestido o toalla de playa.")
                        .precio(new BigDecimal("9.99"))
                        .stock(60)
                        .categoria(TipoCategoria.TEXTIL_VERANO)
                        .imagenes(List.of("/images/logo.png"))
                        .build(),
                Producto.builder()
                        .nombre("Bañador Hombre Tropical")
                        .descripcion("Secado rápido y estampado colorido de cocos y piñas.")
                        .precio(new BigDecimal("19.50"))
                        .stock(20)
                        .categoria(TipoCategoria.TEXTIL_VERANO)
                        .imagenes(List.of("/images/logo.png"))
                        .build()
        ));

        System.out.println("✅ ¡CARGA COMPLETADA! 20 Productos disponibles.");
    }
}