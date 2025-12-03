package srangeldev.centrococotero.models.producto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import srangeldev.centrococotero.utils.Utils;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Producto {

    @Id
    @Column(length = 11)
    private String id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String nombre;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    // La sacamos fuera por normalización, más óptimo para la Base de Datos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // IMÁGENES
    @ElementCollection
    @CollectionTable(name = "producto_imagenes", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "url_imagen")
    private List<String> imagenes;

    // Con esto evitamos que se compren 2 producto a la vez, a nivel de JPA
    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = Utils.generadorId();
        }
        if (this.stock == null) {
            this.stock = 0; // Evitamos NullPointerException en lógica de negocio
        }
    }
}