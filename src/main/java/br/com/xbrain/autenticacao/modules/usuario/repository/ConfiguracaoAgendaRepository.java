package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracaoAgendaRepository extends JpaRepository<ConfiguracaoAgenda, Integer>,
    ConfiguracaoAgendaRepositoryCustom {
    boolean existsByNivelAndCanalAndSituacao(CodigoNivel nivel, ECanal canal, ESituacao situacao);

    Optional<ConfiguracaoAgenda> findById(Integer id);
}
