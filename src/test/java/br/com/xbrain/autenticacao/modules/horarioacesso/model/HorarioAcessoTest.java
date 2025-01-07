package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
import static org.assertj.core.api.Assertions.assertThat;

public class HorarioAcessoTest {

    @Test
    public void of_deveRetornarHorarioAcesso_quandoHorarioAcessoRequestPorParametro() {
        assertThat(HorarioAcesso.of(umHorarioAcessoRequest()))
            .extracting("id", "site.id")
            .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    public void of_deveRetornarHorarioAcesso_quandoHorarioAcessoResponsePorParametro() {
        assertThat(HorarioAcesso.of(umHorarioAcessoResponse()))
            .extracting("id", "site.id")
            .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    public void setDadosAlteracao_deveSetarCampos_quandoSolicitado() {
        var horarioAcesso = new HorarioAcesso();
        assertThat(horarioAcesso.getDataAlteracao()).isNull();
        assertThat(horarioAcesso.getUsuarioAlteracaoId()).isNull();
        assertThat(horarioAcesso.getUsuarioAlteracaoNome()).isNull();

        horarioAcesso.setDadosAlteracao(umUsuario());

        assertThat(horarioAcesso.getDataAlteracao()).isNotNull();
        assertThat(horarioAcesso.getUsuarioAlteracaoId()).isNotNull();
        assertThat(horarioAcesso.getUsuarioAlteracaoNome()).isNotNull();

    }

    private HorarioAcessoRequest umHorarioAcessoRequest() {
        return HorarioAcessoRequest.builder()
            .id(1)
            .siteId(2)
            .build();
    }

    private HorarioAcessoResponse umHorarioAcessoResponse() {
        return HorarioAcessoResponse.builder()
            .horarioAcessoId(1)
            .siteId(2)
            .build();
    }

}
