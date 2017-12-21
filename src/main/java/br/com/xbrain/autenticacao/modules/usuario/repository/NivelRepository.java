package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NivelRepository extends PagingAndSortingRepository<Nivel, Integer> {
    Iterable<Nivel> findBySituacao(ESituacao situacao);
}