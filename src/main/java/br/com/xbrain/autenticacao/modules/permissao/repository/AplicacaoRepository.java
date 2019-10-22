package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AplicacaoRepository extends PagingAndSortingRepository<Aplicacao, Integer> {
}
