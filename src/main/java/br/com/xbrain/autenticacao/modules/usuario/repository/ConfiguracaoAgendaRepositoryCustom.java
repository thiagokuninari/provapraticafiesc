package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ConfiguracaoAgendaRepositoryCustom {

    Optional<Integer> findQtdHorasAdicionaisByCanal(ECanal canal);

    Optional<Integer> findQtdHorasAdicionaisByNivel(CodigoNivel nivel);

    Optional<Integer> findQtdHorasAdicionaisByEstruturaAa(String estruturaAa);

    Optional<Integer> findQtdHorasAdicionaisBySubcanal(ETipoCanal subcanal);

    Page<ConfiguracaoAgenda> findAllByPredicate(Predicate predicate, PageRequest pageable);
}
