package srangeldev.centrococotero.producto.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srangeldev.centrococotero.producto.exceptions.ProductoException;
import srangeldev.centrococotero.producto.models.Categoria;
import srangeldev.centrococotero.producto.models.TipoCategoria;
import srangeldev.centrococotero.producto.repositories.CategoriaRepository;

import java.util.List;

@Service
@Slf4j
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Categoria> getAllCategorias() {
        try {
            return categoriaRepository.findAll();
        } catch (Exception e) {
            log.error("Error al obtener todas las categorías: ", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new ProductoException("Error al obtener todas las categorías: " + errorMsg, e);
        }
    }

    @Override
    public Categoria getCategoriaById(String id) {
        try {
            return categoriaRepository.findById(id)
                    .orElseThrow(() -> new ProductoException("Categoría no encontrada con ID: " + id));
        } catch (ProductoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al obtener la categoría por ID: ", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new ProductoException("Error al obtener la categoría por ID: " + errorMsg, e);
        }
    }

    @Override
    public Categoria getCategoriaByTipo(TipoCategoria tipo) {
        try {
            return categoriaRepository.findByTipo(tipo)
                    .orElseThrow(() -> new ProductoException("Categoría no encontrada con tipo: " + tipo));
        } catch (ProductoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al obtener la categoría por tipo: ", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new ProductoException("Error al obtener la categoría por tipo: " + errorMsg, e);
        }
    }

    @Override
    @Transactional
    public Categoria createCategoria(Categoria categoria) {
        try {
            return categoriaRepository.save(categoria);
        } catch (Exception e) {
            log.error("Error al crear la categoría: ", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new ProductoException("Error al crear la categoría: " + errorMsg, e);
        }
    }

    @Override
    @Transactional
    public Categoria updateCategoria(String id, Categoria categoriaDetails) {
        try {
            Categoria categoria = getCategoriaById(id);
            categoria.setTipo(categoriaDetails.getTipo());
            return categoriaRepository.save(categoria);
        } catch (Exception e) {
            log.error("Error al actualizar la categoría: ", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new ProductoException("Error al actualizar la categoría: " + errorMsg, e);
        }
    }

    @Override
    @Transactional
    public void deleteCategoria(String id) {
        try {
            Categoria categoria = getCategoriaById(id);
            categoriaRepository.delete(categoria);
        } catch (Exception e) {
            log.error("Error al eliminar la categoría: ", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new ProductoException("Error al eliminar la categoría: " + errorMsg, e);
        }
    }
}
