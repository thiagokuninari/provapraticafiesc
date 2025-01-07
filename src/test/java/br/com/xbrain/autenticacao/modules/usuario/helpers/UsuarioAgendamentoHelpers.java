package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.EAcao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeVendasSupervisionadasResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioAgendamentoHelpers {

    public static ConfiguracaoAgendaFiltros umaConfiguracaoAgendaFiltros() {
        return ConfiguracaoAgendaFiltros.builder()
            .tipoConfiguracao(ETipoConfiguracao.CANAL)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .nivel(CodigoNivel.OPERACAO)
            .build();
    }

    public static ConfiguracaoAgendaRequest umaConfiguracaoAgendaRequestEstruturaCompleta() {
        return ConfiguracaoAgendaRequest.builder()
            .qtdHorasAdicionais(100)
            .tipoConfiguracao(ETipoConfiguracao.SUBCANAL)
            .canal(ECanal.D2D_PROPRIO)
            .subcanalId(1)
            .nivel(CodigoNivel.OPERACAO)
            .build();
    }

    public static ConfiguracaoAgendaRequest umaConfiguracaoAgendaRequest() {
        return ConfiguracaoAgendaRequest.builder()
            .qtdHorasAdicionais(100)
            .tipoConfiguracao(ETipoConfiguracao.CANAL)
            .nivel(CodigoNivel.OPERACAO)
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
            .nivel(CodigoNivel.OPERACAO)
            .build();
    }

    public static ConfiguracaoAgendaRequest umaConfiguracaoAgendaRequest(ETipoCanal subcanal) {
        return ConfiguracaoAgendaRequest.builder()
            .qtdHorasAdicionais(100)
            .subcanalId(subcanal.getId())
            .canal(ECanal.D2D_PROPRIO)
            .nivel(CodigoNivel.OPERACAO)
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
            .nivel(CodigoNivel.AGENTE_AUTORIZADO)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .tipoConfiguracao(ETipoConfiguracao.ESTRUTURA)
            .build();
    }

    public static ConfiguracaoAgendaResponse umaConfiguracaoAgendaResponse() {
        return ConfiguracaoAgendaResponse.builder()
            .qtdHorasAdicionais(100)
            .tipoConfiguracao(ETipoConfiguracao.CANAL)
            .nivel(CodigoNivel.OPERACAO)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .situacao(ESituacao.A.getDescricao())
            .dataCadastro(LocalDateTime.of(2024, 1, 1, 12, 30))
            .build();
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgendaPadrao() {
        return ConfiguracaoAgendaReal.builder()
            .id(9)
            .qtdHorasAdicionais(24)
            .situacao(ESituacao.A)
            .tipoConfiguracao(ETipoConfiguracao.PADRAO)
            .build();
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgendaUltimaOrdemAsc() {
        return ConfiguracaoAgendaReal.builder()
            .id(8)
            .qtdHorasAdicionais(60)
            .situacao(ESituacao.I)
            .tipoConfiguracao(ETipoConfiguracao.SUBCANAL)
            .subcanalId(4)
            .dataCadastro(LocalDateTime.of(2024, 1, 8, 12, 30, 0))
            .build();
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgendaUltimaOrdemDesc() {
        return ConfiguracaoAgendaReal.builder()
            .id(1)
            .qtdHorasAdicionais(15)
            .situacao(ESituacao.A)
            .tipoConfiguracao(ETipoConfiguracao.CANAL)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .dataCadastro(LocalDateTime.of(2024, 1, 1, 12, 30, 0))
            .build();
    }

    public static ConfiguracaoAgendaRealHistorico umaConfiguracaoAgendaHistorico() {
        return ConfiguracaoAgendaRealHistorico.builder()
            .configuracao(umaConfiguracaoAgenda())
            .dataAcao(LocalDateTime.of(2024, 12, 30, 12, 30))
            .acao(EAcao.CADASTRO)
            .usuarioAcaoId(2)
            .usuarioAcaoNome("Thiago")
            .build();
    }

    public static ConfiguracaoAgendaRealHistoricoResponse umaConfiguracaoAgendaHistoricoResponse() {
        return new ConfiguracaoAgendaRealHistoricoResponse(
            "Cadastrado",
            LocalDateTime.of(2024, 12, 30, 12, 30),
            2,
            "Thiago",
            null);
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgendaEstruturaCompleta() {
        return ConfiguracaoAgendaReal.builder()
            .qtdHorasAdicionais(100)
            .tipoConfiguracao(ETipoConfiguracao.SUBCANAL)
            .canal(ECanal.D2D_PROPRIO)
            .nivel(CodigoNivel.OPERACAO)
            .subcanalId(1)
            .situacao(ESituacao.A)
            .usuarioCadastroId(2)
            .usuarioCadastroNome("Thiago")
            .dataCadastro(LocalDateTime.of(2024, 1, 1, 12, 30))
            .build();
    }

    public static ConfiguracaoAgendaReal umaConfiguracaoAgenda() {
        return ConfiguracaoAgendaReal.builder()
            .qtdHorasAdicionais(100)
            .nivel(CodigoNivel.OPERACAO)
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
            .canal(ECanal.AGENTE_AUTORIZADO)
            .nivel(CodigoNivel.AGENTE_AUTORIZADO)
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
            .canal(ECanal.D2D_PROPRIO)
            .nivel(CodigoNivel.OPERACAO)
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
            .nivel(CodigoNivel.OPERACAO)
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

    public static Usuario umUsuarioId9991() {
        return Usuario.builder()
            .id(9991)
            .nome("USUARIO 1 DO AA 999")
            .cargo(umCargoVendedorTelevendas())
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuarioId9992() {
        return Usuario.builder()
            .id(9992)
            .nome("USUARIO 2 DO AA 999")
            .cargo(umCargoVendedorTelevendas())
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuarioId9993() {
        return Usuario.builder()
            .id(9993)
            .nome("USUARIO 3 DO AA 999")
            .cargo(umCargoVendedorTelevendas())
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuarioId9994() {
        return Usuario.builder()
            .id(9994)
            .nome("USUARIO 4 DO AA 999")
            .cargo(umCargoVendedorTelevendas())
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuarioId9995() {
        return Usuario.builder()
            .id(9995)
            .nome("USUARIO 5 DO AA 999")
            .cargo(umCargoVendedorTelevendas())
            .situacao(ESituacao.A)
            .build();
    }

    public static List<Usuario> usuariosDoAgenteAutorizado999() {
        return List.of(
            umUsuarioId9991(),
            umUsuarioId9992(),
            umUsuarioId9993(),
            umUsuarioId9994(),
            umUsuarioId9995()
        );
    }

    public static List<UsuarioAgenteAutorizadoResponse> listaUsuariosDoAgenteAutorizado999() {
        return usuariosDoAgenteAutorizado999()
            .stream()
            .map(u -> new UsuarioAgenteAutorizadoResponse(u.getId(), u.getNome(),
                u.getCargoCodigo() == CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS ? 999 : null))
            .collect(Collectors.toList());
    }

    public static EquipeVendaDto umaEquipeVendasDto() {
        return EquipeVendaDto.builder()
            .id(999)
            .descricao("Descrição")
            .canalVenda("D2D")
            .build();
    }

    public static EquipeVendasSupervisionadasResponse umaEquipeDeVendas() {
        return EquipeVendasSupervisionadasResponse.builder()
            .id(999)
            .descricao("EQUIPE DE VENDAS DO AA 999")
            .build();
    }

    public static List<EquipeVendaUsuarioResponse> umaListaUsuariosDaEquipeVenda() {
        return List.of(
            EquipeVendaUsuarioResponse.builder()
                .usuarioId(1)
                .usuarioNome("RENATO")
                .equipeVendaId(10)
                .build(),
            EquipeVendaUsuarioResponse.builder()
                .usuarioId(2)
                .usuarioNome("JOAO")
                .equipeVendaId(10)
                .build()
        );
    }

    public static Usuario umVendedorTelevendas() {
        return Usuario.builder()
            .id(130)
            .nome("JOÃO MARINHO DA SILVA DOS SANTOS")
            .cargo(umCargoVendedorTelevendas())
            .build();
    }

    public static Usuario umVendedorD2d() {
        return Usuario.builder()
            .id(131)
            .nome("JOÃO MARINHO DA SILVA DOS SANTOS JÚNIOR")
            .cargo(umCargoVendedorD2d())
            .build();
    }

    public static Usuario umSocioPrincipal() {
        return Usuario.builder()
            .id(132)
            .nome("JOSÉ MARINHO DA SILVA DOS SANTOS")
            .cargo(umCargoSocioPrincipal())
            .build();
    }

    public static Usuario umSupervisor() {
        return Usuario.builder()
            .id(133)
            .nome("JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR")
            .cargo(umCargoSupervisor())
            .build();
    }

    public static Usuario umSuperviorTelevendas() {
        return Usuario.builder()
            .id(134)
            .nome("MARIA DA SILVA SAURO SANTOS")
            .cargo(umCargoVendedorTelevendas())
            .build();
    }

    public static Usuario umSupervisor2() {
        return Usuario.builder()
            .id(135)
            .nome("MARCOS AUGUSTO DA SILVA SANTOS")
            .cargo(umCargoSupervisor())
            .build();
    }

    public static List<Usuario> usuariosDoAgenteAutorizado1300() {
        return List.of(
            umVendedorTelevendas(),
            umVendedorD2d(),
            umSocioPrincipal(),
            umSupervisor(),
            umSuperviorTelevendas(),
            umSupervisor2()
        );
    }

    public static List<UsuarioAgenteAutorizadoResponse> usuariosDoAa1300ComEquipesDeVendas() {
        return List.of(
            new UsuarioAgenteAutorizadoResponse(130, "JOÃO MARINHO DA SILVA DOS SANTOS", 999),
            new UsuarioAgenteAutorizadoResponse(131, "ANTONIO ARYLDO DE SOUZA RODRIGUES", 980),
            new UsuarioAgenteAutorizadoResponse(132, "LUIZ BARRETTO DE AZEVEDO NETO", 755),
            new UsuarioAgenteAutorizadoResponse(133, "JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR", 999),
            new UsuarioAgenteAutorizadoResponse(134, "PAULO JUNIO COLARES MIRANDA", null),
            new UsuarioAgenteAutorizadoResponse(135, "LEONARDO DOS SANTOS GONCALVES REIS", 999)
        );
    }

    public static Cargo umCargoVendedorTelevendas() {
        return Cargo.builder()
            .id(1000)
            .nome("AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS")
            .codigo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS)
            .nivel(Nivel.builder().codigo(CodigoNivel.AGENTE_AUTORIZADO).build())
            .build();
    }

    public static Cargo umCargoVendedorD2d() {
        return Cargo.builder()
            .id(1000)
            .nome("AGENTE_AUTORIZADO_VENDEDOR_D2D")
            .codigo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_D2D)
            .nivel(Nivel.builder().codigo(CodigoNivel.AGENTE_AUTORIZADO).build())
            .build();
    }

    public static Cargo umCargoSupervisor() {
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

    public static UsuarioPermissaoResponse umaPermissaoDeVendaPresencial() {
        return UsuarioPermissaoResponse.builder()
            .permissoesCargoDepartamento(List.of())
            .permissoesEspeciais(List.of(
                FuncionalidadeResponse.convertFrom(Funcionalidade.builder()
                    .id(100)
                    .role("VDS_TABULACAO_MANUAL")
                    .aplicacao(umaAplicacao())
                    .build())
            ))
            .build();
    }

    public static UsuarioPermissaoResponse umaPermissaoDeVendaDiscadora() {
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

    public static UsuarioPermissaoResponse umaPermissaoDeVendaClickToCall() {
        return UsuarioPermissaoResponse.builder()
            .permissoesCargoDepartamento(List.of())
            .permissoesEspeciais(List.of(
                FuncionalidadeResponse.convertFrom(Funcionalidade.builder()
                    .id(100)
                    .role("VDS_TABULACAO_CLICKTOCALL")
                    .aplicacao(umaAplicacao())
                    .build())
            ))
            .build();
    }

    public static UsuarioPermissaoResponse umaPermissaoVisualizarGeral() {
        return UsuarioPermissaoResponse.builder()
            .permissoesCargoDepartamento(List.of())
            .permissoesEspeciais(List.of(
                FuncionalidadeResponse.convertFrom(Funcionalidade.builder()
                    .id(100)
                    .role("AUT_VISUALIZAR_GERAL")
                    .aplicacao(umaAplicacao())
                    .build())
            ))
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticadoSupervisor() {
        return UsuarioAutenticado.builder()
            .id(135)
            .nome("MARCOS AUGUSTO DA SILVA SANTOS")
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR)
            .usuario(umUsuario())
            .build();
    }

    private static Usuario umUsuario() {
        return Usuario.builder()
            .id(135)
            .nome("MARCOS AUGUSTO DA SILVA SANTOS")
            .cargo(Cargo.builder().codigo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR).build())
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticadoCargoCoordenadorComercial() {
        return UsuarioAutenticado.builder()
            .id(101)
            .nome("COORDENADOR")
            .cargoCodigo(CodigoCargo.COORDENADOR_OPERACAO)
            .departamentoCodigo(CodigoDepartamento.COMERCIAL)
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticadoSocio() {
        return UsuarioAutenticado.builder()
            .id(100)
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO)
            .nome("José")
            .build();
    }

    private static Aplicacao umaAplicacao() {
        return Aplicacao.builder()
            .id(100)
            .nome("VENDAS")
            .codigo(CodigoAplicacao.VDS)
            .build();
    }

}
