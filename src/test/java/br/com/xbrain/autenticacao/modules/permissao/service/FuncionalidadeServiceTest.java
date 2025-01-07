package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao;
import br.com.xbrain.autenticacao.modules.permissao.filtros.FuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.FuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.permissao.helper.CargoDepartamentoFuncionalidadeHelper.*;
import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.umDepartamentoAa;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.umDepartamentoComercial;
import static helpers.Usuarios.SOCIO_AA;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FuncionalidadeServiceTest {

    @InjectMocks
    private FuncionalidadeService service;
    @Mock
    private CargoDepartamentoFuncionalidadeRepository cargoDepartamentoFuncionalidadeRepository;
    @Mock
    private PermissaoEspecialRepository permissaoEspecialRepository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private FuncionalidadeRepository funcionalidadeRepository;
    @Mock
    private Environment environment;

    private static Aplicacao umaAplicacao() {
        var aplicacao = new Aplicacao();
        aplicacao.setId(15);
        aplicacao.setNome("CHAMADO");
        aplicacao.setCodigo(CodigoAplicacao.CHM);
        return aplicacao;
    }

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
                funcionalidadeVisualizarRelatorioConsultaEndereco(),
                funcionalidadeVisualizarPreVendaLojaFuturo()
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
                "Visualizar Pré Venda Loja Futuro",
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
    public void getFuncionalidadesPermitidasAoUsuario_listaDeFuncionalidades_seCanalNaoNullECanalConterNoUsuario() {
        var cargoDepartamentoFuncionalidade = new CargoDepartamentoFuncionalidade();
        cargoDepartamentoFuncionalidade.setId(1);
        cargoDepartamentoFuncionalidade.setCargo(umCargoMsoConsultor());
        cargoDepartamentoFuncionalidade.setDepartamento(umDepartamentoComercial());
        cargoDepartamentoFuncionalidade.setCanal(ECanal.D2D_PROPRIO);
        var usuario = Usuario.builder()
            .id(100)
            .cargo(umCargoExecutivo())
            .departamento(new Departamento(21))
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .build();
        when(cargoDepartamentoFuncionalidadeRepository.findFuncionalidadesPorCargoEDepartamento(getPredicate(usuario)))
            .thenReturn(List.of(cargoDepartamentoFuncionalidade));
        when(permissaoEspecialRepository.findPorUsuario(usuario.getId()))
            .thenReturn(List.of(
                funcionalidadeGerenciarHorariosDeAcesso(),
                funcionalidadeGerenciarPermissoesEspeciaisPorUsuario()
            ));

        assertThat(service.getFuncionalidadesPermitidasAoUsuario(usuario))
            .hasSize(3);
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

    @Test
    public void getAll_naoDeveRetornarFuncionalidadeAdmChamados_quandoUsuarioNaoTiverPermissaoAdmChamados() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"producao"});

        var funcionalidade = FuncionalidadeResponse.convertFrom(Funcionalidade.builder()
            .id(14001)
            .nome("Administrador do suporte")
            .role("CHM_ADM_CHAMADOS")
            .aplicacao(umaAplicacao())

            .build());
        var request = mock(HttpServletRequest.class);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNaoAdmSuporte());

        assertThat(service.getAll(request)).doesNotContain(funcionalidade);
    }

    @Test
    public void getAll_deveRetornarFuncionalidadeAdmChamados_quandoUsuarioTiverPermissaoAdmChamados() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"producao"});
        when(funcionalidadeRepository.findAllByOrderByNome()).thenReturn(List.of(funcionalidadeAdministradorDoSuporte()));

        var funcionalidade = FuncionalidadeResponse.convertFrom(Funcionalidade.builder()
            .id(14001)
            .nome("Administrador do suporte")
            .role("CHM_ADM_CHAMADOS")
            .aplicacao(umaAplicacao())
            .build());

        var request = mock(HttpServletRequest.class);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAdmSuporte());

        assertThat(service.getAll(request)).contains(funcionalidade);
    }

    @Test
    public void getAll_deveRetornarFuncionalidadeAdmChamados_quandoProfileAtivoNaoForProducao() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});
        when(funcionalidadeRepository.findAllByOrderByNome()).thenReturn(List.of(funcionalidadeAdministradorDoSuporte()));

        var funcionalidade = FuncionalidadeResponse.convertFrom(Funcionalidade.builder()
            .id(14001)
            .nome("Administrador do suporte")
            .role("CHM_ADM_CHAMADOS")
            .aplicacao(umaAplicacao())
            .build());

        var request = mock(HttpServletRequest.class);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAdmSuporte());

        assertThat(service.getAll(request)).contains(funcionalidade);
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

    private UsuarioAutenticado umUsuarioAutenticadoNaoAdmSuporte() {
        return UsuarioAutenticado
            .builder()
            .id(1)
            .nome("USUARIO")
            .email("USUARIO@TESTE.COM")
            .cargoCodigo(CodigoCargo.ADMINISTRADOR)
            .nivelCodigo(CodigoNivel.XBRAIN.name())
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole()),
                new SimpleGrantedAuthority(AUT_VISUALIZAR_USUARIO.getRole()),
                new SimpleGrantedAuthority(AUT_EMULAR_USUARIO.getRole())))
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticadoAdmSuporte() {
        return UsuarioAutenticado
            .builder()
            .id(1)
            .nome("USUARIO")
            .email("USUARIO@TESTE.COM")
            .cargoCodigo(CodigoCargo.ADMINISTRADOR)
            .nivelCodigo(CodigoNivel.XBRAIN.name())
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole()),
                new SimpleGrantedAuthority(AUT_VISUALIZAR_USUARIO.getRole()),
                new SimpleGrantedAuthority(AUT_EMULAR_USUARIO.getRole()),
                new SimpleGrantedAuthority(CHM_ADM_CHAMADOS.getRole())))
            .build();
    }
}
