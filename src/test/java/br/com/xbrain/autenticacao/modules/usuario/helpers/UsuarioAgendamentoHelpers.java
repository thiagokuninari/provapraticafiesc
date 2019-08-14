package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoPermitidoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioPermissaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

public class UsuarioAgendamentoHelpers {

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

    public static List<Usuario> usuariosDoAgenteAutorizado999() {
        return List.of(
                Usuario.builder()
                        .id(9991)
                        .nome("USUARIO 1 DO AA 999")
                        .cargo(umCargoVendedorTelevendas())
                        .build(),
                Usuario.builder()
                        .id(9992)
                        .nome("USUARIO 2 DO AA 999")
                        .cargo(umCargoVendedorD2d())
                        .build(),
                Usuario.builder()
                        .id(9993)
                        .nome("USUARIO 3 DO AA 999")
                        .cargo(umCargoSocioPrincipal())
                        .build(),
                Usuario.builder()
                        .id(9994)
                        .nome("USUARIO 4 DO AA 999")
                        .cargo(umCargoSocioPrincipal())
                        .build(),
                Usuario.builder()
                        .id(9995)
                        .nome("USUARIO 5 DO AA 999")
                        .cargo(umCargoSupervisor())
                        .build()
        );
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
                .map(u -> new UsuarioAgenteAutorizadoResponse(u.getId(), u.getNome(), null))
                .collect(Collectors.toList());
    }

    public static List<UsuarioAgenteAutorizadoResponse> todosUsuariosDoAgenteAutorizado999() {
        return usuariosDoAgenteAutorizado999()
                .stream()
                .map(u -> new UsuarioAgenteAutorizadoResponse(u.getId(), u.getNome(),
                        u.getCargoCodigo() == CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS ? 999 : null))
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
}
