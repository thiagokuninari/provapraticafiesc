package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MotivoInativacaoRepository extends PagingAndSortingRepository<MotivoInativacao, Integer> {
    Iterable<MotivoInativacao> findBySituacao(ESituacao situacao);
}