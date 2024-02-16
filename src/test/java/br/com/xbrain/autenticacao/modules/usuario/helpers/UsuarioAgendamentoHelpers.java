package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoPermitidoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioPermissaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UsuarioAgendamentoHelpers {

    public static ConfiguracaoAgendaFiltros umaConfiguracaoAgendaFiltros() {
        return ConfiguracaoAgendaFiltros.builder()
            .tipoConfiguracao(ETipoConfiguracao.CANAL)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .build();
    }

    public static ConfiguracaoAgendaRequest umaConfiguracaoAgendaRequest() {
        return ConfiguracaoAgendaRequest.builder()
            .qtdHorasAdicionais(100)
            .tipoConfiguracao(ETipoConfiguracao.CANAL)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .build();
    }

    public static ConfiguracaoAgendaRequest umaConfiguracaoAgendaRequest(ETipoConfiguracao config) {
        return ConfiguracaoAgendaRequest.builder()
            .qtdHorasAdicionais(100)
            .tipoConfiguracao(config)
            .build();
    }

    public static ConfiguracaoAgendaRequest umaConfiguracaoAgendaRequest(ECanal canal) {
        return ConfiguracaoAgendaRequest.builder()
            .qtdHorasAdicionais(100)
            .canal(canal)
            .tipoConfiguracao(ETipoConfiguracao.CANAL)
            .build();
    }

    public static ConfiguracaoAgendaRequest umaConfiguracaoAgendaRequest(ETipoCanal subcanal) {
        return ConfiguracaoAgendaRequest.builder()
            .qtdHorasAdicionais(100)
            .subcanalId(subcanal.getId())
            .tipoConfiguracao(ETipoConfiguracao.SUBCANAL)
            .build();
    }

    public static ConfiguracaoAgendaRequest umaConfiguracaoAgendaRequest(CodigoNivel nivel) {
        return ConfiguracaoAgendaRequest.builder()
            .qtdHorasAdicionais(100)
            .nivel(nivel)
            .tipoConfiguracao(ETipoConfiguracao.NIVEL)
            .build();
    }

    public static ConfiguracaoAgendaRequest umaConfiguracaoAgendaRequest(String estrutura) {
        return ConfiguracaoAgendaRequest.builder()
            .qtdHorasAdicionais(100)
            .estruturaAa(estrutura)
            .tipoConfiguracao(ETipoConfiguracao.ESTRUTURA)
            .build();
    }

    public static ConfiguracaoAgendaResponse umaConfiguracaoAgendaResponse() {
        return ConfiguracaoAgendaResponse.builder()
            .qtdHorasAdicionais(100)
            .tipoConfiguracao(ETipoConfiguracao.CANAL)
            .canal(ECanal.AGENTE_AUTORIZADO.getDescricao())
            .situacao(ESituacao.A.getDescricao())
            .build();
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgendaPadrao() {
        return ConfiguracaoAgendaReal.builder()
            .qtdHorasAdicionais(24)
            .situacao(ESituacao.A)
            .tipoConfiguracao(ETipoConfiguracao.PADRAO)
            .build();
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgenda() {
        return ConfiguracaoAgendaReal.builder()
            .qtdHorasAdicionais(100)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .tipoConfiguracao(ETipoConfiguracao.CANAL)
            .situacao(ESituacao.A)
            .usuarioCadastroId(2)
            .usuarioCadastroNome("Thiago")
            .dataCadastro(LocalDateTime.of(2024, 1, 1, 12, 30))
            .build();
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgenda(String estrutura) {
        return ConfiguracaoAgendaReal.builder()
            .qtdHorasAdicionais(100)
            .situacao(ESituacao.A)
            .estruturaAa(estrutura)
            .tipoConfiguracao(ETipoConfiguracao.ESTRUTURA)
            .usuarioCadastroId(2)
            .usuarioCadastroNome("Thiago")
            .dataCadastro(LocalDateTime.of(2024, 1, 1, 12, 30))
            .build();
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgenda(ETipoCanal subcanal) {
        return ConfiguracaoAgendaReal.builder()
            .qtdHorasAdicionais(100)
            .situacao(ESituacao.A)
            .subcanalId(subcanal.getId())
            .tipoConfiguracao(ETipoConfiguracao.SUBCANAL)
            .usuarioCadastroId(2)
            .usuarioCadastroNome("Thiago")
            .dataCadastro(LocalDateTime.of(2024, 1, 1, 12, 30))
            .build();
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgenda(CodigoNivel nivel) {
        return ConfiguracaoAgendaReal.builder()
            .qtdHorasAdicionais(100)
            .situacao(ESituacao.A)
            .nivel(nivel)
            .tipoConfiguracao(ETipoConfiguracao.NIVEL)
            .usuarioCadastroId(2)
            .usuarioCadastroNome("Thiago")
            .dataCadastro(LocalDateTime.of(2024, 1, 1, 12, 30))
            .build();
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgenda(ECanal canal) {
        return ConfiguracaoAgendaReal.builder()
            .qtdHorasAdicionais(100)
            .situacao(ESituacao.A)
            .canal(canal)
            .tipoConfiguracao(ETipoConfiguracao.CANAL)
            .usuarioCadastroId(2)
            .usuarioCadastroNome("Thiago")
            .dataCadastro(LocalDateTime.of(2024, 1, 1, 12, 30))
            .build();
    }

    public static List<ConstraintViolation> umaConstraintViolation(String mensagem, String propriedade,
                                                                   String validationMessage, Class classe) {
        var path = new Path() {
            @Override
            public Iterator<Node> iterator() {
                return null;
            }

            @Override
            public String toString() {
                return propriedade;
            }
        };

        return List.of(ConstraintViolationImpl.forReturnValueValidation(
            validationMessage, null, mensagem, classe, null, null,
            null, path, null, null, null, null));
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

    public static UsuarioAgenteAutorizadoResponse umUsuarioAaResponse() {
        return UsuarioAgenteAutorizadoResponse.builder()
            .id(9999)
            .equipeVendaId(999)
            .nome("Kakarotto")
            .email("emailTeste@email.com")
            .agenteAutorizadoId(999)
            .build();
    }

    public static Optional<Usuario> umUsuarioId9991() {
        return Optional.ofNullable(Usuario.builder()
            .id(9991)
            .nome("USUARIO 1 DO AA 999")
            .cargo(umCargoVendedorTelevendas())
            .situacao(ESituacao.A)
            .build());
    }

    public static Optional<Usuario> umUsuarioId9992() {
        return Optional.ofNullable(Usuario.builder()
            .id(9992)
            .nome("USUARIO 2 DO AA 999")
            .cargo(umCargoVendedorTelevendas())
            .situacao(ESituacao.A)
            .build());
    }

    public static Optional<Usuario> umUsuarioId9993() {
        return Optional.ofNullable(Usuario.builder()
            .id(9993)
            .nome("USUARIO 3 DO AA 999")
            .cargo(umCargoVendedorTelevendas())
            .situacao(ESituacao.A)
            .build());
    }

    public static Optional<Usuario> umUsuarioId9994() {
        return Optional.ofNullable(Usuario.builder()
            .id(9994)
            .nome("USUARIO 4 DO AA 999")
            .cargo(umCargoVendedorTelevendas())
            .situacao(ESituacao.A)
            .build());
    }

    public static Optional<Usuario> umUsuarioId9995() {
        return Optional.ofNullable(Usuario.builder()
            .id(9995)
            .nome("USUARIO 5 DO AA 999")
            .cargo(umCargoVendedorTelevendas())
            .situacao(ESituacao.A)
            .build());
    }

    public static List<Usuario> usuariosDoAgenteAutorizado999() {
        return List.of(
                Usuario.builder()
                        .id(9991)
                        .nome("USUARIO 1 DO AA 999")
                        .cargo(umCargoVendedorTelevendas())
                        .situacao(ESituacao.A)
                        .build(),
                Usuario.builder()
                        .id(9992)
                        .nome("USUARIO 2 DO AA 999")
                        .cargo(umCargoVendedorD2d())
                        .situacao(ESituacao.I)
                        .build(),
                Usuario.builder()
                        .id(9993)
                        .nome("USUARIO 3 DO AA 999")
                        .cargo(umCargoSocioPrincipal())
                        .situacao(ESituacao.I)
                        .build(),
                Usuario.builder()
                        .id(9994)
                        .nome("USUARIO 4 DO AA 999")
                        .cargo(umCargoSocioPrincipal())
                        .situacao(ESituacao.I)
                        .build(),
                Usuario.builder()
                        .id(9995)
                        .nome("USUARIO 5 DO AA 999")
                        .situacao(ESituacao.I)
                        .cargo(umCargoSupervisor())
                        .build()
        );
    }

    public static EquipeVendaDto umaEquipeVendasDto() {
        return EquipeVendaDto.builder()
            .id(999)
            .descricao("Descrição")
            .canalVenda("D2D")
            .build();
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

    public static List<UsuarioAgenteAutorizadoResponse> todosUsuariosDoAgenteAutorizado1300ComEquipesDeVendas() {
        return List.of(
                new UsuarioAgenteAutorizadoResponse(130, "JOÃO MARINHO DA SILVA DOS SANTOS", 999),
                new UsuarioAgenteAutorizadoResponse(131, "ANTONIO ARYLDO DE SOUZA RODRIGUES", 980),
                new UsuarioAgenteAutorizadoResponse(132, "LUIZ BARRETTO DE AZEVEDO NETO", 755),
                new UsuarioAgenteAutorizadoResponse(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR", 999),
                new UsuarioAgenteAutorizadoResponse(134, "PAULO JUNIO COLARES MIRANDA", null),
                new UsuarioAgenteAutorizadoResponse(135, "LEONARDO DOS SANTOS GONCALVES REIS", 999)
        );
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
