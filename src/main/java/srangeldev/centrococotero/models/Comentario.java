package srangeldev.centrococotero.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comentarios")
public class Comentario {

    @Id
    private String id;

    private String productoId;
    private String usuarioId;

    private String username;

    private String texto;
    private int puntuacion;

    private LocalDateTime fecha;
}