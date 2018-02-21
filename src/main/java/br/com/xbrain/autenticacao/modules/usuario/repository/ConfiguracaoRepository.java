package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Configuracao;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ConfiguracaoRepository extends PagingAndSortingRepository<Configuracao, Integer> {
}
