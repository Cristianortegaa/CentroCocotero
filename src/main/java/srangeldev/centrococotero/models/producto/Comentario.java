package srangeldev.centrococotero.models.producto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "comentarios")
public class Comentario {

    @Id
    private String id;

    private String productoId; // Referencia al Producto (Postgres)
    private String usuarioId;  // Referencia al Usuario (Postgres)

    // Guardamos el username aquí para no tener que buscarlo en Postgres
    // cada vez que mostramos los comentarios (Desnormalización aceptable en NoSQL)
    private String username;

    private String texto;
    private int puntuacion; // 1 a 5 estrellas

    private LocalDateTime fecha;
}