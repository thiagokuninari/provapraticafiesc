package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:/configuracao-agenda-test.sql")
public class ConfiguracaoAgendaRealRepositoryImplTest {

    @Autowired
    private ConfiguracaoAgendaRealRepository repository;

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
        assertThat(repository.findQtdHorasAdicionaisBySubcanal(ETipoCanal.PAP.getId()))
            .isEqualTo(Optional.of(30));
    }

    @Test
    public void findQtdHorasAdicionaisBySubcanal_deveRetornarOptionalVazio_quandoNaoExistir() {
        assertThat(repository.findQtdHorasAdicionaisBySubcanal(ETipoCanal.INSIDE_SALES_PME.getId()))
            .isEqualTo(Optional.empty());
    }

    @Test
    public void findAllByPredicate_deveListarConfigPadraoPrimeiro_quandoConfigPadraoNaoPossuirDataCadastroEOrdemAsc() {
        assertThat(repository.findAllByPredicate(new BooleanBuilder(), new PageRequest(0, 10, "id", "ASC")))
            .hasSize(9)
            .startsWith(umaConfiguracaoAgendaPadrao())
            .endsWith(umaConfiguracaoAgendaUltimaOrdemAsc());
    }

    @Test
    public void findAllByPredicate_deveListarConfigPadraoPrimeiro_quandoConfigPadraoNaoPossuirDataCadastroEOrdemDesc() {
        assertThat(repository.findAllByPredicate(new BooleanBuilder(), new PageRequest(0, 10, "id", "DESC")))
            .hasSize(9)
            .startsWith(umaConfiguracaoAgendaPadrao())
            .endsWith(umaConfiguracaoAgendaUltimaOrdemDesc());
    }

    @Test
    public void existsByNivel_deveRetornarTrue_quandoNivelEncontradoETipoConfiguracaoForNivel() {
        assertThat(repository.existsByNivel(CodigoNivel.RECEPTIVO))
            .isTrue();
    }

    @Test
    public void existsByNivel_deveRetornarFalse_quandoNivelNaoEncontradoETipoConfiguracaoForNivel() {
        assertThat(repository.existsByNivel(CodigoNivel.OPERACAO))
            .isFalse();
    }

    @Test
    public void existsByCanal_deveRetornarTrue_quandoCanalEncontradoETipoConfiguracaoForCanal() {
        assertThat(repository.existsByCanal(ECanal.AGENTE_AUTORIZADO))
            .isTrue();
    }

    @Test
    public void existsByCanal_deveRetornarFalse_quandoCanalNaoEncontradoETipoConfiguracaoForCanal() {
        assertThat(repository.existsByCanal(ECanal.D2D_PROPRIO))
            .isFalse();
    }

    @Test
    public void existsBySubcanalId_deveRetornarTrue_quandoSubCanalEncontradoETipoConfiguracaoForSubCanal() {
        assertThat(repository.existsBySubcanalId(1))
            .isTrue();
    }

    @Test
    public void existsBySubcanalId_deveRetornarFalse_quandoSubCanalNaoEncontradoETipoConfiguracaoForSubCanal() {
        assertThat(repository.existsBySubcanalId(2345))
            .isFalse();
    }

    @Test
    public void existsByEstruturaAa_deveRetornarTrue_quandoEstruturaEncontradoETipoConfiguracaoForEstrutura() {
        assertThat(repository.existsByEstruturaAa("AGENTE_AUTORIZADO"))
            .isTrue();
    }

    @Test
    public void existsByEstruturaAa_deveRetornarFalse_quandoEstruturaNaoEncontradoETipoConfiguracaoForEstrutura() {
        assertThat(repository.existsByEstruturaAa("OPERACAO"))
            .isFalse();
    }

    @Test
    public void existeConfiguracaoPadrao_deveRetornarTrue_quandoExistirConfiguracaoPadrao() {
        assertThat(repository.existeConfiguracaoPadrao())
            .isTrue();
    }
}
