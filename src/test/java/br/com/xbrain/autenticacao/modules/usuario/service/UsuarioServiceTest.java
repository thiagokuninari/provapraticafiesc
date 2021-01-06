package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.CTR_VISUALIZAR_CARTEIRA_HIERARQUIA;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioPredicateHelper.umVendedoresFeederPredicateComSocioPrincipal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.VendedoresFeederFiltrosHelper.umVendedoresFeederFiltros;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;
    @Mock
    private UsuarioRepository repository;
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
    @Mock
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;

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
        when(repository.getSubclustersUsuario(anyInt()))
            .thenReturn(List.of(
                SubCluster.of(1, "TESTE1"),
                SubCluster.of(2, "TESTE2")));

        assertThat(service.getSubclusterUsuario(1))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "TESTE1"),
                tuple(2, "TESTE2"));
    }

    @Test
    public void buscarExecutivosPorSituacao_deveRetornarOsExecutivos() {
        when(repository.findAllExecutivosBySituacao(eq(ESituacao.A)))
            .thenReturn(List.of(umUsuarioExecutivo()));

        assertThat(service.buscarExecutivosPorSituacao(ESituacao.A))
            .hasSize(1)
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "BAKUGO"));
    }

    @Test
    public void findById_deveRetornarUsuarioResponse_quandoSolicitado() {
        when(repository.findById(1))
            .thenReturn(Optional.of(Usuario.builder()
                .id(1)
                .nome("RENATO")
                .build()));

        assertThat(service.findById(1))
            .extracting("id", "nome")
            .containsExactly(1, "RENATO");
    }

    @Test
    public void findById_deveRetornarException_quandoNaoEncontrarUsuarioById() {
        when(repository.findById(1))
            .thenReturn(Optional.empty());

        assertThatCode(() -> service.findById(1))
            .hasMessage("Usuário não encontrado.")
            .isInstanceOf(ValidacaoException.class);
    }

    @Test
    public void findUsuariosByCodigoCargo_deveRetornarUsuariosAtivos_peloCodigoDoCargo() {
        when(repository.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .thenReturn(umaListaUsuariosExecutivosAtivo());

        assertThat(service.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .extracting("id", "nome", "email", "codigoNivel", "codigoCargo", "codigoDepartamento", "situacao")
            .containsExactly(
                tuple(1, "JOSÉ", "JOSE@HOTMAIL.COM", CodigoNivel.AGENTE_AUTORIZADO,
                    CodigoCargo.EXECUTIVO, CodigoDepartamento.AGENTE_AUTORIZADO, ESituacao.A),
                tuple(2, "HIGOR", "HIGOR@HOTMAIL.COM", CodigoNivel.AGENTE_AUTORIZADO,
                    CodigoCargo.EXECUTIVO, CodigoDepartamento.AGENTE_AUTORIZADO, ESituacao.A));

        verify(repository, times(1)).findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO);
    }

    @Test
    public void salvarUsuarioBackoffice_deveSalvar() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());

        service.salvarUsuarioBackoffice(umUsuarioBackoffice());

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(notificacaoService, atLeastOnce())
            .enviarEmailDadosDeAcesso(argThat(arg -> arg.getNome().equals("Backoffice")), anyString());
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoCpfExistente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());
        when(repository.findTop1UsuarioByCpfAndSituacaoNot(any(), any()))
            .thenReturn(Optional.of(umUsuario()));

        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarUsuarioBackoffice(umUsuarioBackoffice()))
            .withMessage("CPF já cadastrado.");

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(repository, atLeastOnce()).findTop1UsuarioByCpfAndSituacaoNot(eq("09723864592"), any());
        verify(repository, never()).findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any());
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoEmailExistente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());
        when(repository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any()))
            .thenReturn(Optional.of(umUsuario()));

        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarUsuarioBackoffice(umUsuarioBackoffice()))
            .withMessage("Email já cadastrado.");

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(repository, atLeastOnce()).findTop1UsuarioByCpfAndSituacaoNot(eq("09723864592"), any());
        verify(repository, atLeastOnce()).findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any());
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
        when(repository.findUsuariosByIds(any()))
            .thenReturn(List.of(
                umUsuarioSituacaoResponse(1, "JONATHAN", ESituacao.A),
                umUsuarioSituacaoResponse(2, "FLAVIA", ESituacao.I)));

        assertThat(service.findUsuariosByIds(List.of(1, 2)))
            .extracting("id", "nome", "situacao")
            .containsExactlyInAnyOrder(
                tuple(1, "JONATHAN", ESituacao.A),
                tuple(2, "FLAVIA", ESituacao.I));
    }

    @Test
    public void buscarUsuariosAtivosNivelOperacaoCanalAa_doisUsuarios_quandoAtivoECanalAa() {
        when(repository.findAllAtivosByNivelOperacaoCanalAa())
            .thenReturn(umaListaSelectResponse());

        assertThat(service.buscarUsuariosAtivosNivelOperacaoCanalAa())
            .extracting("value", "label")
            .containsExactly(
                tuple(100, "JOSÉ"),
                tuple(101, "JOÃO")
            );

        verify(repository).findAllAtivosByNivelOperacaoCanalAa();
    }

    private List<SelectResponse> umaListaSelectResponse() {
        return List.of(
            SelectResponse.of(100, "JOSÉ"),
            SelectResponse.of(101, "JOÃO")
        );
    }

    @Test
    public void getAllUsuariosDaHierarquiaD2dDoUserLogado_usuarios_quandoUsuarioDiferenteDeAaExbrain() {
        var usuarioComPermissaoDeVisualizarAa = umUsuario(1, "AGENTE_AUTORIZADO",
            CodigoCargo.AGENTE_AUTORIZADO_SOCIO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioComPermissaoDeVisualizarAa);

        service.getAllUsuariosDaHierarquiaD2dDoUserLogado();

        verify(repository, times(1))
            .findAll(eq(new UsuarioPredicate().ignorarAa(true).ignorarXbrain(true).build()));
    }

    @Test
    public void getAllUsuariosDaHierarquiaD2dDoUserLogado_usuariosDaEquipe_quandoUsuarioEquipeVendas() {
        var usuarioEquipeVendas = umUsuario(1, "OPERACAO",
            CodigoCargo.VENDEDOR_OPERACAO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);

        when(equipeVendaD2dService.getUsuariosPermitidos(any())).thenReturn(List.of());
        when(autenticacaoService.getUsuarioId()).thenReturn(3);
        when(repository.getUsuariosSubordinados(any())).thenReturn(new ArrayList<>(List.of(2, 4, 5)));

        service.getAllUsuariosDaHierarquiaD2dDoUserLogado();

        verify(repository, times(1))
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
        when(repository.getUsuariosSubordinados(any())).thenReturn(Lists.newArrayList(List.of(2, 4, 5)));

        service.buscarUsuariosDaHierarquiaDoUsuarioLogado(null);

        verify(equipeVendaD2dService, times(1))
            .getUsuariosPermitidos(argThat(arg -> arg.size() == 3));
        verify(repository, times(1))
            .findAll(any(Predicate.class), eq(new Sort(Sort.Direction.ASC, "nome")));
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogado_usuarios_quandoUsuarioCoordenador() {
        var usuarioEquipeVendas = umUsuario(1, "OPERACAO",
            CodigoCargo.COORDENADOR_OPERACAO, CTR_VISUALIZAR_CARTEIRA_HIERARQUIA);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);

        when(repository.getUsuariosSubordinados(any())).thenReturn(Lists.newArrayList(List.of(2, 4, 5)));

        service.buscarUsuariosDaHierarquiaDoUsuarioLogado(null);

        verify(repository, times(1))
            .findAll(any(Predicate.class), eq(new Sort(Sort.Direction.ASC, "nome")));
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

    @Test
    public void getUsuarioSuperior_usuarioResponseVazio_quandoNaoEncontrarSuperiorDoUsuario() {
        when(repository.getUsuarioSuperior(100)).thenReturn(Optional.empty());

        assertThat(service.getUsuarioSuperior(100))
            .isEqualTo(new UsuarioResponse());
    }

    @Test
    public void getUsuarioSuperior_usuarioResponse_quandoBuscarSuperiorDoUsuario() {
        when(repository.getUsuarioSuperior(100))
            .thenReturn(Optional.of(umUsuarioHierarquia()));

        assertThat(service.getUsuarioSuperior(100))
            .isEqualTo(UsuarioResponse.builder()
                .id(100)
                .nome("RENATO")
                .telefone("43 3322-0000")
                .email("RENATO@GMAIL.COM")
                .situacao(ESituacao.A)
                .codigoUnidadesNegocio(List.of(
                    CodigoUnidadeNegocio.CLARO_RESIDENCIAL,
                    CodigoUnidadeNegocio.RESIDENCIAL_COMBOS))
                .codigoCargo(CodigoCargo.SUPERVISOR_OPERACAO)
                .codigoDepartamento(CodigoDepartamento.COMERCIAL)
                .codigoEmpresas(List.of(CodigoEmpresa.CLARO_RESIDENCIAL))
                .codigoNivel(CodigoNivel.OPERACAO)
                .cpf("333.333.333-33")
                .build());
    }

    private UsuarioHierarquia umUsuarioHierarquia() {
        return UsuarioHierarquia.builder()
            .usuarioSuperior(umUsuarioSuperior())
            .usuario(new Usuario(100))
            .usuarioCadastro(new Usuario(103))
            .dataCadastro(LocalDateTime.now())
            .build();
    }

    private Usuario umUsuarioSuperior() {
        return Usuario.builder()
            .id(100)
            .cpf("333.333.333-33")
            .telefone("43 3322-0000")
            .situacao(ESituacao.A)
            .cargo(umCargoSupervisorOperacao())
            .departamento(umDepartamentoComercial())
            .nome("RENATO")
            .email("RENATO@GMAIL.COM")
            .situacao(ESituacao.A)
            .unidadesNegocios(List.of(
                umaUnidadeNegocio(CodigoUnidadeNegocio.CLARO_RESIDENCIAL),
                umaUnidadeNegocio(CodigoUnidadeNegocio.RESIDENCIAL_COMBOS)))
            .empresas(List.of(umaEmpresa()))
            .build();
    }

    private Cargo umCargoSupervisorOperacao() {
        return Cargo.builder()
            .id(1)
            .codigo(CodigoCargo.SUPERVISOR_OPERACAO)
            .nivel(umNivelOperacao())
            .nome(CodigoCargo.SUPERVISOR_OPERACAO.name())
            .situacao(ESituacao.A)
            .build();
    }

    private Nivel umNivelOperacao() {
        return Nivel.builder()
            .id(1)
            .codigo(CodigoNivel.OPERACAO)
            .nome(CodigoNivel.OPERACAO.name())
            .build();
    }

    private Departamento umDepartamentoComercial() {
        return Departamento.builder()
            .id(1)
            .codigo(CodigoDepartamento.COMERCIAL)
            .nome(CodigoDepartamento.COMERCIAL.name())
            .build();
    }

    private UnidadeNegocio umaUnidadeNegocio(CodigoUnidadeNegocio codigoUnidadeNegocio) {
        return UnidadeNegocio.builder()
            .codigo(codigoUnidadeNegocio)
            .nome(codigoUnidadeNegocio.name())
            .situacao(ESituacao.A)
            .build();
    }

    private Empresa umaEmpresa() {
        return Empresa.builder()
            .id(1)
            .codigo(CodigoEmpresa.CLARO_RESIDENCIAL)
            .nome(CodigoEmpresa.CLARO_RESIDENCIAL.name())
            .build();
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSales() {
        var umUsuarioComLogin = 1000;

        when(repository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(umUsuarioComLoginNetSales(umUsuarioComLogin)));

        var response = service.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "ATIVO_LOCAL_PROPRIO",
                "123.456.887-91");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSalesEForOperador() {
        var umUsuarioComLogin = 1000;
        var user = umUsuarioComLoginNetSales(umUsuarioComLogin);
        user.setCanais(Set.of(ECanal.AGENTE_AUTORIZADO));
        user.getCargo().setNivel(Nivel.builder().codigo(CodigoNivel.OPERACAO).build());
        when(repository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(user));

        var response = service.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "OPERACAO_AGENTE_AUTORIZADO",
                "123.456.887-91");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSalesEForReceptivo() {
        var umUsuarioComLogin = 1000;
        var user = umUsuarioComLoginNetSales(umUsuarioComLogin);
        user.setOrganizacao(Organizacao.builder().codigo("ATENTO").build());
        user.getCargo().setNivel(Nivel.builder().codigo(CodigoNivel.RECEPTIVO).build());
        when(repository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(user));

        var response = service.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "RECEPTIVO_ATENTO",
                "123.456.887-91");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveLancarException_seUsuarioNaoPossuirLoginNetSales() {
        var umUsuarioSemLogin = 1001;

        when(repository.findById(umUsuarioSemLogin))
            .thenReturn(Optional.of(umUsuarioSemLoginNetSales(umUsuarioSemLogin)));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getUsuarioByIdComLoginNetSales(umUsuarioSemLogin))
            .withMessage("Usuário não possui login NetSales válido.");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveLancarException_seUsuarioNaoEncontrado() {
        var umUsuarioInexistente = 1002;

        when(repository.findById(umUsuarioInexistente))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getUsuarioByIdComLoginNetSales(umUsuarioInexistente))
            .withMessage("Usuário não encontrado.");
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaVazia_quandoNaoHouverUsuariosDosAgentesAutorizados() {
        when(agenteAutorizadoNovoService.buscarUsuariosDoAgenteAutorizado(eq(1), eq(false)))
            .thenReturn(List.of());

        assertThat(service.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), null, false)))
            .isEmpty();

        verify(repository, never()).findAll(any(Predicate.class));
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaUsuarioConsultaDto_quandoBuscarInativosNull() {
        when(agenteAutorizadoNovoService.buscarUsuariosDoAgenteAutorizado(eq(1), eq(false)))
            .thenReturn(List.of(umUsuarioDtoVendas(1)));
        when(repository.findAll(eq(umVendedoresFeederPredicateComSocioPrincipal(List.of(1)).build())))
            .thenReturn(List.of(umUsuarioCompleto()));

        assertThat(service.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), true, false)))
            .hasSize(1)
            .extracting("id", "nome", "email", "cpf", "unidadeNegocioNome", "empresaNome", "situacao",
                "nivelCodigo", "nivelNome", "cargoNome", "departamentoNome")
            .containsExactly(tuple(1, "NOME UM", "email@email.com", "111.111.111-11",
                "UNIDADE NEGÓCIO UM", "EMPRESA UM", "A", "AGENTE_AUTORIZADO", "AGENTE AUTORIZADO", null, "DEPARTAMENTO UM"));
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaUsuarioConsultaDto_quandoBuscarInativosFalse() {
        when(agenteAutorizadoNovoService.buscarUsuariosDoAgenteAutorizado(eq(1), eq(false)))
            .thenReturn(List.of(umUsuarioDtoVendas(1)));
        when(repository.findAll(eq(umVendedoresFeederPredicateComSocioPrincipal(List.of(1)).build())))
            .thenReturn(List.of(umUsuarioCompleto()));

        assertThat(service.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), true, false)))
            .hasSize(1)
            .extracting("id", "nome", "email", "cpf", "unidadeNegocioNome", "empresaNome", "situacao",
                "nivelCodigo", "nivelNome", "cargoNome", "departamentoNome")
            .containsExactly(tuple(1, "NOME UM", "email@email.com", "111.111.111-11",
                "UNIDADE NEGÓCIO UM", "EMPRESA UM", "A", "AGENTE_AUTORIZADO", "AGENTE AUTORIZADO", null, "DEPARTAMENTO UM"));
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaUsuarioConsultaDto_quandoBuscarInativosTrue() {
        when(agenteAutorizadoNovoService.buscarUsuariosDoAgenteAutorizado(eq(1), eq(true)))
            .thenReturn(List.of(umUsuarioDtoVendas(1)));
        when(repository.findAll(eq(umVendedoresFeederPredicateComSocioPrincipal(List.of(1)).build())))
            .thenReturn(List.of(umUsuarioCompleto()));

        assertThat(service.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), true, true)))
            .hasSize(1)
            .extracting("id", "nome", "email", "cpf", "unidadeNegocioNome", "empresaNome", "situacao",
                "nivelCodigo", "nivelNome", "cargoNome", "departamentoNome")
            .containsExactly(tuple(1, "NOME UM", "email@email.com", "111.111.111-11",
                "UNIDADE NEGÓCIO UM", "EMPRESA UM", "A", "AGENTE_AUTORIZADO", "AGENTE AUTORIZADO", null, "DEPARTAMENTO UM"));
    }

    private Usuario umUsuarioComLoginNetSales(int id) {
        return Usuario.builder()
            .id(id)
            .nome("UM USUARIO COM LOGIN")
            .loginNetSales("UM LOGIN NETSALES")
            .cargo(Cargo.builder()
                .codigo(CodigoCargo.VENDEDOR_ATIVO_LOCAL_PROPRIO)
                .nivel(Nivel.builder().codigo(CodigoNivel.ATIVO_LOCAL_PROPRIO).build())
                .build())
            .cpf("123.456.887-91")
            .situacao(ESituacao.A)
            .build();
    }

    private Usuario umUsuarioSemLoginNetSales(int id) {
        var usuario = umUsuarioComLoginNetSales(id);
        usuario.setLoginNetSales(null);
        return usuario;
    }

    private UsuarioDtoVendas umUsuarioDtoVendas(Integer id) {
        return UsuarioDtoVendas
            .builder()
            .id(id)
            .build();
    }

    private Usuario umUsuarioCompleto() {
        return Usuario
            .builder()
            .id(1)
            .nome("NOME UM")
            .email("email@email.com")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .cargo(Cargo
                .builder()
                .nivel(Nivel
                    .builder()
                    .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                    .nome("AGENTE AUTORIZADO")
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .nome("UNIDADE NEGÓCIO UM")
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .nome("EMPRESA UM")
                .build()))
            .build();
    }
}