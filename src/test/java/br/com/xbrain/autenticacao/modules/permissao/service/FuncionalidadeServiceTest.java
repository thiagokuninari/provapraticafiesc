package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.permissao.filtros.FuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.helper.CargoDepartamentoFuncionalidadeHelper.*;
import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.umDepartamentoAa;
import static helpers.Usuarios.SOCIO_AA;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class FuncionalidadeServiceTest {

    @InjectMocks
    private FuncionalidadeService service;
    @Mock
    private CargoDepartamentoFuncionalidadeRepository cargoDepartamentoFuncionalidadeRepository;
    @Mock
    private PermissaoEspecialRepository permissaoEspecialRepository;

    @Test
    public void getPermissoes_permissosDoUsuario_somentePermitidasAoUsuario() {
        when(cargoDepartamentoFuncionalidadeRepository.findFuncionalidadesPorCargoEDepartamento(getPredicate(umUsuarioSocio())))
            .thenReturn(umaListaDeCargoDepartamentoFuncionalidadeDeSocio());
        when(permissaoEspecialRepository.findPorUsuario(umUsuarioSocio().getId()))
            .thenReturn(List.of(
                funcionalidadeCadastrarVendaParaVendedorD2d(),
                funcionalidadeRelatorioGerenciamentoOperacional(),
                funcionalidadeRelatorioResumoMailing(),
                funcionalidadeRelatorioTicketMedioAnalitico()
            ));

        var permissoes = service.getPermissoes(umUsuarioSocio());

        assertThat(permissoes)
            .isNotEmpty()
            .extracting("authority")
            .contains("ROLE_AUT_2031");
    }

    @Test
    public void getFuncionalidadesPermitidasAoUsuarioComCanal_deveAgruparAsFuncionalidadesDoCargoEspeciaisComDistinct() {
        when(cargoDepartamentoFuncionalidadeRepository.findFuncionalidadesDoCargoDepartamentoComCanal(
            umUsuarioSocio().getCargoId(),
            umUsuarioSocio().getDepartamentoId()))
            .thenReturn(List.of(
                funcionalidadeGerenciarPausasAgendadas(),
                funcionalidadeVisualizarTabulacaoManual(),
                funcionalidadeVisualizarAgendamento(),
                funcionalidadeRelatorioResumoMailing(),
                funcionalidadeRelatorioTicketMedioAnalitico(),
                funcionalidadeRelatorioTicketMedioPorVendedor(),
                funcionalidadeRelatorioGerenciamentoOperacional(),
                funcionalidadeVisualizarRelatorioConsultaEndereco()
            ));

        when(cargoDepartamentoFuncionalidadeRepository.findPermissoesEspeciaisDoUsuarioComCanal(umUsuarioSocio().getId()))
            .thenReturn(List.of(
                funcionalidadeRelatorioResumoMailing(),
                funcionalidadeRelatorioTicketMedioAnalitico(),
                funcionalidadeRelatorioGerenciamentoOperacional(),
                funcionalidadeCadastrarVendaParaVendedorD2d()
            ));

        var funcionalidades =
            service.getFuncionalidadesPermitidasAoUsuarioComCanal(umUsuarioSocio());

        assertThat(funcionalidades)
            .extracting("nome")
            .containsExactly(
                "Gerenciar Pausas Agendadas",
                "Visualizar Tabulação Manual",
                "Visualizar Agendamento",
                "Relatório - Resumo de Mailing",
                "Relatório - Ticket Médio Analítico",
                "Relatório - Ticket Médio por Vendedor",
                "Relatório - Gerenciamento Operacional",
                "Visualizar Relatório Consulta de Endereço",
                "Cadastrar venda para o vendedor D2D");
    }

    @Test
    public void getFuncionalidadesPermitidasAoUsuario_listaDeFuncionalidades_quandoUsuarioCargoMsoConsultor() {
        var usuario = Usuario.builder()
            .id(100)
            .cargo(umCargoMsoConsultor())
            .departamento(new Departamento(21))
            .nome("RENATO")
            .build();

        when(cargoDepartamentoFuncionalidadeRepository.findFuncionalidadesPorCargoEDepartamento(getPredicate(usuario)))
            .thenReturn(umaListaDeCargoDepartamentoFuncionalidadeDeConsultor());

        when(permissaoEspecialRepository.findPorUsuario(usuario.getId()))
            .thenReturn(List.of(
                funcionalidadeGerenciarHorariosDeAcesso(),
                funcionalidadeGerenciarPermissoesEspeciaisPorUsuario()
            ));

        assertThat(service.getFuncionalidadesPermitidasAoUsuario(usuario))
                .hasSize(43);
    }

    @Test
    public void getFuncionalidadesPermitidasAoUsuario_listaDeFuncionalidades_quandoUsuarioCargoVendedorOperacao() {
        var usuario = Usuario.builder()
            .id(100)
            .cargo(umCargoVendedorOperacao())
            .departamento(new Departamento(3))
            .nome("RENATO")
            .build();

        when(cargoDepartamentoFuncionalidadeRepository.findFuncionalidadesPorCargoEDepartamento(getPredicate(usuario)))
            .thenReturn(umaListaDeCargoDepartamentoFuncionalidadeDeVendedor());

        when(permissaoEspecialRepository.findPorUsuario(usuario.getId()))
            .thenReturn(List.of(
                funcionalidadeGerenciarHorariosDeAcesso(),
                funcionalidadeGerenciarPermissoesEspeciaisPorUsuario()
            ));

        assertThat(service.getFuncionalidadesPermitidasAoUsuario(usuario))
            .hasSize(12);
    }

    private Usuario umUsuarioSocio() {
        return Usuario
            .builder()
            .id(226)
            .email(SOCIO_AA)
            .cargo(umCargoAaSocio())
            .departamento(umDepartamentoAa())
            .build();
    }

    private BooleanBuilder getPredicate(Usuario usuario) {
        return new FuncionalidadePredicate()
            .comCargo(usuario.getCargoId())
            .comDepartamento(usuario.getDepartamentoId())
            .build();
    }
}
