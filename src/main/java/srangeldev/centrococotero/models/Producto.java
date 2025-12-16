package srangeldev.centrococotero.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "productos")
public class Producto {

    @Id
    @GenericGenerator(
            name = "youtube_id",
            strategy = "srangeldev.centrococotero.utils.Utils")
    @GeneratedValue(generator = "youtube_id")
    private String id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String nombre;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 2 decimales")
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private TipoCategoria categoria;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    // IMÁGENES
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producto_imagenes", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "url_imagen")
    private List<String> imagenes;

    // Borrado lógico
    @Builder.Default
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        if (this.stock == null) {
            this.stock = 0;
        }
        if (this.deleted == null) {
            this.deleted = false;
        }
    }
}