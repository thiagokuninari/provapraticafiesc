package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ConfiguracaoAgendaRepository extends JpaRepository<ConfiguracaoAgenda, Integer>,
    ConfiguracaoAgendaRepositoryCustom {
    Optional<ConfiguracaoAgenda> findFirstByCanalAndSituacaoOrderByQtdHorasAdicionaisDesc(ECanal canal, ESituacao situacao);

    Optional<ConfiguracaoAgenda> findFirstByEstruturaAaAndSituacaoOrderByQtdHorasAdicionaisDesc(String estruturaAa, ESituacao situacao);

    Optional<ConfiguracaoAgenda> findFirstByNivelAndSituacaoOrderByQtdHorasAdicionaisDesc(CodigoNivel nivel, ESituacao situacao);

    Optional<ConfiguracaoAgenda> findFirstBySubcanalAndSituacaoOrderByQtdHorasAdicionaisDesc(ETipoCanal subcanal, ESituacao situacao);

    Optional<ConfiguracaoAgenda> findById(Integer id);
}
