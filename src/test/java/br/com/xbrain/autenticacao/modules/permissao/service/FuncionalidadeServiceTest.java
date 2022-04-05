package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.*;
import static helpers.Usuarios.SOCIO_AA;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class FuncionalidadeServiceTest {

    private static Integer USUARIO_SOCIO_ID = 226;
    private static Integer CARGO_SOCIO_ID = 41;
    private static Integer DEPARTAMENTO_SOCIO_ID = 40;

    @Autowired
    private FuncionalidadeService service;
    @MockBean
    private AutenticacaoService autenticacaoService;

    private static Aplicacao umaAplicacao() {
        var aplicacao = new Aplicacao();
        aplicacao.setId(15);
        aplicacao.setNome("CHAMADO");
        aplicacao.setCodigo(CodigoAplicacao.CHM);
        return aplicacao;
    }

    @Test
    public void getPermissoes_permissosDoUsuario_somentePermitidasAoUsuario() {
        List<SimpleGrantedAuthority> permissoes = service.getPermissoes(umUsuarioSocio());

        assertThat(permissoes)
            .isNotEmpty()
            .extracting("authority")
            .contains("ROLE_AUT_2031");
    }

    @Test
    public void getFuncionalidadesPermitidasAoUsuarioComCanal_deveAgruparAsFuncionalidadesDoCargoEspeciaisComDistinct() {
        List<Funcionalidade> funcionalidades =
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

        assertThat(service.getFuncionalidadesPermitidasAoUsuario(usuario))
            .hasSize(51);
    }

    @Test
    public void getFuncionalidadesPermitidasAoUsuario_listaDeFuncionalidades_quandoUsuarioCargoVendedorOperacao() {
        var usuario = Usuario.builder()
            .id(100)
            .cargo(umCargoVendedorOperacao())
            .departamento(new Departamento(3))
            .nome("RENATO")
            .build();

        assertThat(service.getFuncionalidadesPermitidasAoUsuario(usuario))
            .hasSize(12);
    }

    @Test
    public void getAll_naoDeveRetornarFuncionalidadeAdmChamados_quandoUsuarioNaoTiverPermissaoAdmChamados() {
        var eviromentMock = Mockito.mock(Environment.class);

        var funcionalidade = FuncionalidadeResponse.convertFrom(Funcionalidade.builder()
            .id(14001)
            .nome("Administrador do suporte")
            .role("CHM_ADM_CHAMADOS")
            .aplicacao(umaAplicacao())

            .build());
        var request = mock(HttpServletRequest.class);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNaoAdmSuporte());
        when(eviromentMock.getActiveProfiles()).thenReturn(new String[]{"producao"});

        assertThat(service.getAll(request)).doesNotContain(funcionalidade);
    }

    @Test
    public void getAll_deveRetornarFuncionalidadeAdmChamados_quandoUsuarioTiverPermissaoAdmChamados() {
        var eviromentMock = Mockito.mock(Environment.class);

        var funcionalidade = FuncionalidadeResponse.convertFrom(Funcionalidade.builder()
            .id(14001)
            .nome("Administrador do suporte")
            .role("CHM_ADM_CHAMADOS")
            .aplicacao(umaAplicacao())
            .build());

        var request = mock(HttpServletRequest.class);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAdmSuporte());
        when(eviromentMock.getActiveProfiles()).thenReturn(new String[]{"producao"});

        assertThat(service.getAll(request)).contains(funcionalidade);
    }

    @Test
    public void getAll_deveRetornarFuncionalidadeAdmChamados_quandoProfileAtivoNaoForProducao() {

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
            .id(USUARIO_SOCIO_ID)
            .email(SOCIO_AA)
            .cargo(Cargo.builder()
                .id(CARGO_SOCIO_ID)
                .codigo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO)
                .nivel(Nivel.builder()
                    .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                    .build())
                .build())
            .departamento(Departamento.builder().id(DEPARTAMENTO_SOCIO_ID).build())
            .build();
    }

    private Cargo umCargoMsoConsultor() {
        return Cargo.builder()
            .id(22)
            .codigo(CodigoCargo.MSO_CONSULTOR)
            .nivel(umNivelMso())
            .build();
    }

    private Nivel umNivelMso() {
        return Nivel.builder()
            .id(2)
            .codigo(CodigoNivel.MSO)
            .build();
    }

    private Cargo umCargoVendedorOperacao() {
        return Cargo.builder()
            .id(8)
            .codigo(CodigoCargo.VENDEDOR_OPERACAO)
            .nivel(umNivelOperacao())
            .build();
    }

    private Nivel umNivelOperacao() {
        return Nivel.builder()
            .id(1)
            .codigo(CodigoNivel.OPERACAO)
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
