package srangeldev.centrococotero.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "favoritos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "producto_id"})
})
public class Favorito {
    @Id
    @Column(length = 11)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    private LocalDateTime fechaAgregado;

    @PrePersist
    protected void onCreate() {
        if (this.fechaAgregado == null) this.fechaAgregado = LocalDateTime.now();
    }
}