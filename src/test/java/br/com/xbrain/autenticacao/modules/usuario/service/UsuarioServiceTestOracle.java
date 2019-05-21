package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
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
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
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

    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Before
    public void setUp() {
        when(autenticacaoService.getUsuarioId()).thenReturn(101);
    }

    @Test
    public void getVendedoresOperacaoDaHierarquia_idsDosVendedores_quandoForGerenteOperacaoPelaHierarquia() {
        Assert.assertEquals(5, service.getVendedoresOperacaoDaHierarquia(227).size());
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
                CodigoCargo.SUPERVISOR_OPERACAO.name()).size());
    }

    @Test
    public void getIdsSubordinadosDaHierarquia_idsDosVendedores_quandoForCoordenador() {
        Assert.assertEquals(2, service.getIdsSubordinadosDaHierarquia(228,
                CodigoCargo.SUPERVISOR_OPERACAO.name()).size());
        Assert.assertEquals(1, service.getIdsSubordinadosDaHierarquia(234,
                CodigoCargo.SUPERVISOR_OPERACAO.name()).size());
    }

    @Test
    public void deveBuscarSuperioresDoUsuario() {
        UsuarioFiltrosHierarquia usuarioFiltrosHierarquia = getFiltroHierarquia();
        List<UsuarioResponse> usuariosResponse = service.getUsuariosSuperiores(getFiltroHierarquia());
        Assert.assertEquals(1, usuariosResponse.size());
        Assert.assertEquals(usuariosResponse.get(0).getCodigoCargo(), usuarioFiltrosHierarquia.getCodigoCargo());
        Assert.assertEquals(usuariosResponse.get(0).getCodigoDepartamento(), usuarioFiltrosHierarquia.getCodigoDepartamento());
        Assert.assertEquals(usuariosResponse.get(0).getCodigoNivel(), usuarioFiltrosHierarquia.getCodigoNivel());
    }

    @Test
    public void getAllForCsv_ListaComUsuariosParaExportacaoCsv_ComFiltroPorNomeUsuario() {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .nivelCodigo(CodigoNivel.XBRAIN.name())
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
    public void deveBuscarOsUsuarioComInatividade() throws Exception {
        List<Usuario> usuarios = service.getUsuariosSemAcesso();
        Assert.assertEquals(2, usuarios
                .stream()
                .filter(u -> Arrays.asList(104, 101).contains(u.getId()))
                .collect(Collectors.toList())
                .size());
    }

    @Test
    public void deveInativarOsUsuarioComInatividade() throws Exception {
        service.inativarUsuariosSemAcesso();
        List<Usuario> usuarios = service.getUsuariosSemAcesso();
        Assert.assertEquals(0, usuarios.size());

        Assert.assertEquals(ESituacao.I, service.findById(101).getSituacao());
        Assert.assertEquals(ESituacao.I, service.findById(104).getSituacao());

        Assert.assertEquals(1, usuarioHistoricoService.getHistoricoDoUsuario(101)
                .stream().filter(h -> "Inativado por falta de acesso".equals(h.getObservacao())).count());

        Assert.assertEquals(1, usuarioHistoricoService.getHistoricoDoUsuario(104)
                .stream().filter(h -> "Inativado por falta de acesso".equals(h.getObservacao())).count());

        Assert.assertEquals(ESituacao.A, service.findById(100).getSituacao());
        Assert.assertEquals(ESituacao.A, service.findById(366).getSituacao());
    }

    @Test
    public void getPermissoesPorUsuarios_permissoesComUsuario_conformeParametroUsuarioIdAndPermissao() {
        UsuarioPermissoesRequest request = new UsuarioPermissoesRequest();
        request.setPermissoes(Arrays.asList(
                "ROLE_VDS_TABULACAO_DISCADORA",
                "ROLE_VDS_TABULACAO_CLICKTOCALL",
                "ROLE_VDS_TABULACAO_PERSONALIZADA",
                "ROLE_VDS_TABULACAO_MANUAL"));
        request.setUsuariosId(Arrays.asList(245, 243, 231));

        List<UsuarioPermissoesResponse> response = service.findUsuariosByPermissoes(request);
        Assert.assertEquals(3, response.size());
        assertThat(response)
            .containsExactlyElementsOf(
                Arrays.asList(
                    new UsuarioPermissoesResponse(231, Collections.emptyList()),
                    new UsuarioPermissoesResponse(243, Arrays.asList(
                            "ROLE_VDS_TABULACAO_CLICKTOCALL",
                            "ROLE_VDS_TABULACAO_DISCADORA",
                            "ROLE_VDS_TABULACAO_PERSONALIZADA")),
                    new UsuarioPermissoesResponse(245, Arrays.asList(
                            "ROLE_VDS_TABULACAO_MANUAL",
                            "ROLE_VDS_TABULACAO_PERSONALIZADA"))));
    }

    private UsuarioFiltrosHierarquia getFiltroHierarquia() {
        UsuarioFiltrosHierarquia usuarioFiltrosHierarquia = new UsuarioFiltrosHierarquia();
        usuarioFiltrosHierarquia.setUsuarioId(Collections.singletonList(101));
        usuarioFiltrosHierarquia.setCodigoNivel(CodigoNivel.OPERACAO);
        usuarioFiltrosHierarquia.setCodigoDepartamento(CodigoDepartamento.COMERCIAL);
        usuarioFiltrosHierarquia.setCodigoCargo(CodigoCargo.GERENTE_OPERACAO);
        return usuarioFiltrosHierarquia;
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return new UsuarioAutenticado(umUsuarioComHierarquia());
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
