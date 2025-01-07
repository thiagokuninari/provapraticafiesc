package br.com.xbrain.autenticacao.modules.horarioacesso.helper;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAtuacaoDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class HorarioHelper {
    public static HorarioAcesso umHorarioAcesso() {
        return HorarioAcesso.builder()
            .id(1)
            .site(umSite())
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

    public static HorarioAcessoResponse umHorarioAcessoResponse() {
        return HorarioAcessoResponse.builder()
            .horarioAcessoId(1)
            .horarioHistoricoId(null)
            .siteId(100)
            .siteNome("SITE TESTE")
            .dataAlteracao("22/11/2021 13:53:10")
            .usuarioAlteracaoNome("USUARIO TESTE")
            .horariosAtuacao(umaListaHorarioAtuacaoDto())
            .build();
    }

    public static HorarioAcessoResponse umHorarioHistoricoResponse() {
        return HorarioAcessoResponse.builder()
            .horarioAcessoId(1)
            .horarioHistoricoId(1)
            .siteId(100)
            .siteNome("SITE TESTE")
            .dataAlteracao("22/11/2021 13:53:10")
            .usuarioAlteracaoNome("USUARIO TESTE")
            .horariosAtuacao(umaListaHorarioAtuacaoDto())
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
            .id(1)
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

    public static List<HorarioAtuacaoDto> umaListaHorarioAtuacaoDto() {
        return List.of(
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
                .build());
    }

    public static Site umSite() {
        return Site.builder()
            .id(100)
            .nome("SITE TESTE")
            .build();
    }

    public static UsuarioAutenticado umOperadorTelevendas() {
        return UsuarioAutenticado.builder()
            .usuario(Usuario.builder()
                .id(101)
                .nome("OPERADOR TELEVENDAS")
                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                .cargo(Cargo.builder()
                    .codigo(CodigoCargo.OPERACAO_TELEVENDAS)
                    .build())
                .build())
            .nivelCodigo("OPERACAO")
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .cargoCodigo(CodigoCargo.OPERACAO_TELEVENDAS)
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .usuario(Usuario.builder()
                .id(101)
                .nome("SUPERVISOR_ATIVO_LOCAL_PROPRIO")
                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                .cargo(Cargo.builder()
                    .codigo(CodigoCargo.SUPERVISOR_ATIVO_LOCAL_PROPRIO)
                    .build())
                .build())
            .nivelCodigo("XBRAIN")
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .cargoCodigo(CodigoCargo.SUPERVISOR_ATIVO_LOCAL_PROPRIO)
            .build();
    }

    public static UsuarioAutenticado umAdmin() {
        return UsuarioAutenticado.builder()
            .usuario(Usuario.builder()
                .id(102)
                .nome("ADMIN")
                .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
                .cargo(Cargo.builder()
                    .codigo(CodigoCargo.ADMINISTRADOR)
                    .build())
                .build())
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
            .cargoCodigo(CodigoCargo.ADMINISTRADOR)
            .build();
    }

    public static Page<HorarioAcessoResponse> umaListaHorarioHistoricoResponse() {
        return new PageImpl<>(List.of(umHorarioHistoricoResponse()));
    }

    public static List<SelectResponse> umaListaSelectResponse() {
        return List.of(SelectResponse.of(1, "teste"));
    }
}
