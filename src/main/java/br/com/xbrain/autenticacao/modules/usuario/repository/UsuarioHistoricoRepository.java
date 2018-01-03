package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UsuarioHistoricoRepository extends PagingAndSortingRepository<UsuarioHistorico, Integer> {
}