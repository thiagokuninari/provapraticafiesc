package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracaoAgendaRealRepository extends JpaRepository<ConfiguracaoAgendaReal, Integer>,
    ConfiguracaoAgendaRealRepositoryCustom {

    Optional<ConfiguracaoAgendaReal> findById(Integer id);

    boolean existsByNivelAndTipoConfiguracao(CodigoNivel nivel, ETipoConfiguracao tipoConfiguracao);

    boolean existsByCanalAndTipoConfiguracao(ECanal canal, ETipoConfiguracao tipoConfiguracao);

    boolean existsBySubcanalIdAndTipoConfiguracao(Integer subcanalId, ETipoConfiguracao tipoConfiguracao);

    boolean existsByEstruturaAaAndTipoConfiguracao(String estruturaAa, ETipoConfiguracao tipoConfiguracao);

    default boolean existsByNivel(CodigoNivel nivel) {
        return existsByNivelAndTipoConfiguracao(nivel, ETipoConfiguracao.NIVEL);
    }

    default boolean existsByCanal(ECanal canal) {
        return existsByCanalAndTipoConfiguracao(canal, ETipoConfiguracao.CANAL);
    }

    default boolean existsBySubcanalId(Integer subcanalId) {
        return existsBySubcanalIdAndTipoConfiguracao(subcanalId, ETipoConfiguracao.SUBCANAL);
    }

    default boolean existsByEstruturaAa(String estruturaAa) {
        return existsByEstruturaAaAndTipoConfiguracao(estruturaAa, ETipoConfiguracao.ESTRUTURA);
    }
}
