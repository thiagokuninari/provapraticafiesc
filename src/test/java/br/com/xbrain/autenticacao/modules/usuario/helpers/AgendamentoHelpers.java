package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.mailing.dto.AgendamentoAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoPermitidoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisorResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgendamentoDistribuicaoListagemResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgendamentoDistribuicaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgendamentoUsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioPermissaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.assertj.core.api.Condition;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AgendamentoHelpers {
    public static final Function<Long, Condition<Long>> QUANTIDADE_IGUAL_A = (valor) -> new Condition<>() {
        @Override
        public boolean matches(Long value) {
            return value.equals(valor);
        }
    };

    public static List<UsuarioAgenteAutorizadoAgendamentoResponse> usuariosMesmoSegmentoAgenteAutorizado1400() {
        return List.of(
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(140)
                        .nome("MARINA VENDEDORA TELEVENDAS")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(143)
                        .nome("MARINA SUPERVISORA SEM PERMISSAO DE VENDA")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(144)
                        .nome("MARINA SUPERVISORA COM PERMISSAO DE VENDA")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(145)
                        .nome("MARIANO VENDEDOR TELEVENDAS")
                        .build()
                );
    }

    public static List<Usuario> usuariosDoAgenteAutorizado1400() {
        return List.of(
                Usuario.builder()
                        .id(140)
                        .nome("MARINA VENDEDORA TELEVENDAS")
                        .cargo(umCargoVendedorTelevendas())
                        .build(),
                Usuario.builder()
                        .id(141)
                        .nome("MARINA VENDEDORA D2D")
                        .cargo(umCargoVendedorD2d())
                        .build(),
                Usuario.builder()
                        .id(142)
                        .nome("MARIO SÓCIO PRINCIPAL")
                        .cargo(umCargoSocioPrincipal())
                        .build(),
                Usuario.builder()
                        .id(143)
                        .nome("MARINA SUPERVISORA SEM PERMISSAO DE VENDA")
                        .cargo(umCargoSupervisor())
                        .build(),
                Usuario.builder()
                        .id(144)
                        .nome("MARINA SUPERVISORA COM PERMISSAO DE VENDA")
                        .cargo(umCargoSupervisor())
                        .build(),
                Usuario.builder()
                        .id(145)
                        .nome("MARIANO VENDEDOR TELEVENDAS")
                        .cargo(umCargoVendedorTelevendas())
                        .build()
        );
    }

    public static List<UsuarioAgenteAutorizadoAgendamentoResponse> usuariosMesmoSegmentoAgenteAutorizado1300() {
        return List.of(
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(130)
                        .nome("JOÃO MARINHO DA SILVA DOS SANTOS")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(133)
                        .nome("JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(134)
                        .nome("MARIA DA SILVA SAURO SANTOS")
                        .build(),
                UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                        .id(135)
                        .nome("MARCOS AUGUSTO DA SILVA SANTOS")
                        .build());
    }

    public static List<Usuario> usuariosDoAgenteAutorizado1300() {
        return List.of(
                Usuario.builder()
                        .id(130)
                        .nome("JOÃO MARINHO DA SILVA DOS SANTOS")
                        .cargo(umCargoVendedorTelevendas())
                        .build(),
                Usuario.builder()
                        .id(131)
                        .nome("JOÃO MARINHO DA SILVA DOS SANTOS JÚNIOR")
                        .cargo(umCargoVendedorD2d())
                        .build(),
                Usuario.builder()
                        .id(132)
                        .nome("JOSÉ MARINHO DA SILVA DOS SANTOS")
                        .cargo(umCargoSocioPrincipal())
                        .build(),
                Usuario.builder()
                        .id(133)
                        .nome("JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR")
                        .cargo(umCargoSupervisor())
                        .build(),
                Usuario.builder()
                        .id(134)
                        .nome("MARIA DA SILVA SAURO SANTOS")
                        .cargo(umCargoVendedorTelevendas())
                        .build(),
                Usuario.builder()
                        .id(135)
                        .nome("MARCOS AUGUSTO DA SILVA SANTOS")
                        .cargo(umCargoSupervisor())
                        .build()
        );
    }

    public static List<UsuarioAgenteAutorizadoResponse> todosUsuariosDoAgenteAutorizado1300() {
        return usuariosDoAgenteAutorizado1300()
                .stream()
                .map(u -> UsuarioAgenteAutorizadoResponse.builder()
                        .id(u.getId())
                        .nome(u.getNome())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<AgenteAutorizadoPermitidoResponse> agentesAutorizadosPermitidos() {
        return List.of(
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
                .agendamentosPorUsuario(List.of(
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
                .agendamentosPorUsuario(List.of(
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
        return List.of(new AgendamentoAgenteAutorizadoResponse(1300, 35L));
    }

    public static List<AgendamentoAgenteAutorizadoResponse> agendamentosDoAA1400() {
        return List.of(new AgendamentoAgenteAutorizadoResponse(1400, 14L));
    }

    public static EquipeVendasSupervisorResponse umaEquipeVendaAgendamentoRespose() {
        return EquipeVendasSupervisorResponse.builder()
                .equipeVendasNome("UMA EQUIPE DE VENDAS")
                .supervisorNome("SUPERVISOR DA EQUIPE DE VENDAS")
                .build();
    }

    public static List<AgendamentoDistribuicaoListagemResponse> agendamentoDistribuicaoListagemResponse() {
        return List.of(
                new AgendamentoDistribuicaoListagemResponse(1400, "123.456-789/0001-12", "EV", "SUPERVISOR A", 0L, 0L, List.of()),
                new AgendamentoDistribuicaoListagemResponse(1401, "123.456-790/0001-12", "IV", "SUPERVISOR B", 1L, 1L, List.of())
        );
    }

    private static Aplicacao umaAplicacao() {
        var aplicacao = new Aplicacao();
        aplicacao.setId(100);
        aplicacao.setNome("VENDAS");
        aplicacao.setCodigo(CodigoAplicacao.VDS);
        return aplicacao;
    }

    public static UsuarioPermissaoResponse umaPermissaoResponseVazia() {
        return UsuarioPermissaoResponse.builder()
                .permissoesEspeciais(List.of())
                .permissoesCargoDepartamento(List.of())
                .build();
    }

    public static UsuarioPermissaoResponse umaPermissaoDeVendaResponse() {
        return UsuarioPermissaoResponse.builder()
                .permissoesCargoDepartamento(List.of())
                .permissoesEspeciais(List.of(
                        FuncionalidadeResponse.convertFrom(Funcionalidade.builder()
                                .id(100)
                                .role("VDS_TABULACAO_DISCADORA")
                                .aplicacao(umaAplicacao())
                                .build())
                ))
                .build();
    }

    public static Cargo umCargoVendedorTelevendas() {
        return Cargo.builder()
                .id(1000)
                .nome("AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS")
                .codigo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS)
                .nivel(Nivel.builder().codigo(CodigoNivel.AGENTE_AUTORIZADO).build())
                .build();
    }

    private static Cargo umCargoVendedorD2d() {
        return Cargo.builder()
                .id(1000)
                .nome("AGENTE_AUTORIZADO_VENDEDOR_D2D")
                .codigo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_D2D)
                .nivel(Nivel.builder().codigo(CodigoNivel.AGENTE_AUTORIZADO).build())
                .build();
    }

    private static Cargo umCargoSupervisor() {
        return Cargo.builder()
                .id(2000)
                .nome("AGENTE_AUTORIZADO_SUPERVISOR")
                .codigo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR)
                .nivel(Nivel.builder().codigo(CodigoNivel.AGENTE_AUTORIZADO).build())
                .build();
    }

    public static Cargo umCargoSocioPrincipal() {
        return Cargo.builder()
                .id(3000)
                .nome("AGENTE_AUTORIZADO_SOCIO_PRINCIPAL")
                .codigo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO)
                .nivel(Nivel.builder().codigo(CodigoNivel.AGENTE_AUTORIZADO).build())
                .build();
    }

    public static List<Usuario> todosUsuariosDoAgenteAutorizado1500() {
        return IntStream.range(150, 159)
                .mapToObj(i ->
                        Usuario.builder()
                                .id(i)
                                .nome("USUARIO DO AA 1500 - " + i)
                                .cargo(umCargoVendedorTelevendas())
                                .build())
                .collect(Collectors.toList());
    }

    public static List<UsuarioAgenteAutorizadoAgendamentoResponse> usuariosDoAgenteAutorizado1500() {
        return IntStream.range(150, 159)
                .mapToObj(i ->
                        UsuarioAgenteAutorizadoAgendamentoResponse.builder()
                                .id(i)
                                .nome("USUARIO DO AA 1500 - " + i)
                                .build())
                .collect(Collectors.toList());
    }
}
