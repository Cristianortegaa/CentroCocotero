package srangeldev.centrococotero.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items_carrito",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "producto_id"}))
public class ItemCarrito {

    @Id
    @Column(length = 11)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    // Precio unitario al momento de añadir al carrito
    @NotNull(message = "El precio unitario es obligatorio")
    private BigDecimal precioUnitario;


}