package srangeldev.centrococotero.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import srangeldev.centrococotero.models.Favorito;
import srangeldev.centrococotero.models.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalDataController {

    @ModelAttribute("favoritos")
    public List<Favorito> cargarFavoritosMock() {

        // Producto 1: Usando tu logo local
        Producto p1 = Producto.builder()
                .id("1")
                .nombre("Aceite de Coco Virgen Extra")
                .precio(new BigDecimal("15.99"))
                .imagenes(List.of("/images/logo.png")) // <--- IMAGEN CAMBIADA
                .build();

        // Producto 2: Lista vacía (Para probar que tu HTML pone el logo por defecto si no hay foto)
        Producto p2 = Producto.builder()
                .id("2")
                .nombre("Pack 3 Cocos Frescos")
                .precio(new BigDecimal("12.50"))
                .imagenes(new ArrayList<>())
                .build();

        // Producto 3: Usando tu logo local
        Producto p3 = Producto.builder()
                .id("3")
                .nombre("Crema Hidratante de Coco (Edición Limitada)")
                .precio(new BigDecimal("24.00"))
                .imagenes(List.of("/images/logo.png")) // <--- IMAGEN CAMBIADA
                .build();

        List<Favorito> listaFalsa = new ArrayList<>();
        listaFalsa.add(Favorito.builder().producto(p1).build());
        listaFalsa.add(Favorito.builder().producto(p2).build());
        listaFalsa.add(Favorito.builder().producto(p3).build());

        return listaFalsa;
    }
}