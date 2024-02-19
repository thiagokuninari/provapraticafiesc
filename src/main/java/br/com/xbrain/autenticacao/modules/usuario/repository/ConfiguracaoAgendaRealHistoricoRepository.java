package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaRealHistorico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoAgendaRealHistoricoRepository extends
    JpaRepository<ConfiguracaoAgendaRealHistorico, Integer> {

    Page<ConfiguracaoAgendaRealHistorico> findByConfiguracao_Id(Integer id, Pageable pageable);
}
