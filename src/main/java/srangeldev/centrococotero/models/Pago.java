package srangeldev.centrococotero.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un pago/transacción en el sistema
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pagos")
public class Pago {

    @Id
    @Column(length = 11)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "La cantidad es obligatorio")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false)
    private BigDecimal cantidad;

    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    // Referencia externa del proveedor de pagos (Stripe, PayPal, etc.)
    private String transaccionExternaId;

    // Últimos 4 dígitos de la tarjeta (para referencia)
    @Size(max = 4)
    private String ultimosDigitosTarjeta;

    // Fecha de creación del pago
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaPago;

    // Fecha de última actualización del estado
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.fechaPago == null) this.fechaPago = LocalDateTime.now();
        if (this.estado == null) this.estado = EstadoPago.PENDIENTE;
    }
}
