package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartamentoRepositoryCustom {

    Page<Departamento> findAll(Predicate predicate, Pageable pageable);

    @Cacheable("cargoFindBySituacaoAndNivelId")
    Iterable<Departamento> findBySituacaoAndNivelId(ESituacao situacao, Integer nivelId);

}
