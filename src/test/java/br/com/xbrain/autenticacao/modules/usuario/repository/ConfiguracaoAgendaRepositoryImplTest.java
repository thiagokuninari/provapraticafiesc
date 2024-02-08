package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:/configuracao-agenda-test.sql")
public class ConfiguracaoAgendaRepositoryImplTest {

    @Autowired
    private ConfiguracaoAgendaRepository repository;

    @Test
    public void findQtdHorasAdicionaisByCanal_deveRetornarQtdHorasDaConfigAtiva_quandoExistir() {
        assertThat(repository.findQtdHorasAdicionaisByCanal(ECanal.AGENTE_AUTORIZADO))
            .isEqualTo(Optional.of(15));
    }

    @Test
    public void findQtdHorasAdicionaisByCanal_deveRetornarOptionalVazio_quandoNaoExistir() {
        assertThat(repository.findQtdHorasAdicionaisByCanal(ECanal.ATP))
            .isEqualTo(Optional.empty());
    }

    @Test
    public void findQtdHorasAdicionaisByNivel_deveRetornarQtdHorasDaConfigAtiva_quandoExistir() {
        assertThat(repository.findQtdHorasAdicionaisByNivel(CodigoNivel.RECEPTIVO))
            .isEqualTo(Optional.of(20));
    }

    @Test
    public void findQtdHorasAdicionaisByNivel_deveRetornarOptionalVazio_quandoNaoExistir() {
        assertThat(repository.findQtdHorasAdicionaisByNivel(CodigoNivel.AGENTE_AUTORIZADO_NACIONAL))
            .isEqualTo(Optional.empty());
    }

    @Test
    public void findQtdHorasAdicionaisByEstruturaAa_deveRetornarQtdHorasDaConfigAtiva_quandoExistir() {
        assertThat(repository.findQtdHorasAdicionaisByEstruturaAa("AGENTE_AUTORIZADO"))
            .isEqualTo(Optional.of(25));
    }

    @Test
    public void findQtdHorasAdicionaisByEstruturaAa_deveRetornarOptionalVazio_quandoNaoExistir() {
        assertThat(repository.findQtdHorasAdicionaisByEstruturaAa("AGENTE_AUTORIZADO_NACIONAL"))
            .isEqualTo(Optional.empty());
    }

    @Test
    public void findQtdHorasAdicionaisBySubcanal_deveRetornarQtdHorasDaConfigAtiva_quandoExistir() {
        assertThat(repository.findQtdHorasAdicionaisBySubcanal(ETipoCanal.PAP))
            .isEqualTo(Optional.of(30));
    }

    @Test
    public void findQtdHorasAdicionaisBySubcanal_deveRetornarOptionalVazio_quandoNaoExistir() {
        assertThat(repository.findQtdHorasAdicionaisBySubcanal(ETipoCanal.INSIDE_SALES_PME))
            .isEqualTo(Optional.empty());
    }
}
