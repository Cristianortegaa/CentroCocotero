package srangeldev.centrococotero.producto.services;

import srangeldev.centrococotero.producto.models.Categoria;
import srangeldev.centrococotero.producto.models.TipoCategoria;

import java.util.List;

public interface CategoriaService {
    List<Categoria> getAllCategorias();
    Categoria getCategoriaById(String id);
    Categoria getCategoriaByTipo(TipoCategoria tipo);
    Categoria createCategoria(Categoria categoria);
    Categoria updateCategoria(String id, Categoria categoria);
    void deleteCategoria(String id);
}
