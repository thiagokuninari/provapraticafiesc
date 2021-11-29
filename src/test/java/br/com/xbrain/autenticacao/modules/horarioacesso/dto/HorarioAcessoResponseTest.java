package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import java.util.List;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.horarioacesso.helper.HorarioHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class HorarioAcessoResponseTest {
    
    @Test
    public void of_deveRetornarHorarioAcessoResponse_seFornecidoHorarioAcesso() {
        var response = HorarioAcessoResponse.of(umHorarioAcesso());
        response.setHorariosAtuacao(umaListaHorariosAtuacao());

        assertThat(response)
            .extracting("horarioAcessoId", "horarioHistoricoId", "siteId", "siteNome",
                "dataAlteracao", "usuarioAlteracaoNome", "horariosAtuacao")
            .containsExactly(1, null, 100, "SITE TESTE", "22/11/2021 13:53:10", "USUARIO TESTE", 
                List.of(
                    HorarioAtuacaoDto.builder()
                        .id(1)
                        .diaSemana("Segunda-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(2)
                        .diaSemana("Quarta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(3)
                        .diaSemana("Sexta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build()));
    }

    @Test
    public void of_deveRetornarHorarioAcessoResponse_seFornecidoHorarioHistorico() {
        var response = HorarioAcessoResponse.of(umHorarioHistorico());
        response.setHorariosAtuacao(umaListaHorariosAtuacao());

        assertThat(response)
            .extracting("horarioAcessoId", "horarioHistoricoId", "siteId", "siteNome",
                "dataAlteracao", "usuarioAlteracaoNome", "horariosAtuacao")
            .containsExactly(1, 1, 100, "SITE TESTE", "22/11/2021 13:53:10", "USUARIO TESTE", 
                List.of(
                    HorarioAtuacaoDto.builder()
                        .id(1)
                        .diaSemana("Segunda-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(2)
                        .diaSemana("Quarta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(3)
                        .diaSemana("Sexta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build()));
    }
}
