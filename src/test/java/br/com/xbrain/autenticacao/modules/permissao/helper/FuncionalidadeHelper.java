package br.com.xbrain.autenticacao.modules.permissao.helper;

import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.experimental.UtilityClass;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao.*;
import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeCanalHelper.umaFuncionalidadeCanal;

@UtilityClass
public class FuncionalidadeHelper {

    public static Funcionalidade funcionalidadeGerenciarPausasAgendadas() {
        return Funcionalidade.builder()
            .id(2031)
            .nome("Gerenciar Pausas Agendadas")
            .role("AUT_2031")
            .aplicacao(umaAplicacaoAutenticacao())
            .canais(List.of(
                umaFuncionalidadeCanal(2031, ECanal.ATIVO_PROPRIO),
                umaFuncionalidadeCanal(2031, ECanal.AGENTE_AUTORIZADO)
                ))
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeAbrirChamado() {
        return Funcionalidade.builder()
            .id(14000)
            .nome("Abrir chamado")
            .role("CHM_ABRIR_CHAMADO")
            .aplicacao(umaAplicacaoChamado())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeSolicitarRamal() {
        return Funcionalidade.builder()
            .id(2033)
            .nome("Solicitar Ramal")
            .role("CTR_2033")
            .aplicacao(umaAplicacaoControle())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarRamalUsuario() {
        return Funcionalidade.builder()
            .id(2015)
            .nome("Visualizar Ramal Usuário")
            .role("CTR_VISUALIZAR_RAMAL_USUARIO")
            .aplicacao(umaAplicacaoControle())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeDistribuirAgendamentosProprietarios() {
        return Funcionalidade.builder()
            .id(5013)
            .nome("Distribuir Agendamentos Proprietários")
            .role("MLG_5013")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarDistribuicaoMailingSegmentacao() {
        return Funcionalidade.builder()
            .id(5018)
            .nome("Gerenciar Distribuição Mailing Segmentação")
            .role("MLG_5018")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarCarteiras() {
        return Funcionalidade.builder()
            .id(5009)
            .nome("Gerenciar carteiras")
            .role("MLG_5009")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarCampanha() {
        return Funcionalidade.builder()
            .id(5003)
            .nome("Visualizar Campanha")
            .role("MLG_5003")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarDistribuicaoMailing() {
        return Funcionalidade.builder()
            .id(5002)
            .nome("Visualizar Distribuição de Mailing")
            .role("MLG_5002")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarGeral() {
        return Funcionalidade.builder()
            .id(5000)
            .nome("Visualizar Geral")
            .role("MLG_VISUALIZAR_GERAL")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarImportacaoMailing() {
        return Funcionalidade.builder()
            .id(5001)
            .nome("Visualizar Importação de Mailing")
            .role("MLG_VISUALIZAR_IMP_MAILING")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarLayoutMailing() {
        return Funcionalidade.builder()
            .id(5004)
            .nome("Visualizar Layout de Mailing")
            .role("MLG_VISUALIZAR_TIPOS_MAILING")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarAtaReuniao() {
        return Funcionalidade.builder()
            .id(241)
            .nome("Gerenciar a Ata de Reunião")
            .role("POL_241")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarAtaReuniao() {
        return Funcionalidade.builder()
            .id(242)
            .nome("Visualizar Ata de Reunião")
            .role("POL_242")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarEDownloadPainelGestaoAa() {
        return Funcionalidade.builder()
            .id(816)
            .nome("Visualizar e fazer download do Painel de Gestão do Agente Autorizado")
            .role("POL_240")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeCriarTratativas() {
        return Funcionalidade.builder()
            .id(3052)
            .nome("Criar Tratativas")
            .role("VDS_3052")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeDashboardNumerosTabulacao() {
        return Funcionalidade.builder()
            .id(3019)
            .nome("Dashboard - Números Tabulação")
            .role("VDS_3019")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeDashboardRelatorioTabulacao() {
        return Funcionalidade.builder()
            .id(3020)
            .nome("Dashboard - Relatório Tabulação")
            .role("VDS_3020")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeDesbloquearVenda() {
        return Funcionalidade.builder()
            .id(3039)
            .nome("Desbloquear venda")
            .role("VDS_3039")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRelatorioExportarAcompanhamentoVendas() {
        return Funcionalidade.builder()
            .id(3017)
            .nome("Relatório - Exportar acompanhamento de Vendas")
            .role("VDS_REL_EXPOR_ACOMPANHAMENTO_VENDAS")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRelatorioGerenciamentoOperacional() {
        return Funcionalidade.builder()
            .id(3024)
            .nome("Relatório - Gerenciamento Operacional")
            .role("VDS_3024")
            .aplicacao(umaAplicacaoVendas())
            .canais(List.of(
                umaFuncionalidadeCanal(3024, ECanal.D2D_PROPRIO)
            ))
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRelatorioResumoMailing() {
        return Funcionalidade.builder()
            .id(3021)
            .nome("Relatório - Resumo de Mailing")
            .role("VDS_3021")
            .aplicacao(umaAplicacaoVendas())
            .canais(List.of(
                umaFuncionalidadeCanal(3021, ECanal.AGENTE_AUTORIZADO),
                umaFuncionalidadeCanal(3021, ECanal.ATIVO_PROPRIO)
            ))
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRelatorioTabulacoesRealizadas() {
        return Funcionalidade.builder()
            .id(3016)
            .nome("Relatório - Tabulações Realizadas")
            .role("VDS_REL_TABULACOES_REALIZADAS")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRelatorioTicketMedioAnalitico() {
        return Funcionalidade.builder()
            .id(3022)
            .nome("Relatório - Ticket Médio Analítico")
            .role("VDS_3022")
            .aplicacao(umaAplicacaoVendas())
            .canais(List.of(
                umaFuncionalidadeCanal(3022, ECanal.AGENTE_AUTORIZADO)
            ))
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRelatorioTicketMedioPorVendedor() {
        return Funcionalidade.builder()
            .id(3023)
            .nome("Relatório - Ticket Médio por Vendedor")
            .role("VDS_3023")
            .aplicacao(umaAplicacaoVendas())
            .canais(List.of(
                umaFuncionalidadeCanal(3023, ECanal.AGENTE_AUTORIZADO)
            ))
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarAcompanhamentoVenda() {
        return Funcionalidade.builder()
            .id(3007)
            .nome("Visualizar Acompanhamento de Venda")
            .role("VDS_VISUALIZAR_ACOM_VENDA")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarAgendamento() {
        return Funcionalidade.builder()
            .id(3009)
            .nome("Visualizar Agendamento")
            .role("VDS_VISUALIZAR_AGENDAMENTO")
            .aplicacao(umaAplicacaoVendas())
            .canais(List.of(
                umaFuncionalidadeCanal(3009, ECanal.ATIVO_PROPRIO)
            ))
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarConsultaHp() {
        return Funcionalidade.builder()
            .id(3010)
            .nome("Visualizar Consulta de HP")
            .role("VDS_VISUALIZAR_CONSULTA_HP")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarRelatorioConsultaEndereco() {
        return Funcionalidade.builder()
            .id(3059)
            .nome("Visualizar Relatório Consulta de Endereço")
            .role("VDS_3059")
            .aplicacao(umaAplicacaoVendas())
            .canais(List.of(
                umaFuncionalidadeCanal(3059, ECanal.ATIVO_PROPRIO)
            ))
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarTabulacaoManual() {
        return Funcionalidade.builder()
            .id(3004)
            .nome("Visualizar Tabulação Manual")
            .role("VDS_TABULACAO_MANUAL")
            .aplicacao(umaAplicacaoVendas())
            .canais(List.of(
                umaFuncionalidadeCanal(3004, ECanal.AGENTE_AUTORIZADO),
                umaFuncionalidadeCanal(3004, ECanal.D2D_PROPRIO)
            ))
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarTabulacaoPersonalizada() {
        return Funcionalidade.builder()
            .id(3013)
            .nome("Visualizar Tabulação Personalizada")
            .role("VDS_TABULACAO_PERSONALIZADA")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarTratamentoVenda() {
        return Funcionalidade.builder()
            .id(3008)
            .nome("Visualizar Tratamento de Venda")
            .role("VDS_VISUALIZAR_TRAT_VENDA")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarTratativas() {
        return Funcionalidade.builder()
            .id(3051)
            .nome("Visualizar Tratativas")
            .role("VDS_3051")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarVendaClaro() {
        return Funcionalidade.builder()
            .id(3002)
            .nome("Visualizar Venda Claro")
            .role("VDS_VISUALIZAR_VENDA_CLARO")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarVendaNet() {
        return Funcionalidade.builder()
            .id(3001)
            .nome("Visualizar Venda NET")
            .role("VDS_VISUALIZAR_VENDA_NET")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarViabilidadeEndereco() {
        return Funcionalidade.builder()
            .id(3003)
            .nome("Visualizar Viabilidade de Endereço")
            .role("VDS_VISUALIZAR_VIABILIDADE_ENDERECO")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeCadastrarVendaParaVendedorD2d() {
        return Funcionalidade.builder()
            .id(3030)
            .nome("Cadastrar venda para o vendedor D2D")
            .role("VDS_3030")
            .aplicacao(umaAplicacaoVendas())
            .canais(List.of(umaFuncionalidadeCanal(3030, ECanal.AGENTE_AUTORIZADO)))
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarEquipeVendaAtivoLocal() {
        return Funcionalidade.builder()
            .id(2048)
            .nome("Gerenciar Equipe Venda Ativo Local")
            .role("AUT_2048")
            .aplicacao(umaAplicacaoAutenticacao())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarSites() {
        return Funcionalidade.builder()
            .id(2047)
            .nome("Gerenciar Sites")
            .role("AUT_2047")
            .aplicacao(umaAplicacaoAutenticacao())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeInativarUsuariosSemAcesso() {
        return Funcionalidade.builder()
            .id(2057)
            .nome("Inativar Usuários Sem Acesso")
            .role("AUT_2057")
            .aplicacao(umaAplicacaoAutenticacao())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarSites() {
        return Funcionalidade.builder()
            .id(2046)
            .nome("Visualizar Sites")
            .role("AUT_2046")
            .aplicacao(umaAplicacaoAutenticacao())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeCadastrarTratativaEduconexao() {
        return Funcionalidade.builder()
            .id(16010)
            .nome("Cadastrar Tratativa Educonexao")
            .role("BKO_16010")
            .aplicacao(umaAplicacaoBko())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeDesbloquearTratativas() {
        return Funcionalidade.builder()
            .id(16007)
            .nome("Desbloquear Tratativas")
            .role("BKO_16007")
            .aplicacao(umaAplicacaoBko())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeDetalharTratativa() {
        return Funcionalidade.builder()
            .id(16004)
            .nome("Detalhar Tratativa")
            .role("BKO_16004")
            .aplicacao(umaAplicacaoBko())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarFornecedores() {
        return Funcionalidade.builder()
            .id(16005)
            .nome("Gerenciar Fornecedores")
            .role("BKO_16005")
            .aplicacao(umaAplicacaoBko())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadePriorizarTratativa() {
        return Funcionalidade.builder()
            .id(16003)
            .nome("Priorizar Tratativa")
            .role("BKO_16003")
            .aplicacao(umaAplicacaoBko())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarAcompanhamento() {
        return Funcionalidade.builder()
            .id(16002)
            .nome("Visualizar Acompanhamento")
            .role("BKO_16002")
            .aplicacao(umaAplicacaoBko())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarDashAcessoIndevido() {
        return Funcionalidade.builder()
            .id(16014)
            .nome("Visualizar Dash Acesso Indevido")
            .role("BKO_16014")
            .aplicacao(umaAplicacaoBko())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarFuncionalidadeGerenciarGrupos() {
        return Funcionalidade.builder()
            .id(16012)
            .nome("Visualizar Funcionalidade de Gerenciar Grupos")
            .role("BKO_16012")
            .aplicacao(umaAplicacaoBko())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarTodosChamados() {
        return Funcionalidade.builder()
            .id(14004)
            .nome("Visualizar todos os chamados")
            .role("CHM_VISUALIZAR_CHAMADO_GERAL")
            .aplicacao(umaAplicacaoChamado())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeCriarSolicitacoesComunicados() {
        return Funcionalidade.builder()
            .id(8006)
            .nome("Criar solicitações de comunicados")
            .role("CMD_8006")
            .aplicacao(umaAplicacaoComunicado())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeConsultarDirecionamentosPapPremium() {
        return Funcionalidade.builder()
            .id(2054)
            .nome("Consultar Direcionamentos PAP Premium")
            .role("CTR_2054")
            .aplicacao(umaAplicacaoComunicado())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarDescritivoMailing() {
        return Funcionalidade.builder()
            .id(2052)
            .nome("Gerenciar Descritivo de Mailing")
            .role("CTR_GERENCIAR_DESCRITIVO_MAILING")
            .aplicacao(umaAplicacaoControle())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarTodosSites() {
        return Funcionalidade.builder()
            .id(2044)
            .nome("Visualizar todos os Sites")
            .role("CTR_17005")
            .aplicacao(umaAplicacaoControle())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeMonitoriaSuperiorDoSupervisor() {
        return Funcionalidade.builder()
            .id(11007)
            .nome("Monitoria - Superior do Supervisor")
            .role("EVD_11007")
            .aplicacao(umaAplicacaoEquipeVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeConfigEnvioClaroQueEuIndicoFeeder() {
        return Funcionalidade.builder()
            .id(18001)
            .nome("Configurar Envio Claro Que eu Indico Feeder")
            .role("CTR_2056")
            .aplicacao(umaAplicacaoIndicacao())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarScriptsMailing() {
        return Funcionalidade.builder()
            .id(5017)
            .nome("Gerenciar Scripts do Mailing")
            .role("MLG_5017")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarD2dMailing() {
        return Funcionalidade.builder()
            .id(5006)
            .nome("Visualizar D2D Mailing")
            .role("MLG_5006")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarLayoutMailingD2d() {
        return Funcionalidade.builder()
            .id(5007)
            .nome("Visualizar Layout de Mailing D2D")
            .role("MLG_5007")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarRelatorioOperadorXMailing() {
        return Funcionalidade.builder()
            .id(20012)
            .nome("Visualizar relatório operador x mailing")
            .role("MLG_20012")
            .aplicacao(umaAplicacaoMailing())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeConsultarDadosVendedorNetSales() {
        return Funcionalidade.builder()
            .id(246)
            .nome("Consultar Dados Vendedor NetSales")
            .role("POL_246")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeDownloadEListagemContratosPortalAntigo() {
        return Funcionalidade.builder()
            .id(247)
            .nome("Download e listagem de contratos do Portal Antigo")
            .role("POL_247")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRelatorioConsultaEndereco() {
        return Funcionalidade.builder()
            .id(10012)
            .nome("Relatório Consulta de Endereço")
            .role("REL_10012")
            .aplicacao(umaAplicacaoRelatorios())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRelatorioExportarTabulacoesRealizadas() {
        return Funcionalidade.builder()
            .id(10008)
            .nome("Relatório Exportar Tabulacões Realizadas")
            .role("REL_10008")
            .aplicacao(umaAplicacaoRelatorios())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRelatorioGerenciamentoOperacionalGeral() {
        return Funcionalidade.builder()
            .id(10007)
            .nome("Relatório Gerenciamento Operacional Geral")
            .role("REL_10007")
            .aplicacao(umaAplicacaoRelatorios())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRelatorioGerenciamentoOperacionalPorParceiro() {
        return Funcionalidade.builder()
            .id(10009)
            .nome("Relatório Gerenciamento Operacional Por Parceiro")
            .role("REL_10009")
            .aplicacao(umaAplicacaoRelatorios())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeRemoverPlantao() {
        return Funcionalidade.builder()
            .id(3047)
            .nome("Remover Plantao")
            .role("VDS_3047")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarCondominio() {
        return Funcionalidade.builder()
            .id(3038)
            .nome("Visualizar condominio")
            .role("VDS_3038")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeSelecionarSupervisor() {
        return Funcionalidade.builder()
            .id(3045)
            .nome("selecionar supervisor")
            .role("VDS_3045")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarHorariosDeAcesso() {
        return Funcionalidade.builder()
            .id(20009)
            .nome("Gerenciar Horários de Acesso")
            .role("AUT_20009")
            .aplicacao(umaAplicacaoAutenticacao())
            .canais(List.of(
                umaFuncionalidadeCanal(20009, ECanal.ATIVO_PROPRIO)
            ))
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarPermissoesEspeciaisPorUsuario() {
        return Funcionalidade.builder()
            .id(2012)
            .nome("Gerenciar Permissões Especiais por Usuário")
            .role("AUT_GER_PERMISSAO_ESPECIAL_USUARIO")
            .aplicacao(umaAplicacaoAutenticacao())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeObrigatorioSelecionarCidadesDeAtuacao() {
        return Funcionalidade.builder()
            .id(2032)
            .nome("Obrigatório selecionar Cidades de atuação")
            .role("AUT_2032")
            .aplicacao(umaAplicacaoAutenticacao())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeAbrirChamadoCrn() {
        return Funcionalidade.builder()
            .id(13000)
            .nome("Abrir chamado CRN")
            .role("CRN_ABRIR_CHAMADO")
            .aplicacao(umaAplicacaoContatoCrn())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarChamadoCrn() {
        return Funcionalidade.builder()
            .id(13002)
            .nome("Visualizar chamado CRN")
            .role("CRN_VISUALIZAR_CHAMADO")
            .aplicacao(umaAplicacaoContatoCrn())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeMonitoriaVendedor() {
        return Funcionalidade.builder()
            .id(11005)
            .nome("Monitoria - Vendedor")
            .role("EVD_11005")
            .aplicacao(umaAplicacaoEquipeVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarChecklists() {
        return Funcionalidade.builder()
            .id(11001)
            .nome("Visualizar Checklists")
            .role("EVD_11001")
            .aplicacao(umaAplicacaoEquipeVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeConsultarIndicacoesPremium() {
        return Funcionalidade.builder()
            .id(3062)
            .nome("Consultar Indicações Premium")
            .role("VDS_3062")
            .aplicacao(umaAplicacaoVendas())
            .canais(List.of(
                umaFuncionalidadeCanal(3062, ECanal.D2D_PROPRIO)
            ))
            .especial(false)
            .build();
    }

    public static Funcionalidade umaFuncionalidadeBko(Integer id, String nome) {
        return Funcionalidade.builder()
            .id(id)
            .nome(nome)
            .role(nome.concat(" - ").concat(id.toString()))
            .aplicacao(umaAplicacaoBko())
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarAgendaVisita() {
        return Funcionalidade.builder()
            .id(3043)
            .nome("Gerenciar Agenda Visita")
            .role("VDS_3043")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarCondominio() {
        return Funcionalidade.builder()
            .id(3037)
            .nome("Gerenciar condominio")
            .role("VDS_3037")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarTodosDadosDosAas() {
        return Funcionalidade.builder()
            .id(244)
            .nome("Visualizar todos os dados dos Agentes Autorizados")
            .role("POL_244")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarEquipeVenda() {
        return Funcionalidade.builder()
            .id(5)
            .nome("Gerenciar Equipe Venda")
            .role("POL_GERENCIAR_EQUIPE_VENDA")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarEquipeTecnica() {
        return Funcionalidade.builder()
            .id(6)
            .nome("Gerenciar Equipe Técnica")
            .role("POL_GERENCIAR_EQUIPE_TECNICA")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarNoticias() {
        return Funcionalidade.builder()
            .id(11)
            .nome("Visualizar Notícias")
            .role("POL_VISUALIZAR_NOTICIAS")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeAaAprovacaoOperacao() {
        return Funcionalidade.builder()
            .id(12)
            .nome("Agente Autorizado Aprovação Operação")
            .role("POL_AGENTE_AUTORIZADO_APROVACAO_OPERACAO")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeAaAprovacaoMso() {
        return Funcionalidade.builder()
            .id(13)
            .nome("Agente Autorizado Aprovação MSO")
            .role("POL_AGENTE_AUTORIZADO_APROVACAO_MSO")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeGerenciarAas() {
        return Funcionalidade.builder()
            .id(16)
            .nome("Gerenciar Agentes Autorizados")
            .role("POL_GERENCIAR_AA")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeDescredenciamentoDeAa() {
        return Funcionalidade.builder()
            .id(17)
            .nome("Descredenciamento de Agente Autorizado")
            .role("POL_DESCREDENCIAR_AA")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarComissionamento() {
        return Funcionalidade.builder()
            .id(18)
            .nome("Visualizar Comissionamento")
            .role("POL_VISUALIZAR_COMISSAO")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeCaptacaoAaExtracao() {
        return Funcionalidade.builder()
            .id(28)
            .nome("Captação de AA Extração")
            .role("POL_CAPTACAO_AA_EXTRACAO")
            .aplicacao(umaAplicacaoPol())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeVisualizarPreVendaLojaFuturo() {
        return Funcionalidade.builder()
            .id(3066)
            .nome("Visualizar Pré Venda Loja Futuro")
            .role("VDS_3066")
            .aplicacao(umaAplicacaoVendas())
            .especial(false)
            .build();
    }

    public static Funcionalidade funcionalidadeAdministradorDoSuporte() {
        return Funcionalidade.builder()
            .id(14001)
            .nome("Administrador do suporte")
            .role("CHM_ADM_CHAMADOS")
            .aplicacao(umaAplicacaoChamado())
            .especial(false)
            .build();
    }

    private static Aplicacao umaAplicacaoAutenticacao() {
        return new Aplicacao(1, "AUTENTICAÇÃO", AUT);
    }

    private static Aplicacao umaAplicacaoChamado() {
        return new Aplicacao(15, "CHAMADO", CHM);
    }

    private static Aplicacao umaAplicacaoControle() {
        return new Aplicacao(20, "CONTROLE", CTR);
    }

    private static Aplicacao umaAplicacaoMailing() {
        return new Aplicacao(4, "MAILING", MLG);
    }

    private static Aplicacao umaAplicacaoPol() {
        return new Aplicacao(2, "PARCEIROS ONLINE", POL);
    }

    private static Aplicacao umaAplicacaoVendas() {
        return new Aplicacao(3, "VENDAS", VDS);
    }

    private static Aplicacao umaAplicacaoBko() {
        return new Aplicacao(24, "BACKOFFICE", BKO);
    }

    private static Aplicacao umaAplicacaoComunicado() {
        return new Aplicacao(21, "COMUNICADO", CMD);
    }

    private static Aplicacao umaAplicacaoEquipeVendas() {
        return new Aplicacao(13, "EQUIPE VENDAS", EVD);
    }

    private static Aplicacao umaAplicacaoIndicacao() {
        return new Aplicacao(22, "INDICAÇÃO", IND);
    }

    private static Aplicacao umaAplicacaoRelatorios() {
        return new Aplicacao(17, "RELATÓRIOS", REL);
    }

    private static Aplicacao umaAplicacaoContatoCrn() {
        return new Aplicacao(14, "CONTATO CRN", CRN);
    }
}
