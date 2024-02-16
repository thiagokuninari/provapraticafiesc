package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracaoAgendaRealRepository extends JpaRepository<ConfiguracaoAgendaReal, Integer>,
    ConfiguracaoAgendaRealRepositoryCustom {

    Optional<ConfiguracaoAgendaReal> findById(Integer id);

    boolean existsByNivel(CodigoNivel nivel);

    boolean existsByCanal(ECanal canal);

    boolean existsBySubcanalId(Integer subcanalId);

    boolean existsByEstruturaAa(String estruturaAa);

}
