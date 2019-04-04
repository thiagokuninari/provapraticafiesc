package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UsuarioHistoricoRepository
        extends PagingAndSortingRepository<UsuarioHistorico, Integer>, UsuarioHistoricoRepositoryCustom {

    List<UsuarioHistorico> findByUsuarioId(Integer usuarioid);
}