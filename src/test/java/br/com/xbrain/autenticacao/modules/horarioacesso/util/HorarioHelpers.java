package br.com.xbrain.autenticacao.modules.horarioacesso.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAtuacaoDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;
import br.com.xbrain.autenticacao.modules.site.model.Site;

public class HorarioHelpers {
    public static HorarioAcesso umHorarioAcesso() {
        return HorarioAcesso.builder()
            .id(1)
            .site(Site.builder().id(100).nome("SITE TESTE").build())
            .dataAlteracao(LocalDateTime.of(2021, 11, 22, 13, 53, 10))
            .usuarioAlteracaoId(100)
            .usuarioAlteracaoNome("USUARIO TESTE")
            .build();
    }

    public static HorarioHistorico umHorarioHistorico() {
        return HorarioHistorico.builder()
            .id(1)
            .horarioAcesso(umHorarioAcesso())
            .dataAlteracao(LocalDateTime.of(2021, 11, 22, 13, 53, 10))
            .usuarioAlteracaoId(100)
            .usuarioAlteracaoNome("USUARIO TESTE")
            .build();
    }

    public static List<HorarioAtuacao> umaListaHorariosAtuacao() {
        return List.of(
            HorarioAtuacao.builder()
                .id(1)
                .horarioAcesso(umHorarioAcesso())
                .horarioHistorico(umHorarioHistorico())
                .diaSemana(EDiaSemana.SEGUNDA)
                .horarioInicio(LocalTime.of(9, 0))
                .horarioFim(LocalTime.of(15, 0))
                .build(),
            HorarioAtuacao.builder()
                .id(2)
                .horarioAcesso(umHorarioAcesso())
                .horarioHistorico(umHorarioHistorico())
                .diaSemana(EDiaSemana.QUARTA)
                .horarioInicio(LocalTime.of(9, 0))
                .horarioFim(LocalTime.of(15, 0))
                .build(),
            HorarioAtuacao.builder()
                .id(3)
                .horarioAcesso(umHorarioAcesso())
                .horarioHistorico(umHorarioHistorico())
                .diaSemana(EDiaSemana.SEXTA)
                .horarioInicio(LocalTime.of(9, 0))
                .horarioFim(LocalTime.of(15, 0))
                .build()
        );
    }

    public static HorarioAcessoRequest umHorarioAcessoRequest() {
        return HorarioAcessoRequest.builder()
            .siteId(100)
            .horariosAtuacao(List.of(
                HorarioAtuacaoDto.builder()
                    .diaSemana("SEGUNDA")
                    .horarioInicio("09:00")
                    .horarioFim("15:00")
                    .build(),
                HorarioAtuacaoDto.builder()
                    .diaSemana("QUARTA")
                    .horarioInicio("09:00")
                    .horarioFim("15:00")
                    .build(),
                HorarioAtuacaoDto.builder()
                    .diaSemana("SEXTA")
                    .horarioInicio("09:00")
                    .horarioFim("15:00")
                    .build()))
            .build();
    }
}
