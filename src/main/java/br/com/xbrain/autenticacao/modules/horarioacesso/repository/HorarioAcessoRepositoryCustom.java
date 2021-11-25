package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface HorarioAcessoRepositoryCustom {

    PageImpl<HorarioAcessoResponse> findAll(Pageable pageable, Predicate predicate);

    HorarioAcessoResponse findById(Integer id);
}
