package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ConfiguracaoAgendaRealRepositoryCustom {

    Optional<Integer> findQtdHorasAdicionaisByCanal(ECanal canal);

    Optional<Integer> findQtdHorasAdicionaisByNivel(CodigoNivel nivel);

    Optional<Integer> findQtdHorasAdicionaisByEstruturaAa(String estruturaAa);

    Optional<Integer> findQtdHorasAdicionaisBySubcanal(Integer subcanalId);

    Page<ConfiguracaoAgendaReal> findAllByPredicate(Predicate predicate, PageRequest pageable);

    Integer getQtdHorasPadrao();

    boolean existeConfiguracaoPadrao();
}
