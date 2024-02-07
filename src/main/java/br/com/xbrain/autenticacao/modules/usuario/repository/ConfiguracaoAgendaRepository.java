package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracaoAgendaRepository extends JpaRepository<ConfiguracaoAgenda, Integer>,
    ConfiguracaoAgendaRepositoryCustom {

    Optional<ConfiguracaoAgenda> findById(Integer id);
}
