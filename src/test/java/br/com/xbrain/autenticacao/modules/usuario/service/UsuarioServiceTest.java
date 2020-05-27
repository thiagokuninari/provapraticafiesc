package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioExecutivoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSituacaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioCidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHierarquiaRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import com.google.common.collect.Lists;
import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.CTR_VISUALIZAR_CARTEIRA_HIERARQUIA;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private NotificacaoService notificacaoService;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private UnidadeNegocioRepository unidadeNegocioRepository;
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private EntityManager entityManager;
    @Mock
    private UsuarioHierarquiaRepository usuarioHierarquiaRepository;
    @Mock
    private UsuarioCidadeRepository usuarioCidadeRepository;
    @Mock
    private EquipeVendaD2dService equipeVendaD2dService;

    private static UsuarioExecutivoResponse umUsuarioExecutivo() {
        return new UsuarioExecutivoResponse(1, "bakugo@teste.com", "BAKUGO");
    }

    private static UsuarioSituacaoResponse umUsuarioSituacaoResponse(Integer id, String nome, ESituacao situacao) {
        return UsuarioSituacaoResponse
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }

    @Test
    public void getSubclustersUsuario_deveConverterORetornoEmSelectResponse_conformeListaDeSubclusters() {
        when(usuarioRepository.getSubclustersUsuario(anyInt()))
            .thenReturn(List.of(
                SubCluster.of(1, "TESTE1"),
                SubCluster.of(2, "TESTE2")));

        assertThat(usuarioService.getSubclusterUsuario(1))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "TESTE1"),
                tuple(2, "TESTE2"));
    }

    @Test
    public void buscarExecutivosPorSituacao_deveRetornarOsExecutivos() {
        when(usuarioRepository.findAllExecutivosBySituacao(eq(ESituacao.A)))
            .thenReturn(List.of(umUsuarioExecutivo()));

        assertThat(usuarioService.buscarExecutivosPorSituacao(ESituacao.A))
            .hasSize(1)
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "BAKUGO"));
    }

    @Test
    public void findById_deveRetornarUsuarioResponse_quandoSolicitado() {
        when(usuarioRepository.findById(1))
            .thenReturn(Optional.of(Usuario.builder()
                .id(1)
                .nome("RENATO")
                .build()));

        assertThat(usuarioService.findById(1))
            .extracting("id", "nome")
            .containsExactly(1, "RENATO");
    }

    @Test
    public void findById_deveRetornarException_quandoNaoEncontrarUsuarioById() {
        when(usuarioRepository.findById(1))
            .thenReturn(Optional.empty());

        assertThatCode(() -> usuarioService.findById(1))
            .hasMessage("Usuário não encontrado.")
            .isInstanceOf(ValidacaoException.class);
    }

    @Test
    public void findUsuariosByCodigoCargo_deveRetornarUsuariosAtivos_peloCodigoDoCargo() {
        when(usuarioRepository.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .thenReturn(umaListaUsuariosExecutivosAtivo());

        assertThat(usuarioService.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .extracting("id", "nome", "email", "codigoNivel", "codigoCargo", "codigoDepartamento", "situacao")
            .containsExactly(
                tuple(1, "JOSÉ", "JOSE@HOTMAIL.COM", CodigoNivel.AGENTE_AUTORIZADO,
                    CodigoCargo.EXECUTIVO, CodigoDepartamento.AGENTE_AUTORIZADO, ESituacao.A),
                tuple(2, "HIGOR", "HIGOR@HOTMAIL.COM", CodigoNivel.AGENTE_AUTORIZADO,
                    CodigoCargo.EXECUTIVO, CodigoDepartamento.AGENTE_AUTORIZADO, ESituacao.A));

        verify(usuarioRepository, times(1)).findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO);
    }

    @Test
    public void salvarUsuarioBackoffice_deveSalvar() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());

        usuarioService.salvarUsuarioBackoffice(umUsuarioBackoffice());

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(notificacaoService, atLeastOnce())
            .enviarEmailDadosDeAcesso(argThat(arg -> arg.getNome().equals("Backoffice")), anyString());
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoCpfExistente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());
        when(usuarioRepository.findTop1UsuarioByCpfAndSituacaoNot(any(), any()))
            .thenReturn(Optional.of(umUsuario()));

        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.salvarUsuarioBackoffice(umUsuarioBackoffice()))
            .withMessage("CPF já cadastrado.");

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(usuarioRepository, atLeastOnce()).findTop1UsuarioByCpfAndSituacaoNot(eq("09723864592"), any());
        verify(usuarioRepository, never()).findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any());
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoEmailExistente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());
        when(usuarioRepository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any()))
            .thenReturn(Optional.of(umUsuario()));

        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.salvarUsuarioBackoffice(umUsuarioBackoffice()))
            .withMessage("Email já cadastrado.");

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(usuarioRepository, atLeastOnce()).findTop1UsuarioByCpfAndSituacaoNot(eq("09723864592"), any());
        verify(usuarioRepository, atLeastOnce()).findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any());
    }

    private List<Usuario> umaListaUsuariosExecutivosAtivo() {
        return List.of(
            Usuario.builder()
                .id(1)
                .nome("JOSÉ")
                .email("JOSE@HOTMAIL.COM")
                .situacao(ESituacao.A)
                .departamento(Departamento.builder()
                    .id(1)
                    .codigo(CodigoDepartamento.AGENTE_AUTORIZADO)
                    .build())
                .cargo(Cargo.builder()
                    .id(1)
                    .codigo(CodigoCargo.EXECUTIVO)
                    .nivel(Nivel.builder()
                        .id(1)
                        .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                        .build())
                    .build())
                .build(),
            Usuario.builder()
                .id(2)
                .nome("HIGOR")
                .email("HIGOR@HOTMAIL.COM")
                .situacao(ESituacao.A)
                .departamento(Departamento.builder()
                    .id(1)
                    .codigo(CodigoDepartamento.AGENTE_AUTORIZADO)
                    .build())
                .cargo(Cargo.builder()
                    .id(1)
                    .codigo(CodigoCargo.EXECUTIVO)
                    .nivel(Nivel.builder()
                        .id(1)
                        .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                        .build())
                    .build())
                .build()
        );
    }

    @Test
    public void findUsuariosByIds_deveRetonarUsuarios_quandoForPassadoIdsDosUsuarios() {
        when(usuarioRepository.findUsuariosByIds(any()))
            .thenReturn(List.of(
                umUsuarioSituacaoResponse(1, "JONATHAN", ESituacao.A),
                umUsuarioSituacaoResponse(2, "FLAVIA", ESituacao.I)));

        assertThat(usuarioService.findUsuariosByIds(List.of(1, 2)))
            .extracting("id", "nome", "situacao")
            .containsExactlyInAnyOrder(
                tuple(1, "JONATHAN", ESituacao.A),
                tuple(2, "FLAVIA", ESituacao.I));
    }

    @Test
    public void getAllUsuariosDaHierarquiaD2dDoUserLogado_usuarios_quandoUsuarioDiferenteDeAaExbrain() {
        var usuarioComPermissaoDeVisualizarAa = umUsuario(1, "AGENTE_AUTORIZADO",
            CodigoCargo.AGENTE_AUTORIZADO_SOCIO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioComPermissaoDeVisualizarAa);

        usuarioService.getAllUsuariosDaHierarquiaD2dDoUserLogado();

        verify(usuarioRepository, times(1))
            .findAll(eq(new UsuarioPredicate().ignorarAa(true).ignorarXbrain(true).build()));
    }

    @Test
    public void getAllUsuariosDaHierarquiaD2dDoUserLogado_usuariosDaEquipe_quandoUsuarioEquipeVendas() {
        var usuarioEquipeVendas = umUsuario(1, "OPERACAO",
            CodigoCargo.VENDEDOR_OPERACAO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);

        when(equipeVendaD2dService.getUsuariosPermitidos(any())).thenReturn(List.of());
        when(autenticacaoService.getUsuarioId()).thenReturn(3);
        when(usuarioRepository.getUsuariosSubordinados(any())).thenReturn(new ArrayList<>(List.of(2, 4, 5)));

        usuarioService.getAllUsuariosDaHierarquiaD2dDoUserLogado();

        verify(usuarioRepository, times(1))
            .findAll(eq(new UsuarioPredicate().ignorarAa(true).ignorarXbrain(true)
                .comIds(List.of(3, 2, 4, 5, 1, 1)).build()));
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogado_usuariosSubordinados_quandoUsuarioEquipeVendas() {
        var usuarioEquipeVendas = umUsuario(1, "OPERACAO",
            CodigoCargo.SUPERVISOR_OPERACAO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);

        when(equipeVendaD2dService.getUsuariosPermitidos(any())).thenReturn(List.of());
        when(autenticacaoService.getUsuarioId()).thenReturn(3);
        when(usuarioRepository.getUsuariosSubordinados(any())).thenReturn(Lists.newArrayList(List.of(2, 4, 5)));

        usuarioService.buscarUsuariosDaHierarquiaDoUsuarioLogado();

        verify(equipeVendaD2dService, times(1))
            .getUsuariosPermitidos(argThat(arg -> arg.size() == 3));
        verify(usuarioRepository, times(1))
            .findAll(any(Predicate.class), eq(new Sort(Sort.Direction.ASC, "nome")));
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogado_usuarios_quandoUsuarioCoordenador() {
        var usuarioEquipeVendas = umUsuario(1, "OPERACAO",
            CodigoCargo.COORDENADOR_OPERACAO, CTR_VISUALIZAR_CARTEIRA_HIERARQUIA);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);

        when(autenticacaoService.getUsuarioId()).thenReturn(1);
        when(usuarioRepository.getUsuariosSubordinados(any())).thenReturn(Lists.newArrayList(List.of(2, 4, 5)));

        usuarioService.buscarUsuariosDaHierarquiaDoUsuarioLogado();

        verify(usuarioRepository, times(1))
            .findAll(eq(new UsuarioPredicate().ignorarAa(true).ignorarXbrain(true)
                .comIds(List.of(1, 2, 4, 5)).build()), eq(new Sort(Sort.Direction.ASC, "nome")));
    }

    private Usuario umUsuarioBackoffice() {
        return Usuario.builder()
                .nome("Backoffice")
                .cargo(new Cargo(110))
                .departamento(new Departamento(69))
                .organizacao(new Organizacao(5))
                .cpf("097.238.645-92")
                .email("usuario@teste.com")
                .telefone("43995565661")
                .hierarquiasId(List.of())
                .usuariosHierarquia(new HashSet<>())
                .build();
    }

    private Usuario umUsuario() {
        return Usuario.builder()
            .id(1)
            .nome("Seiya")
            .build();
    }

    private UsuarioAutenticado umUsuario(int usuarioId, String nivelCodigo, CodigoCargo cargo,
                                         CodigoFuncionalidade... permissoes) {
        return UsuarioAutenticado.builder()
            .usuario(getUser(usuarioId, getCargo(cargo)))
            .nivelCodigo(nivelCodigo)
            .cargoCodigo(cargo)
            .id(usuarioId)
            .permissoes(getPermissoes(permissoes))
            .build();
    }

    private Usuario getUser(int usuarioId, Cargo cargo) {
        return Usuario.builder()
            .id(usuarioId)
            .cargo(cargo)
            .build();
    }

    private Cargo getCargo(CodigoCargo cargo) {
        return Cargo.builder()
            .codigo(cargo)
            .build();
    }
        
    private List<SimpleGrantedAuthority> getPermissoes(CodigoFuncionalidade... permissoes) {
        return Objects.nonNull(permissoes)
            ? Arrays.stream(permissoes)
            .map(permissao -> new SimpleGrantedAuthority(permissao.getRole()))
            .collect(Collectors.toList())
            : null;
    }
}