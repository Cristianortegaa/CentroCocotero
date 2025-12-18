package srangeldev.centrococotero.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srangeldev.centrococotero.models.Pedido;
import srangeldev.centrococotero.repositories.PedidoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de pedidos
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Pedido> buscarPorUsuarioPaginado(Long usuarioId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return pedidoRepository.findByUsuarioId(usuarioId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(String id) {
        return pedidoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorIdYUsuario(String id, Long usuarioId) {
        return pedidoRepository.findByIdAndUsuarioId(id, usuarioId);
    }

    @Override
    @Transactional
    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorUsuario(Long usuarioId) {
        return pedidoRepository.countByUsuarioId(usuarioId);
    }
}
