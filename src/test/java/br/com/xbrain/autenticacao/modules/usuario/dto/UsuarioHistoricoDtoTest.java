package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioAfastamento;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioFerias;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioHistoricoDtoTest {

    @Test
    public void of_deveRetornarUsuarioHistoricoDto_quandoSolicitado() {
        assertThat(UsuarioHistoricoDto.of(umUsuarioHistorico(umUsuarioFerias(), umUsuarioAfastamento())))
            .extracting("id", "situacao", "observacao", "cadastro", "feriasInicio", "feriasFim",
                "afastamentoInicio", "afastamentoFim", "usuarioAlteracao")
            .containsExactly(23,
                "ATIVO",
                "uma observacao bem observada",
                LocalDateTime.of(2024, 8, 9, 0, 0, 0),
                LocalDate.of(2024, 7, 9),
                LocalDate.of(2024, 8, 9),
                LocalDate.of(2024, 5, 9),
                LocalDate.of(2024, 6, 9),
                "nome do usuário");
    }

    @Test
    public void of_deveRetornarUsuarioHistoricoDto_quandoUsuarioAlteracaoNull() {
        var historico = umUsuarioHistorico(umUsuarioFerias(), umUsuarioAfastamento());
        historico.setUsuarioAlteracao(null);
        assertThat(UsuarioHistoricoDto.of(historico))
            .extracting("id", "situacao", "observacao", "cadastro", "feriasInicio", "feriasFim",
                "afastamentoInicio", "afastamentoFim", "usuarioAlteracao")
            .containsExactly(23,
                "ATIVO",
                "uma observacao bem observada",
                LocalDateTime.of(2024, 8, 9, 0, 0, 0),
                LocalDate.of(2024, 7, 9),
                LocalDate.of(2024, 8, 9),
                LocalDate.of(2024, 5, 9),
                LocalDate.of(2024, 6, 9),
                null);
    }

    @Test
    public void getFeriasInicio_deveRetornarInicioDasFerias_quandoHouverDados() {
        assertThat(UsuarioHistoricoDto.getFeriasInicio(umUsuarioHistorico(umUsuarioFerias(), umUsuarioAfastamento())))
            .isEqualTo(LocalDate.of(2024, 7, 9));
    }

    @Test
    public void getFeriasInicio_deveRetornarNull_quandoNaoHouverDados() {
        assertThat(UsuarioHistoricoDto.getFeriasInicio(umUsuarioHistorico(null, umUsuarioAfastamento())))
            .isNull();
    }

    @Test
    public void getFeriasFim_deveRetornarFimDasFerias_quandoHouverDados() {
        assertThat(UsuarioHistoricoDto.getFeriasFim(umUsuarioHistorico(umUsuarioFerias(), umUsuarioAfastamento())))
            .isEqualTo(LocalDate.of(2024, 8, 9));
    }

    @Test
    public void getFeriasFim_deveRetornarNull_quandoNaoHouverDados() {
        assertThat(UsuarioHistoricoDto.getFeriasFim(umUsuarioHistorico(null, umUsuarioAfastamento())))
            .isNull();
    }

    @Test
    public void getAfastamentoInicio_deveRetornarInicioDoAfastamento_quandoHouverDados() {
        assertThat(UsuarioHistoricoDto.getAfastamentoInicio(umUsuarioHistorico(umUsuarioFerias(), umUsuarioAfastamento())))
            .isEqualTo(LocalDate.of(2024, 5, 9));
    }

    @Test
    public void getAfastamentoInicio_deveRetornarNull_quandoNaoHouverDados() {
        assertThat(UsuarioHistoricoDto.getAfastamentoInicio(umUsuarioHistorico(umUsuarioFerias(), null)))
            .isNull();
    }

    @Test
    public void getAfastamentoFim_deveRetornarFimDoAfastamento_quandoHouverDados() {
        assertThat(UsuarioHistoricoDto.getAfastamentoFim(umUsuarioHistorico(umUsuarioFerias(), umUsuarioAfastamento())))
            .isEqualTo(LocalDate.of(2024, 6, 9));
    }

    @Test
    public void getAfastamentoFim_deveRetornarNull_quandoNaoHouverDados() {
        assertThat(UsuarioHistoricoDto.getAfastamentoFim(umUsuarioHistorico(umUsuarioFerias(), null)))
            .isNull();
    }

    private UsuarioHistorico umUsuarioHistorico(UsuarioFerias usuarioFerias, UsuarioAfastamento usuarioAfastamento) {
        return UsuarioHistorico.builder()
            .ferias(usuarioFerias)
            .afastamento(usuarioAfastamento)
            .id(23)
            .situacao(ESituacao.A)
            .observacao("uma observacao bem observada")
            .dataCadastro(LocalDateTime.of(2024, 8, 9, 0, 0, 0))
            .usuarioAlteracao(Usuario.builder().nome("nome do usuário").build())
            .build();
    }

    private UsuarioFerias umUsuarioFerias() {
        return UsuarioFerias.builder()
            .inicio(LocalDate.of(2024, 7, 9))
            .fim(LocalDate.of(2024, 8, 9))
            .build();
    }

    private UsuarioAfastamento umUsuarioAfastamento() {
        var afastamento = new UsuarioAfastamento();
        afastamento.setInicio(LocalDate.of(2024, 5, 9));
        afastamento.setFim(LocalDate.of(2024, 6, 9));
        return afastamento;
    }
}
