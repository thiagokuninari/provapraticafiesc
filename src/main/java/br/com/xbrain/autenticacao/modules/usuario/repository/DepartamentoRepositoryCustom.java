package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartamentoRepositoryCustom {

    Page<Departamento> findAll(Predicate predicate, Pageable pageable);

    List<Departamento> findAll(Predicate predicate);

    @Cacheable("cargoFindBySituacaoAndNivelId")
    Iterable<Departamento> findBySituacaoAndNivelId(ESituacao situacao, Integer nivelId);

}
