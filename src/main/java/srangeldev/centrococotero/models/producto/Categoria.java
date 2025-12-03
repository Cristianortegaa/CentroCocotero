package srangeldev.centrococotero.models.producto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import srangeldev.centrococotero.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Categoria {

    @Id
    @Column(length = 11)
    private String id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(unique = true, nullable = false)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    // Soporte para subcategorías
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_padre_id")
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Categoria> subcategorias = new ArrayList<>();

    @Column(name = "url_imagen")
    private String imagen;

    // La misma lógica que en Producto
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = Utils.generadorId();
        }
    }

    //Si queremos saber qué producto tiene esta categoría desde aquí (dejar comentado de momento)
    // @OneToMany(mappedBy = "categoria")
    // private List<Producto> producto;
}
