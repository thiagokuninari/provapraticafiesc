package br.com.xbrain.autenticacao.modules.permissao.helper;

import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeRequest;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;

import java.util.Arrays;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.*;

public class CargoDepartamentoFuncionalidadeHelper {

    public static CargoDepartamentoFuncionalidade umCargoDeptoFuncionalidadeDeSocio(Integer cargoDeptoFuncionalidadeId,
                                                                                    Funcionalidade funcionalidade) {
        return CargoDepartamentoFuncionalidade.builder()
            .id(cargoDeptoFuncionalidadeId)
            .cargo(umCargoAaSocio())
            .departamento(umDepartamentoAa())
            .funcionalidade(funcionalidade)
            .build();
    }

    public static CargoDepartamentoFuncionalidade umCargoDeptoFuncionalidadeDeConsultor(Integer cargoDeptoFuncionalidadeId,
                                                                                        Funcionalidade funcionalidade) {
        return CargoDepartamentoFuncionalidade.builder()
            .id(cargoDeptoFuncionalidadeId)
            .cargo(umCargoMsoConsultor())
            .departamento(umDepartamentoComercial())
            .funcionalidade(funcionalidade)
            .build();
    }

    public static CargoDepartamentoFuncionalidade umCargoDeptoFuncionalidadeDeVendedor(Integer cargoDeptoFuncionalidadeId,
                                                                                       Funcionalidade funcionalidade) {
        return CargoDepartamentoFuncionalidade.builder()
            .id(cargoDeptoFuncionalidadeId)
            .cargo(umCargoVendedorOperacao())
            .departamento(umDepartamentoComercial())
            .funcionalidade(funcionalidade)
            .build();
    }

    public static CargoDepartamentoFuncionalidade umCargoDeptoFuncionalidadeDeAnalista(Integer cargoDeptoFuncionalidadeId,
                                                                                       Funcionalidade funcionalidade) {
        return CargoDepartamentoFuncionalidade.builder()
            .id(cargoDeptoFuncionalidadeId)
            .cargo(umCargoAnalistaOperacao())
            .departamento(umDepartamentoAdministrativo())
            .funcionalidade(funcionalidade)
            .build();
    }

    public static CargoDepartamentoFuncionalidade umCargoDeptoFuncionalidadeDeAdministrador(Integer cargoDeptoFuncionalidadeId,
                                                                                            Funcionalidade funcionalidade) {
        return CargoDepartamentoFuncionalidade.builder()
            .id(cargoDeptoFuncionalidadeId)
            .cargo(umCargoAdministrador())
            .departamento(umDepartamentoAdministrador())
            .funcionalidade(funcionalidade)
            .build();
    }

    public static List<CargoDepartamentoFuncionalidade> umaListaDeCargoDepartamentoFuncionalidadeDeSocio() {
        return List.of(
            umCargoDeptoFuncionalidadeDeSocio(869, funcionalidadeGerenciarPausasAgendadas()),
            umCargoDeptoFuncionalidadeDeSocio(144, funcionalidadeAbrirChamado()),
            umCargoDeptoFuncionalidadeDeSocio(813, funcionalidadeSolicitarRamal()),
            umCargoDeptoFuncionalidadeDeSocio(870, funcionalidadeVisualizarRamalUsuario()),
            umCargoDeptoFuncionalidadeDeSocio(815, funcionalidadeDistribuirAgendamentosProprietarios()),
            umCargoDeptoFuncionalidadeDeSocio(825, funcionalidadeGerenciarDistribuicaoMailingSegmentacao()),
            umCargoDeptoFuncionalidadeDeSocio(818, funcionalidadeGerenciarCarteiras()),
            umCargoDeptoFuncionalidadeDeSocio(887, funcionalidadeVisualizarCampanha()),
            umCargoDeptoFuncionalidadeDeSocio(886, funcionalidadeVisualizarDistribuicaoMailing()),
            umCargoDeptoFuncionalidadeDeSocio(884, funcionalidadeVisualizarGeral()),
            umCargoDeptoFuncionalidadeDeSocio(885, funcionalidadeVisualizarImportacaoMailing()),
            umCargoDeptoFuncionalidadeDeSocio(888, funcionalidadeVisualizarLayoutMailing()),
            umCargoDeptoFuncionalidadeDeSocio(819, funcionalidadeVisualizarAtaReuniao()),
            umCargoDeptoFuncionalidadeDeSocio(816, funcionalidadeVisualizarEDownloadPainelGestaoAa()),
            umCargoDeptoFuncionalidadeDeSocio(895, funcionalidadeCriarTratativas()),
            umCargoDeptoFuncionalidadeDeSocio(821, funcionalidadeCriarTratativas()),
            umCargoDeptoFuncionalidadeDeSocio(807, funcionalidadeDashboardNumerosTabulacao()),
            umCargoDeptoFuncionalidadeDeSocio(881, funcionalidadeDashboardNumerosTabulacao()),
            umCargoDeptoFuncionalidadeDeSocio(808, funcionalidadeDashboardRelatorioTabulacao()),
            umCargoDeptoFuncionalidadeDeSocio(882, funcionalidadeDashboardRelatorioTabulacao()),
            umCargoDeptoFuncionalidadeDeSocio(817, funcionalidadeDesbloquearVenda()),
            umCargoDeptoFuncionalidadeDeSocio(890, funcionalidadeRelatorioExportarAcompanhamentoVendas()),
            umCargoDeptoFuncionalidadeDeSocio(893, funcionalidadeRelatorioGerenciamentoOperacional()),
            umCargoDeptoFuncionalidadeDeSocio(883, funcionalidadeRelatorioResumoMailing()),
            umCargoDeptoFuncionalidadeDeSocio(809, funcionalidadeRelatorioResumoMailing()),
            umCargoDeptoFuncionalidadeDeSocio(889, funcionalidadeRelatorioTabulacoesRealizadas()),
            umCargoDeptoFuncionalidadeDeSocio(810, funcionalidadeRelatorioTicketMedioAnalitico()),
            umCargoDeptoFuncionalidadeDeSocio(891, funcionalidadeRelatorioTicketMedioAnalitico()),
            umCargoDeptoFuncionalidadeDeSocio(892, funcionalidadeRelatorioTicketMedioPorVendedor()),
            umCargoDeptoFuncionalidadeDeSocio(876, funcionalidadeVisualizarAcompanhamentoVenda()),
            umCargoDeptoFuncionalidadeDeSocio(878, funcionalidadeVisualizarAgendamento()),
            umCargoDeptoFuncionalidadeDeSocio(879, funcionalidadeVisualizarConsultaHp()),
            umCargoDeptoFuncionalidadeDeSocio(871, funcionalidadeVisualizarGeral()),
            umCargoDeptoFuncionalidadeDeSocio(823, funcionalidadeVisualizarRelatorioConsultaEndereco()),
            umCargoDeptoFuncionalidadeDeSocio(875, funcionalidadeVisualizarTabulacaoManual()),
            umCargoDeptoFuncionalidadeDeSocio(880, funcionalidadeVisualizarTabulacaoPersonalizada()),
            umCargoDeptoFuncionalidadeDeSocio(877, funcionalidadeVisualizarTratamentoVenda()),
            umCargoDeptoFuncionalidadeDeSocio(894, funcionalidadeVisualizarTratativas()),
            umCargoDeptoFuncionalidadeDeSocio(820, funcionalidadeVisualizarTratativas()),
            umCargoDeptoFuncionalidadeDeSocio(873, funcionalidadeVisualizarVendaClaro()),
            umCargoDeptoFuncionalidadeDeSocio(872, funcionalidadeVisualizarVendaNet()),
            umCargoDeptoFuncionalidadeDeSocio(872, funcionalidadeVisualizarVendaNet()),
            umCargoDeptoFuncionalidadeDeSocio(874, funcionalidadeVisualizarViabilidadeEndereco())
        );
    }

    public static List<CargoDepartamentoFuncionalidade> umaListaDeCargoDepartamentoFuncionalidadeDeConsultor() {
        return List.of(
            umCargoDeptoFuncionalidadeDeConsultor(548, funcionalidadeGerenciarEquipeVendaAtivoLocal()),
            umCargoDeptoFuncionalidadeDeConsultor(450, funcionalidadeGerenciarSites()),
            umCargoDeptoFuncionalidadeDeConsultor(459, funcionalidadeInativarUsuariosSemAcesso()),
            umCargoDeptoFuncionalidadeDeConsultor(452, funcionalidadeVisualizarSites()),
            umCargoDeptoFuncionalidadeDeConsultor(655, funcionalidadeCadastrarTratativaEduconexao()),
            umCargoDeptoFuncionalidadeDeConsultor(654, funcionalidadeDesbloquearTratativas()),
            umCargoDeptoFuncionalidadeDeConsultor(652, funcionalidadeDetalharTratativa()),
            umCargoDeptoFuncionalidadeDeConsultor(653, funcionalidadeGerenciarFornecedores()),
            umCargoDeptoFuncionalidadeDeConsultor(651, funcionalidadePriorizarTratativa()),
            umCargoDeptoFuncionalidadeDeConsultor(650, funcionalidadeVisualizarAcompanhamento()),
            umCargoDeptoFuncionalidadeDeConsultor(657, funcionalidadeVisualizarDashAcessoIndevido()),
            umCargoDeptoFuncionalidadeDeConsultor(656, funcionalidadeVisualizarFuncionalidadeGerenciarGrupos()),
            umCargoDeptoFuncionalidadeDeConsultor(154, funcionalidadeVisualizarTodosChamados()),
            umCargoDeptoFuncionalidadeDeConsultor(206, funcionalidadeCriarSolicitacoesComunicados()),
            umCargoDeptoFuncionalidadeDeConsultor(458, funcionalidadeConsultarDirecionamentosPapPremium()),
            umCargoDeptoFuncionalidadeDeConsultor(1672, funcionalidadeGerenciarDescritivoMailing()),
            umCargoDeptoFuncionalidadeDeConsultor(454, funcionalidadeVisualizarTodosSites()),
            umCargoDeptoFuncionalidadeDeConsultor(626, funcionalidadeMonitoriaSuperiorDoSupervisor()),
            umCargoDeptoFuncionalidadeDeConsultor(649, funcionalidadeConfigEnvioClaroQueEuIndicoFeeder()),
            umCargoDeptoFuncionalidadeDeConsultor(647, funcionalidadeGerenciarDistribuicaoMailingSegmentacao()),
            umCargoDeptoFuncionalidadeDeConsultor(646, funcionalidadeGerenciarScriptsMailing()),
            umCargoDeptoFuncionalidadeDeConsultor(543, funcionalidadeVisualizarD2dMailing()),
            umCargoDeptoFuncionalidadeDeConsultor(544, funcionalidadeVisualizarLayoutMailingD2d()),
            umCargoDeptoFuncionalidadeDeConsultor(648, funcionalidadeVisualizarRelatorioOperadorXMailing()),
            umCargoDeptoFuncionalidadeDeConsultor(406, funcionalidadeConsultarDadosVendedorNetSales()),
            umCargoDeptoFuncionalidadeDeConsultor(1669, funcionalidadeDownloadEListagemContratosPortalAntigo()),
            umCargoDeptoFuncionalidadeDeConsultor(400, funcionalidadeGerenciarAtaReuniao()),
            umCargoDeptoFuncionalidadeDeConsultor(433, funcionalidadeRelatorioConsultaEndereco()),
            umCargoDeptoFuncionalidadeDeConsultor(414, funcionalidadeRelatorioExportarTabulacoesRealizadas()),
            umCargoDeptoFuncionalidadeDeConsultor(409, funcionalidadeRelatorioGerenciamentoOperacionalGeral()),
            umCargoDeptoFuncionalidadeDeConsultor(423, funcionalidadeRelatorioGerenciamentoOperacionalPorParceiro()),
            umCargoDeptoFuncionalidadeDeConsultor(490, funcionalidadeConsultarIndicacoesPremium()),
            umCargoDeptoFuncionalidadeDeConsultor(547, funcionalidadeCriarTratativas()),
            umCargoDeptoFuncionalidadeDeConsultor(540, funcionalidadeDashboardNumerosTabulacao()),
            umCargoDeptoFuncionalidadeDeConsultor(541, funcionalidadeDashboardRelatorioTabulacao()),
            umCargoDeptoFuncionalidadeDeConsultor(542, funcionalidadeRelatorioResumoMailing()),
            umCargoDeptoFuncionalidadeDeConsultor(486, funcionalidadeRemoverPlantao()),
            umCargoDeptoFuncionalidadeDeConsultor(549, funcionalidadeVisualizarRelatorioConsultaEndereco()),
            umCargoDeptoFuncionalidadeDeConsultor(546, funcionalidadeVisualizarTratativas()),
            umCargoDeptoFuncionalidadeDeConsultor(622, funcionalidadeVisualizarCondominio()),
            umCargoDeptoFuncionalidadeDeConsultor(480, funcionalidadeSelecionarSupervisor()),
            umCargoDeptoFuncionalidadeDeConsultor(474, funcionalidadeSelecionarSupervisor())
        );
    }

    public static List<CargoDepartamentoFuncionalidade> umaListaDeCargoDepartamentoFuncionalidadeDeVendedor() {
        return List.of(
            umCargoDeptoFuncionalidadeDeVendedor(1346, funcionalidadeObrigatorioSelecionarCidadesDeAtuacao()),
            umCargoDeptoFuncionalidadeDeVendedor(132, funcionalidadeAbrirChamadoCrn()),
            umCargoDeptoFuncionalidadeDeVendedor(135, funcionalidadeVisualizarChamadoCrn()),
            umCargoDeptoFuncionalidadeDeVendedor(1348, funcionalidadeMonitoriaVendedor()),
            umCargoDeptoFuncionalidadeDeVendedor(1347, funcionalidadeVisualizarChecklists()),
            umCargoDeptoFuncionalidadeDeVendedor(1356, funcionalidadeConsultarIndicacoesPremium()),
            umCargoDeptoFuncionalidadeDeVendedor(1353, funcionalidadeCriarTratativas()),
            umCargoDeptoFuncionalidadeDeVendedor(1351, funcionalidadeGerenciarAgendaVisita()),
            umCargoDeptoFuncionalidadeDeVendedor(1350, funcionalidadeGerenciarCondominio()),
            umCargoDeptoFuncionalidadeDeVendedor(1351, funcionalidadeVisualizarTratativas())
        );
    }

    public static List<CargoDepartamentoFuncionalidade> umaListaDeFuncionalidadesRepetidas() {
        return List.of(
            umCargoDeptoFuncionalidadeDeAnalista(20, funcionalidadeAaAprovacaoOperacao()),
            umCargoDeptoFuncionalidadeDeAnalista(30, funcionalidadeAaAprovacaoMso()),
            umCargoDeptoFuncionalidadeDeAnalista(40, funcionalidadeGerenciarAas()),
            umCargoDeptoFuncionalidadeDeAnalista(50, funcionalidadeDescredenciamentoDeAa())
        );
    }

    public static List<CargoDepartamentoFuncionalidade> umaListaDeCargoDepartamentoFuncionalidadeDeAdministrador() {
        return List.of(
            umCargoDeptoFuncionalidadeDeAdministrador(1, funcionalidadeVisualizarGeral()),
            umCargoDeptoFuncionalidadeDeAdministrador(2, funcionalidadeGerenciarEquipeVenda()),
            umCargoDeptoFuncionalidadeDeAdministrador(3, funcionalidadeGerenciarEquipeTecnica()),
            umCargoDeptoFuncionalidadeDeAdministrador(4, funcionalidadeVisualizarNoticias()),
            umCargoDeptoFuncionalidadeDeAdministrador(5, funcionalidadeAaAprovacaoOperacao()),
            umCargoDeptoFuncionalidadeDeAdministrador(6, funcionalidadeAaAprovacaoMso()),
            umCargoDeptoFuncionalidadeDeAdministrador(7, funcionalidadeGerenciarAas()),
            umCargoDeptoFuncionalidadeDeAdministrador(8, funcionalidadeDescredenciamentoDeAa()),
            umCargoDeptoFuncionalidadeDeAdministrador(9, funcionalidadeVisualizarComissionamento()),
            umCargoDeptoFuncionalidadeDeAdministrador(10, funcionalidadeCaptacaoAaExtracao())
        );
    }

    private static CargoDepartamentoFuncionalidadeRequest permissoes(List<Integer> funcionalidadeIds) {
        var request = new CargoDepartamentoFuncionalidadeRequest();
        request.setCargoId(1);
        request.setDepartamentoId(1);
        request.setFuncionalidadesIds(funcionalidadeIds);
        return request;
    }

    public static CargoDepartamentoFuncionalidadeRequest novasFuncionalidades() {
        return permissoes(Arrays.asList(1, 2, 3, 4));
    }

    public static CargoDepartamentoFuncionalidadeRequest funcionalidadesRepetidas() {
        return permissoes(Arrays.asList(12, 13, 16, 17));
    }

    public static CargoDepartamentoFuncionalidade umCargoDepartamentoFuncionalidade(Integer id) {
        return CargoDepartamentoFuncionalidade.builder()
            .id(id)
            .cargo(umCargoVendedorOperacao())
            .departamento(umDepartamento(3, "Comercial"))
            .funcionalidade(funcionalidadeRelatorioGerenciamentoOperacional())
            .build();
    }
}
