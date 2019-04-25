package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.mailing.dto.AgendamentoAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoPermitidoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisorResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgendamentoDistribuicaoListagemResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgendamentoDistribuicaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgendamentoUsuarioDto;
import org.assertj.core.api.Condition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class AgendamentoHelpers {
    public static final Function<Long, Condition<Long>> QUANTIDADE_IGUAL_A = (valor) -> new Condition<Long>() {
        @Override
        public boolean matches(Long value) {
            return value.equals(valor);
        }
    };

    public static List<UsuarioAgenteAutorizadoAgendamentoResponse> usuariosDoAgenteAutorizado1400() {
        return Arrays.asList(
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(140)
                        .nome("MARINA PERES DA SILVA DOS SANTOS")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(141)
                        .nome("MARINA PERES DA SILVA DOS SANTOS JÚNIOR")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(142)
                        .nome("MARIA DA SILVA DOS SANTOS")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(143)
                        .nome("MARIA DA SILVA DOS SANTOS JÚNIOR")
                        .build());
    }

    public static List<UsuarioAgenteAutorizadoAgendamentoResponse> usuariosDoAgenteAutorizado1300() {
        return Arrays.asList(
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(130)
                        .nome("JOÃO MARINHO DA SILVA DOS SANTOS")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(131)
                        .nome("JOÃO MARINHO DA SILVA DOS SANTOS JÚNIOR")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(132)
                        .nome("JOSÉ MARINHO DA SILVA DOS SANTOS")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(133)
                        .nome("JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR")
                        .build());
    }

    public static List<AgenteAutorizadoPermitidoResponse> agentesAutorizadosPermitidos() {
        return Arrays.asList(
                AgenteAutorizadoPermitidoResponse.builder()
                        .id(1300)
                        .cnpj("17.822.087/0001-85")
                        .cnpjRazaoSocial("17.822.087/0001-85 - S2 TELECOM COMERCIO DE ANTENAS EIRELI - ME")
                        .build(),
                AgenteAutorizadoPermitidoResponse.builder()
                        .id(1400)
                        .cnpj("06.152.588/0001-85")
                        .cnpjRazaoSocial("06.152.588/0001-85 - AS2 COMERCIO DE ANTENAS EIRELI - ME")
                        .build(),
                AgenteAutorizadoPermitidoResponse.builder()
                        .id(1500)
                        .cnpj("07.152.589/0001-85")
                        .cnpjRazaoSocial("07.152.589/0001-85 - AS3 COMERCIO DE ANTENAS EIRELI - ME")
                        .build()
        );
    }

    public static AgendamentoDistribuicaoRequest umAgendamentoDistribuicaoRequestDoUsuario140() {
        return AgendamentoDistribuicaoRequest.builder()
                .agenteAutorizadoId(1400)
                .usuarioOrigemId(140)
                .agendamentosPorUsuario(Collections.singletonList(
                        AgendamentoUsuarioDto.builder()
                                .id(141)
                                .nome("USUARIO 141")
                                .quantidade(3L)
                                .build()))
                .build();
    }

    public static AgendamentoDistribuicaoRequest umAgendamentoDistribuicaoRequestDoUsuario141() {
        return AgendamentoDistribuicaoRequest.builder()
                .agenteAutorizadoId(1400)
                .usuarioOrigemId(141)
                .agendamentosPorUsuario(Arrays.asList(
                        AgendamentoUsuarioDto.builder()
                                .id(140)
                                .nome("USUARIO 140")
                                .quantidade(5L)
                                .build(),
                        AgendamentoUsuarioDto.builder()
                                .id(142)
                                .nome("USUARIO 142")
                                .quantidade(5L)
                                .build(),
                        AgendamentoUsuarioDto.builder()
                                .id(143)
                                .nome("USUARIO 143")
                                .quantidade(4L)
                                .build()))
                .build();
    }

    public static List<AgendamentoAgenteAutorizadoResponse> agendamentosDoUsuario130PorAa() {
        return Arrays.asList(
                new AgendamentoAgenteAutorizadoResponse(1300, 15L),
                new AgendamentoAgenteAutorizadoResponse(1400, 14L));
    }

    public static List<AgendamentoAgenteAutorizadoResponse> agendamentosDoAA1400() {
        return Collections.singletonList(new AgendamentoAgenteAutorizadoResponse(1400, 14L));
    }

    public static EquipeVendasSupervisorResponse umaEquipeVendaAgendamentoRespose() {
        return EquipeVendasSupervisorResponse.builder()
                .equipeVendasNome("UMA EQUIPE DE VENDAS")
                .supervisorNome("SUPERVISOR DA EQUIPE DE VENDAS")
                .build();
    }

    public static List<AgendamentoDistribuicaoListagemResponse> agendamentoDistribuicaoListagemResponse() {
        return Arrays.asList(
                new AgendamentoDistribuicaoListagemResponse(1400, "123.456-789/0001-12", "EV", "SUPERVISOR A", 0L, 0L, List.of()),
                new AgendamentoDistribuicaoListagemResponse(1401, "123.456-790/0001-12", "IV", "SUPERVISOR B", 1L, 1L, List.of())
        );
    }
}
