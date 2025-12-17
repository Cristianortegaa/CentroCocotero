package srangeldev.centrococotero.models;

public enum TipoCategoria {
    ALIMENTOS_TROPICALES("Alimentos y Snacks Tropicales"),
    BEBIDAS("Bebidas Tropicales"),
    COSMETICA_NATURAL("Cosmética Natural"),
    ACEITES_DERIVADOS("Aceites y Derivados de Coco"),
    ACCESORIOS_PLAYA("Accesorios de Playa"),
    DECORACION_TROPICAL("Decoración Tropical"),
    TEXTIL_VERANO("Textil y Verano"),
    BIENESTAR("Bienestar y Aromas"),
    ARTESANIA("Artesanía Tropical"),
    OTROS("Otros");

    private final String descripcion;

    TipoCategoria(String descripcion) {
        this.descripcion = descripcion;
    }
}
