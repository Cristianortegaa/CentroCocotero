package srangeldev.centrococotero.models.carrito;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import srangeldev.centrococotero.utils.Utils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "carrito")
public class Carrito {

    @Id
    private String id = Utils.generadorId(); // Nuestro generador de ID propio

    @Indexed(unique = true)
    private String usuarioId; // Referencia al Usuario de Postgres

    private List<ItemCarrito> items = new ArrayList<>();

    private BigDecimal total = BigDecimal.ZERO;

    private LocalDateTime ultimaActualizacion = LocalDateTime.now();
}