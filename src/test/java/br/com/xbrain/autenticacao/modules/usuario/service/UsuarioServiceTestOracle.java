package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.assertj.core.groups.Tuple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento.COMERCIAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.XBRAIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("oracle-test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database_oracle.sql", "classpath:/tests_hierarquia.sql"})
public class UsuarioServiceTestOracle {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private UsuarioService service;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @MockBean
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;

    @Before
    public void setUp() {
        when(autenticacaoService.getUsuarioId()).thenReturn(101);
    }

    @Test
    public void getVendedoresOperacaoDaHierarquia_idsDosVendedores_quandoForGerenteOperacaoPelaHierarquia() {
        Assert.assertEquals(5, service.getVendedoresOperacaoDaHierarquia(227).size());
    }

    @Test
    public void getSupervisoresOperacaoDaHierarquia_idsDosVendedores_quandoForGerenteOperacaoPelaHierarquia() {
        Assert.assertEquals(3, service.getSupervisoresOperacaoDaHierarquia(227).size());
    }

    @Test
    public void getVendedoresOperacaoDaHierarquia_idsDosVendedores_quandoForOperacaoPelaHierarquia() {
        Assert.assertEquals(3, service.getVendedoresOperacaoDaHierarquia(228).size());
        Assert.assertEquals(2, service.getVendedoresOperacaoDaHierarquia(234).size());
    }

    @Test
    public void getVendedoresOperacaoDaHierarquia_idsDosVendedores_quandoForVendedorOperacaoPelaHierarquia() {
        Assert.assertEquals(0, service.getVendedoresOperacaoDaHierarquia(230).size());
    }

    @Test
    public void getIdsSubordinadosDaHierarquia_idsDosVendedores_quandoForGerente() {
        Assert.assertEquals(3, service.getIdsSubordinadosDaHierarquia(227,
                Set.of(SUPERVISOR_OPERACAO.name())).size());
    }

    @Test
    public void getIdsSubordinadosDasHierarquias_idsDosVendedoresParaMultiplosCargos_quandoForGerente() {
        assertThat(service.getIdsSubordinadosDaHierarquia(227,
            Set.of(COORDENADOR_OPERACAO.name(),
                   SUPERVISOR_OPERACAO.name())))
            .isNotEmpty()
            .hasSize(6);
    }

    @Test
    public void getIdsSubordinadosDasHierarquias_idsDosVendedoresParaUnicoCargo_quandoForGerente() {
        assertThat(service.getIdsSubordinadosDaHierarquia(227,
            Set.of(COORDENADOR_OPERACAO.name())))
            .isNotEmpty()
            .hasSize(3);
    }

    @Test
    public void getIdsSubordinadosDaHierarquia_idsDosVendedores_quandoForCoordenador() {
        assertThat(service.getIdsSubordinadosDaHierarquia(228,
            Set.of(SUPERVISOR_OPERACAO.name())))
            .isNotEmpty()
            .hasSize(2);
        assertThat(service.getIdsSubordinadosDaHierarquia(234,
            Set.of(SUPERVISOR_OPERACAO.name())))
            .isNotEmpty()
            .hasSize(1);
    }

    @Test
    public void getUsuariosSuperiores_deveRetonarUsuariosSuperiores_comSituacaoAtivoComCargoGerenteOperacao() {
        assertThat(service.getUsuariosSuperiores(getFiltroHierarquia()))
                .hasSize(1)
                .extracting("id", "nome", "codigoCargo", "codigoDepartamento", "codigoNivel", "situacao")
                .containsExactly(
                        tuple(104, "operacao_gerente_comercial", GERENTE_OPERACAO, COMERCIAL, OPERACAO, A));
    }

    @Test
    public void findAllAutoComplete_deveRetornarExecutivosOperacao_quandoDepartamentoForComercial() {
        assertThat(service.findAllExecutivosOperacaoDepartamentoComercial(null))
            .extracting("value", "text")
            .containsExactly(
                tuple(116, "ALBERTO PEREIRA"),
                tuple(119, "JOANA OLIVEIRA"),
                tuple(117, "ROBERTO ALMEIDA"),
                tuple(149, "USUARIO INFERIOR"));
    }

    @Test
    public void getUsuariosAlvoDoComunicado_deveFiltrarSeNaoReceberCidadesDoPol_seRetornarUsuario() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoCompleto());
        usuarioRepository.findAll()
            .forEach(user -> service.atualizarDataUltimoAcesso(user.getId()));
        when(agenteAutorizadoService.getIdsUsuariosPermitidosDoUsuario(any()))
            .thenReturn(List.of(111, 104, 115));

        var usuarios = service.getUsuariosAlvoDoComunicado(PublicoAlvoComunicadoFiltros.builder()
            .subClusterId(189)
            .grupoId(20)
            .regionalId(3)
            .clusterId(45)
            .cidadesIds(List.of(5578))
            .build());

        assertThat(usuarios).extracting("id", "nome")
            .containsExactlyInAnyOrder(
                Tuple.tuple(104, "operacao_gerente_comercial"),
                Tuple.tuple(100, "ADMIN"),
                Tuple.tuple(233, "VENDEDOR OPERACAO 3"),
                Tuple.tuple(234, "COORDENADOR OPERACAO 2"),
                Tuple.tuple(235, "SUPERVISOR OPERACAO 3"),
                Tuple.tuple(236, "VENDEDOR OPERACAO 2"),
                Tuple.tuple(237, "VENDEDOR OPERACAO 3"),
                Tuple.tuple(238, "COORDENADOR OPERACAO 3"),
                Tuple.tuple(239, "VENDEDOR OPERACAO 2"),
                Tuple.tuple(240, "VENDEDOR OPERACAO 3"),
                Tuple.tuple(369, "MARIA AUGUSTA"),
                Tuple.tuple(370, "HELIO OLIVEIRA"));
    }

    @Test
    public void getUsuariosAlvoDoComunicado_deveFiltrarSeReceberCidadesDoPol_seRetornarUsuario() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoCompleto());
        usuarioRepository.findAll()
            .forEach(user -> service.atualizarDataUltimoAcesso(user.getId()));
        when(agenteAutorizadoService.getIdsUsuariosPermitidosDoUsuario(any()))
            .thenReturn(List.of(111, 104, 115));

        var usuarios = service.getUsuariosAlvoDoComunicado(PublicoAlvoComunicadoFiltros.builder()
            .subClusterId(189)
            .grupoId(20)
            .regionalId(3)
            .clusterId(45)
            .cidadesIds(List.of(5578))
            .build());

        assertThat(usuarios).extracting("id", "nome")
            .containsExactlyInAnyOrder(
                Tuple.tuple(104, "operacao_gerente_comercial"),
                Tuple.tuple(100, "ADMIN"),
                Tuple.tuple(233, "VENDEDOR OPERACAO 3"),
                Tuple.tuple(234, "COORDENADOR OPERACAO 2"),
                Tuple.tuple(235, "SUPERVISOR OPERACAO 3"),
                Tuple.tuple(236, "VENDEDOR OPERACAO 2"),
                Tuple.tuple(237, "VENDEDOR OPERACAO 3"),
                Tuple.tuple(238, "COORDENADOR OPERACAO 3"),
                Tuple.tuple(239, "VENDEDOR OPERACAO 2"),
                Tuple.tuple(240, "VENDEDOR OPERACAO 3"),
                Tuple.tuple(369, "MARIA AUGUSTA"),
                Tuple.tuple(370, "HELIO OLIVEIRA"));
    }

    @Test
    public void findExecutivosPorIds_deveRetornarExecutivos_quandoEstiveremVinculadosAosIdsDaListagem() {
        var usuarioLogado = umUsuarioAutenticado();
        usuarioLogado.setCargoCodigo(EXECUTIVO);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioLogado);
        assertThat(service.findExecutivosPorIds(List.of(119)))
            .extracting("value", "text")
            .containsExactly(
                tuple(119, "JOANA OLIVEIRA"));
    }

    @Test
    public void findExecutivosPorIds_deveListaVazia_quandoIdsNaoForemDeUsuariosExecutivosComerciais() {
        var usuarioLogado = umUsuarioAutenticado();
        usuarioLogado.setCargoCodigo(COORDENADOR_OPERACAO);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioLogado);
        assertThat(service.findExecutivosPorIds(List.of(110)))
            .extracting("value", "text")
            .isEmpty();
    }

    @Test
    public void findAllLideresComerciaisDoExecutivo_deveRetornarLideresComerciaisDoExecutivo_quandoLiderEstiverAtivo() {
        assertThat(service.findAllLideresComerciaisDoExecutivo(101))
            .hasSize(2)
            .extracting("value", "text")
            .containsExactly(
                tuple(104, "operacao_gerente_comercial"),
                tuple(369, "MARIA AUGUSTA"));
    }

    @Test
    public void getAllForCsv_ListaComUsuariosParaExportacaoCsv_ComFiltroPorNomeUsuario() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado
                .builder()
                .nivelCodigo(XBRAIN.name())
                .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
                .build());

        assertThat(service.getAllForCsv(getFiltroUsuario("USUARIO TESTE")))
            .hasSize(1)
            .extracting(
                "nome",
                "email",
                "empresas",
                "unidadesNegocios",
                "cargo",
                "departamento")
            .containsExactly(tuple(
                "USUARIO TESTE",
                "USUARIO_TESTE@GMAIL.COM",
                "Xbrain.NET",
                "Pessoal.Xbrain",
                "Vendedor",
                "Administrador"));
    }

    @Test
    public void getPermissoesPorUsuarios_permissoesComUsuario_conformeParametroUsuarioIdAndPermissao() {
        UsuarioPermissoesRequest request = new UsuarioPermissoesRequest();
        request.setPermissoes(Arrays.asList(
            "ROLE_VDS_TABULACAO_DISCADORA",
            "ROLE_VDS_TABULACAO_CLICKTOCALL",
            "ROLE_VDS_TABULACAO_PERSONALIZADA",
            "ROLE_VDS_TABULACAO_MANUAL"));
        request.setUsuariosId(Arrays.asList(231, 238, 245, 243));

        List<UsuarioPermissoesResponse> response = service.findUsuariosByPermissoes(request);
        Assert.assertEquals(4, response.size());
        assertThat(response)
            .containsExactlyElementsOf(
                Arrays.asList(
                    new UsuarioPermissoesResponse(231, Collections.emptyList()),
                    new UsuarioPermissoesResponse(238, Collections.singletonList(
                        "ROLE_VDS_TABULACAO_DISCADORA")),
                    new UsuarioPermissoesResponse(243, Arrays.asList(
                        "ROLE_VDS_TABULACAO_CLICKTOCALL",
                        "ROLE_VDS_TABULACAO_DISCADORA",
                        "ROLE_VDS_TABULACAO_PERSONALIZADA")),
                    new UsuarioPermissoesResponse(245, Arrays.asList(
                        "ROLE_VDS_TABULACAO_MANUAL",
                        "ROLE_VDS_TABULACAO_PERSONALIZADA"))
                ));
    }

    @Test
    public void getPermissoesPorUsuarios_permissoesComUsuario_naoDeveLancarErroAoReceberMuitosIds() {
        UsuarioPermissoesRequest request = new UsuarioPermissoesRequest();
        request.setPermissoes(Arrays.asList(
            "ROLE_VDS_TABULACAO_DISCADORA",
            "ROLE_VDS_TABULACAO_CLICKTOCALL",
            "ROLE_VDS_TABULACAO_PERSONALIZADA",
            "ROLE_VDS_TABULACAO_MANUAL"));
        request.setUsuariosId(IntStream.rangeClosed(1, 3000)
            .boxed().collect(Collectors.toList()));

        List<UsuarioPermissoesResponse> response = service.findUsuariosByPermissoes(request);
        Assert.assertEquals(47, response.size());
    }

    @SuppressWarnings("LineLength")
    @Test
    public void getSubordinadosDoUsuarioPorCargo_deveRetornarUsuariosSubordinados_quandoUsuarioPossuirSubordinadosComCargoExecutivoOuHunter() {
        assertThat(service.getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(115))
            .hasSize(3)
            .extracting("value", "text")
            .contains(
                tuple(116, "ALBERTO PEREIRA"),
                tuple(117, "ROBERTO ALMEIDA"),
                        tuple(119, "JOANA OLIVEIRA"));
    }

    @SuppressWarnings("LineLength")
    @Test
    public void getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter_deveRetornarVazio_quandoUsuarioNaoPossuirSubordinadosComCargoExecutivoOuHunter() {
        assertThat(service.getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(500))
                .isEmpty();
    }

    @Test
    public void getSubordinadosDoUsuario_deveRetornarTodosSubordinadosDoUsuario_quandoUsuarioPossuirSubordinados() {
        assertThat(service.getSubordinadosDoUsuario(115))
                .hasSize(5)
                .extracting("id", "nome", "cpf", "email", "codigoNivel", "codigoDepartamento", "codigoCargo", "nomeCargo")
                .contains(
                        tuple(116, "ALBERTO PEREIRA", "88855511147", "ALBERTO@NET.COM",
                                OPERACAO, COMERCIAL, EXECUTIVO, "Executivo"),
                        tuple(118, "HENRIQUE ALVES", "88855511177", "HENRIQUE@NET.COM",
                            CodigoNivel.AGENTE_AUTORIZADO, CodigoDepartamento.AGENTE_AUTORIZADO, AGENTE_AUTORIZADO_SOCIO,
                            "Sócio Principal"),
                    tuple(120, "MARIA AUGUSTA", "88855511133", "MARIA@NET.COM",
                        OPERACAO, COMERCIAL, EXECUTIVO, "Executivo"),
                    tuple(117, "ROBERTO ALMEIDA", "88855511199", "ROBERTO@NET.COM",
                        OPERACAO, COMERCIAL, EXECUTIVO, "Executivo"),
                    tuple(119, "JOANA OLIVEIRA", "88855511166", "JOANA@NET.COM",
                        OPERACAO, COMERCIAL, EXECUTIVO_HUNTER, "Executivo Hunter"));
    }

    @Test
    public void getAll_deveRetornarTodos_quandoInformarListaComMaisDe1000Ids() {
        var lista1000Ids = IntStream.rangeClosed(0, 2000)
            .boxed().collect(Collectors.toList());

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(agenteAutorizadoNovoService.getIdUsuariosPorAa(anyString(), anyBoolean())).thenReturn(lista1000Ids);

        var filtros = new UsuarioFiltros();
        filtros.setCnpjAa("15.765.222/0001-72");

        assertThat(service.getAll(new PageRequest(), filtros)).isNotNull();
    }

    @Test
    public void getAll_deveRetornarVazia_quandoInformarListaSemRegistro() {

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(agenteAutorizadoNovoService.getIdUsuariosPorAa(anyString(), anyBoolean())).thenReturn(List.of());

        var filtros = new UsuarioFiltros();
        filtros.setCnpjAa("15.765.222/0001-72");

        assertThat(service.getAll(new PageRequest(), filtros)).isEmpty();
    }

    @Test
    public void getAll_deveRetornarTodos_quandoPossuirSubCanalPap() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoCompleto());

        var filtros = UsuarioFiltros.builder().subCanalId(1).build();

        assertThat(service.getAll(new PageRequest(), filtros)).isNotEmpty();
    }

    private UsuarioFiltrosHierarquia getFiltroHierarquia() {
        return UsuarioFiltrosHierarquia.builder()
            .usuarioId(Collections.singletonList(101))
            .codigoNivel(OPERACAO)
            .codigoDepartamento(COMERCIAL)
            .codigoCargo(GERENTE_OPERACAO)
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return new UsuarioAutenticado(umUsuarioComHierarquia());
    }

    private UsuarioAutenticado umUsuarioAutenticadoCompleto() {
        return UsuarioAutenticado
            .builder()
            .id(1)
            .nome("USUARIO")
            .email("USUARIO@TESTE.COM")
            .usuario(Usuario.builder()
                .canais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                .build())
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_XBRAIN)
            .nivelCodigo(XBRAIN.name())
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole()),
                new SimpleGrantedAuthority(AUT_VISUALIZAR_USUARIO.getRole()),
                new SimpleGrantedAuthority(AUT_VISUALIZAR_USUARIOS_AA.getRole())))
            .build();
    }

    private UsuarioFiltros getFiltroUsuario(String nome) {
        UsuarioFiltros usuarioFiltros = new UsuarioFiltros();
        usuarioFiltros.setNome(nome);
        return usuarioFiltros;
    }

    private Usuario umUsuarioComHierarquia() {
        Usuario usuario = usuarioRepository.findOne(110);
        UsuarioHierarquia usuarioHierarquia = criarUsuarioHierarquia(usuario, 113);
        usuario.getUsuariosHierarquia().add(usuarioHierarquia);
        return usuario;
    }

    private UsuarioHierarquia criarUsuarioHierarquia(Usuario usuario, Integer idUsuarioSuperior) {
        return UsuarioHierarquia.criar(usuario, idUsuarioSuperior, usuario.getId());
    }
}
