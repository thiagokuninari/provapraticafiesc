package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaFiltros;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresaHistorico;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.rabbitmq.OrganizacaoEmpresaMqSender;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.OrganizacaoEmpresaRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa.A;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganizacaoEmpresaServiceTest {

    @Mock
    private OrganizacaoEmpresaRepository organizacaoEmpresaRepository;
    @InjectMocks
    private OrganizacaoEmpresaService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private OrganizacaoEmpresaHistoricoService historicoService;
    @Captor
    private ArgumentCaptor<OrganizacaoEmpresa> organizacaoEmpresaCaptor;
    @Mock
    private NivelRepository nivelRepository;
    @Mock
    private OrganizacaoEmpresaMqSender sender;

    @Test
    public void findById_notFoundException_quandoNaoExistirOrganizacaoCadastrada() {
        when(organizacaoEmpresaRepository.findById(any()))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.findById(1))
            .withMessage("Organização não encontrada.");

        verify(organizacaoEmpresaRepository, times(1)).findById(eq(1));
    }

    @Test
    public void validarNivel_notFoundException_quandoNaoExistirNivelCadastrada() {
        when(nivelRepository.findById(any()))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.validarNivel(1))
            .withMessage("Nível empresa não encontrada.");

        verify(nivelRepository, times(1)).findById(eq(1));
    }

    @Test
    public void findById_deveRetornarUmaOrganizacaoEmpresa_quandoBuscarPorId() {
        when(organizacaoEmpresaRepository.findById(any()))
            .thenReturn(Optional.of(umaOrganizacaoEmpresaCadastradaId()));

        assertThat(service.findById(1))
            .extracting("id")
            .containsExactly(1);

        verify(organizacaoEmpresaRepository, times(1)).findById(eq(1));
    }

    @Test
    public void getAll_deveRetornarTodosAsOrganizacoes_quandoHouverOrganizacoes() {
        when(organizacaoEmpresaRepository.findAll(any(Predicate.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(umaListaOrganizacoesEmpresa()));

        assertThat(service.getAll(new OrganizacaoEmpresaFiltros(), new PageRequest()))
            .hasSize(3)
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "Organizacao 1"),
                tuple(2, "Organizacao 2"),
                tuple(3, "Organizacao 3")
            );
    }

    @Test
    public void save_validacaoException_quandoExistirUmaOrganizacaoEmpresaComAMesmoNome() {
        when(nivelRepository.findById(eq(1))).thenReturn(Optional.of(OrganizacaoEmpresaHelper.umNivel()));
        when(organizacaoEmpresaRepository.existsByNomeIgnoreCase(eq("Organizacao 1")))
            .thenReturn(true);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umaOrganizacaoEmpresaRequest()))
            .withMessage("Organização já cadastrada com o mesmo nome.");

        verify(organizacaoEmpresaRepository, times(1))
            .existsByNomeIgnoreCase(eq("Organizacao 1"));
    }

    @Test
    public void save_deveSalvarOrganizacaoEmpresa_quandoNivelBackOffice() {
        when(nivelRepository.findById(eq(2))).thenReturn(Optional.of(OrganizacaoEmpresaHelper.umNivelBackoffice()));

        when(organizacaoEmpresaRepository.save(any(OrganizacaoEmpresa.class)))
            .thenReturn(umaOrganizacaoEmpresaBackoffice(2, "Organizacao 2", "BACKOFFICE"));
        when(autenticacaoService.getUsuarioId()).thenReturn(1);

        assertThat(service.save(umaOrganizacaoEmpresaBackofficeRequest()))
            .extracting("nome", "nivel.id", "codigo")
            .contains("Organizacao 2", 2, "BACKOFFICE");

        verify(organizacaoEmpresaRepository, times(1)).save(any(OrganizacaoEmpresa.class));
        verify(sender, times(1)).sendSuccess(any());

    }

    @Test
    public void save_deveSalvarOrganizacaoEmpresa_quandoNivelReceptivo() {
        when(nivelRepository.findById(eq(3))).thenReturn(Optional.of(OrganizacaoEmpresaHelper.umNivelReceptivo()));

        when(organizacaoEmpresaRepository.save(any(OrganizacaoEmpresa.class)))
            .thenReturn(umaOrganizacaoEmpresaReceptivo(1, "Organizacao 3", "RECEPTIVO"));
        when(autenticacaoService.getUsuarioId()).thenReturn(1);

        assertThat(service.save(umaOrganizacaoEmpresaReceptivoRequest()))
            .extracting("nome", "nivel.id", "codigo")
            .contains("Organizacao 3", 3, "RECEPTIVO");

        verify(organizacaoEmpresaRepository, times(1)).save(any(OrganizacaoEmpresa.class));
        verify(sender, times(1)).sendSuccess(any());
    }

    @Test
    public void inativar_validacaoException_quandoOrganizacaoEmpresaInativa() {
        var organizacaoEmpresa = umaOrganizacaoInativa(1, ESituacaoOrganizacaoEmpresa.I);
        when(organizacaoEmpresaRepository.findById(any())).thenReturn(Optional.of(organizacaoEmpresa));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.inativar(1))
            .withMessage("Organização já está inativa.");
    }

    @Test
    public void inativar_deveInativarESalvarHistorico_quandoOrganizacaoEmpresaAtiva() {
        var organizacaoEmpresa = umaOrganizacaoAtiva(1, A);
        when(organizacaoEmpresaRepository.findById(any())).thenReturn(Optional.of(organizacaoEmpresa));
        when(historicoService.salvarHistorico(any(), any(), any())).thenReturn(umaOrganizacaoEmpresaHistorico());

        service.inativar(1);
        Assertions.assertThat(service.findById(1))
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(1, ESituacaoOrganizacaoEmpresa.I);

        verify(historicoService, times(1)).salvarHistorico(organizacaoEmpresaCaptor.capture(),
            eq(EHistoricoAcao.INATIVACAO), any());
        Assertions.assertThat(organizacaoEmpresaCaptor.getValue())
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(1, ESituacaoOrganizacaoEmpresa.I);
        verify(sender, times(1)).sendInativarSituacaoSuccess(any());

    }

    @Test
    public void ativar_validacaoException_quandoOrganizacaoEmpresaAtiva() {
        var organizacaoEmpresa = umaOrganizacaoAtiva(1, A);
        when(organizacaoEmpresaRepository.findById(any())).thenReturn(Optional.of(organizacaoEmpresa));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.ativar(1))
            .withMessage("Organização já está ativa.");
    }

    @Test
    public void ativar_deveAtivarESalvarHistorico_quandoOrganizacaoEmpresaInativa() {
        var organizacaoEmpresa = umaOrganizacaoInativa(1, ESituacaoOrganizacaoEmpresa.I);
        when(organizacaoEmpresaRepository.findById(any())).thenReturn(Optional.of(organizacaoEmpresa));

        service.ativar(1);
        Assertions.assertThat(service.findById(1))
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(1, A);

        verify(historicoService, times(1)).salvarHistorico(organizacaoEmpresaCaptor.capture(),
            eq(EHistoricoAcao.ATIVACAO), any());
        Assertions.assertThat(organizacaoEmpresaCaptor.getValue())
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(1, A);
        verify(sender, times(1)).sendAtivarSituacaoSuccess(any());

    }

    @Test
    public void update_notFoundException_quandoNaoExistirOrganizacaoEmpresa() {
        when(organizacaoEmpresaRepository.findById(1))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.update(1, umaOrganizacaoEmpresaRequest()))
            .withMessage("Organização não encontrada.");

        verify(organizacaoEmpresaRepository, times(1)).findById(eq(1));
    }

    @Test
    public void update_deveSalvarOrganizacaoEmpresaBackofficeEGerarHistorico_quandoForChamado() {
        when(nivelRepository.findById(eq(2))).thenReturn(Optional.of(OrganizacaoEmpresaHelper.umNivelBackoffice()));
        when(organizacaoEmpresaRepository.findById(2)).thenReturn(Optional.of(umaOrganizacaoEmpresaBackoffice(2,
            "Organizacao 2", "BACKOFFICE")));
        when(historicoService.salvarHistorico(any(), any(), any())).thenReturn(umaOrganizacaoEmpresaHistorico());

        service.update(2, umaOrganizacaoEmpresaBackofficeRequest());
        Assertions.assertThat(service.findById(2))
            .extracting("id", "nome", "nivel.id", "situacao")
            .containsExactlyInAnyOrder(2, "Organizacao 2", 2, A);

        verify(historicoService, times(1)).salvarHistorico(organizacaoEmpresaCaptor.capture(),
            eq(EHistoricoAcao.EDICAO), any());
        Assertions.assertThat(organizacaoEmpresaCaptor.getValue())
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(2, A);
        verify(sender, times(1)).sendUpdateSuccess(any());

    }

    @Test
    public void update_deveSalvarOrganizacaoEmpresaReceptivoEGerarHistorico_quandoForChamado() {
        when(nivelRepository.findById(eq(3))).thenReturn(Optional.of(OrganizacaoEmpresaHelper.umNivelReceptivo()));
        when(organizacaoEmpresaRepository.findById(3)).thenReturn(Optional.of(umaOrganizacaoEmpresaReceptivo(3,
            "Organizacao 3", "RECEPTIVO")));
        when(historicoService.salvarHistorico(any(), any(), any())).thenReturn(umaOrganizacaoEmpresaHistorico());

        service.update(3, umaOrganizacaoEmpresaReceptivoRequest());
        Assertions.assertThat(service.findById(3))
            .extracting("id", "nome", "nivel.id", "situacao")
            .containsExactlyInAnyOrder(3, "Organizacao 3", 3, A);

        verify(historicoService, times(1)).salvarHistorico(organizacaoEmpresaCaptor.capture(),
            eq(EHistoricoAcao.EDICAO), any());
        Assertions.assertThat(organizacaoEmpresaCaptor.getValue())
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(3, A);
        verify(sender, times(1)).sendUpdateSuccess(any());

    }

    @Test
    public void update_validacaoException_quandoExistirUmaOrganizacaoEmpresaComOMesmoNome() {
        when(nivelRepository.findById(eq(1))).thenReturn(Optional.of(OrganizacaoEmpresaHelper.umNivel()));
        when(organizacaoEmpresaRepository.findById(1)).thenReturn(Optional.of(umaOrganizacaoEmpresaBackoffice(1,
            "Organizacao 1", "CODIGO")));
        when(organizacaoEmpresaRepository.existsByNomeAndIdNot("Organizacao 1", 1)).thenReturn(true);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.update(1, umaOrganizacaoEmpresaRequest()))
            .withMessage("Organização já cadastrada com o mesmo nome.");
    }

    @Test
    public void findAllByNivelId_deveRetornarUmaListaDeOrganizacaoEmpresa_quandoSolicitado() {
        when(organizacaoEmpresaRepository.findAllByNivelId(1))
            .thenReturn(List.of(OrganizacaoEmpresaHelper.umaOutraOrganizacaoEmpresa()));

        Assertions.assertThat(service.findAllByNivelId(1))
            .extracting("id", "nome", "nivel")
            .containsExactly(
                tuple(2, "Teste AA Dois", OrganizacaoEmpresaHelper.umNivelResponse()));

        verify(organizacaoEmpresaRepository, times(1)).findAllByNivelId(eq(1));
    }

    @Test
    public void findAllByNivelId_deveLancarNotFoundException_quandoNivelIdNaoEncontrado() {
        when(organizacaoEmpresaRepository.findAllByNivelId(1))
            .thenReturn(List.of());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.findAllByNivelId(1))
            .withMessage("Organização não encontrada.");

        verify(organizacaoEmpresaRepository, times(1)).findAllByNivelId(eq(1));
    }

    @Test
    public void findAllAtivos_deveRetornarUmaListaDeOrganizacaoEmpresaAtiva_quandoSolicitado() {
        var filtros = OrganizacaoEmpresaFiltros.builder().nivelId(1).build();

        doReturn(umUsuarioGerenteInternet())
            .when(autenticacaoService)
            .getUsuarioAutenticado();
        doReturn(List.of(OrganizacaoEmpresaHelper.umaOutraOrganizacaoEmpresa()))
            .when(organizacaoEmpresaRepository)
                .findAll(filtros.toPredicate().comSituacao(A).build());

        Assertions.assertThat(service.findAllAtivos(filtros))
            .extracting("id", "nome", "nivel")
            .containsExactly(
                tuple(2, "Teste AA Dois", OrganizacaoEmpresaHelper.umNivelResponse()));

        verify(organizacaoEmpresaRepository, times(1))
            .findAll(any(Predicate.class));
    }

    @Test
    public void findAllAtivos_deveLancarNotFoundException_quandoNivelIdNaoEncontrado() {
        var filtros = OrganizacaoEmpresaFiltros.builder().nivelId(1).build();

        doReturn(umUsuarioGerenteInternet())
            .when(autenticacaoService)
            .getUsuarioAutenticado();
        doReturn(List.of())
            .when(organizacaoEmpresaRepository)
                .findAll(filtros.toPredicate().comSituacao(A).build());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.findAllAtivos(filtros))
            .withMessage("Organização não encontrada.");

        verify(organizacaoEmpresaRepository, times(1))
            .findAll(any(Predicate.class));
    }

    @Test
    public void findAllAtivos_deveLancarValidacaoException_quandoNivelIdNaoInformado() {
        var filtros = OrganizacaoEmpresaFiltros.builder().build();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.findAllAtivos(filtros))
            .withMessage("O campo nível Id é obrigatório!");

        verify(organizacaoEmpresaRepository, never())
            .findAll(any(Predicate.class));
    }

    @Test
    public void findAllAtivos_deveFiltrarPorOrganizacaoId_quandoUsuarioDiferenteDeGerenteInternet() {
        var predicate = OrganizacaoEmpresaFiltros.builder().nivelId(1).organizacaoId(1).situacao(A)
            .build().toPredicate().build();

        doReturn(umUsuarioCoordenadorInternet())
            .when(autenticacaoService)
                .getUsuarioAutenticado();
        doReturn(List.of(OrganizacaoEmpresaHelper.umaOrganizacaoEmpresa()))
            .when(organizacaoEmpresaRepository)
            .findAll(predicate);

        Assertions.assertThat(service.findAllAtivos(OrganizacaoEmpresaFiltros.builder().nivelId(1).build()))
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "Teste AA"));

        verify(organizacaoEmpresaRepository).findAll(predicate);
    }

    @Test
    public void findAll_organizacoesFiltradas_quandoUsuarioBackoffice() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioBackoffice());
        var filtros = OrganizacaoEmpresaFiltros.builder().organizacaoId(1).nome("organizacao 1").build();

        when(organizacaoEmpresaRepository.findByPredicate(eq(filtros.toPredicate().build())))
            .thenReturn(List.of(umaOrganizacaoEmpresaBackoffice(1, "Organizacao 1",
                "CODIGO")));

        assertThat(service.getAllSelect(filtros))
            .hasSize(1)
            .extracting("value", "label")
            .contains(tuple(1, "Organizacao 1"));
    }

    @Test
    public void findAll_organizacoesFiltradas_quandoParametroCodigoNivel() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioBackoffice());
        var filtros = OrganizacaoEmpresaFiltros.builder().codigoNivel(CodigoNivel.BACKOFFICE).organizacaoId(1).build();

        when(organizacaoEmpresaRepository.findByPredicate(eq(filtros.toPredicate().build())))
            .thenReturn(List.of(umaOrganizacaoEmpresaBackoffice(1, "Organizacao 1",
                "CODIGO")));

        assertThat(service.getAllSelect(filtros))
            .hasSize(1)
             .extracting("value", "label")
            .contains(tuple(1, "Organizacao 1"));
    }

    private UsuarioAutenticado umUsuarioBackoffice() {
        return UsuarioAutenticado.builder()
            .nome("Backoffice")
            .cargoId(110)
            .departamentoId(1)
            .organizacaoId(1)
            .nivelCodigo("BACKOFFICE")
            .cpf("097.238.645-92")
            .email("usuario@teste.com")
            .organizacaoCodigo("CODIGO")
            .build();
    }

    private OrganizacaoEmpresaHistorico umaOrganizacaoEmpresaHistorico() {
        return OrganizacaoEmpresaHistorico.builder()
            .organizacaoEmpresa(umaOrganizacaoEmpresaBackoffice(1, "Organizacao 1",
                "CODIGO"))
            .observacao(EHistoricoAcao.ATIVACAO)
            .situacao(ESituacaoOrganizacaoEmpresa.I)
            .dataAlteracao(LocalDateTime.of(2020, 5, 11, 11, 1))
            .usuarioNome("JOHN")
            .usuarioId(2222)
            .build();
    }

    private OrganizacaoEmpresa umaOrganizacaoInativa(Integer id, ESituacaoOrganizacaoEmpresa situacao) {
        return OrganizacaoEmpresa.builder()
            .id(1)
            .situacao(ESituacaoOrganizacaoEmpresa.I)
            .build();
    }

    private OrganizacaoEmpresa umaOrganizacaoAtiva(Integer id, ESituacaoOrganizacaoEmpresa situacao) {
        return OrganizacaoEmpresa.builder()
            .id(1)
            .situacao(A)
            .build();
    }

    private OrganizacaoEmpresaRequest umaOrganizacaoEmpresaRequest() {
        return OrganizacaoEmpresaRequest.builder()
            .nome("Organizacao 1")
            .nivelId(1)
            .situacao(A)
            .build();
    }

    private OrganizacaoEmpresaRequest umaOrganizacaoEmpresaBackofficeRequest() {
        return OrganizacaoEmpresaRequest.builder()
            .nome("Organizacao 2")
            .nivelId(2)
            .situacao(A)
            .build();
    }

    private OrganizacaoEmpresaRequest umaOrganizacaoEmpresaReceptivoRequest() {
        return OrganizacaoEmpresaRequest.builder()
            .nome("Organizacao 3")
            .nivelId(3)
            .situacao(A)
            .build();
    }

    public static Usuario umUsuario() {
        var usuario = new Usuario();
        usuario.setId(100);
        usuario.setNome("Thiago");
        return usuario;
    }

    public static UsuarioAutenticado umUsuarioAutenticado() {
        var usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setId(2);
        usuarioAutenticado.setNome("Thiago");
        return usuarioAutenticado;
    }

    private OrganizacaoEmpresa umaOrganizacaoEmpresaCadastradaId() {
        return OrganizacaoEmpresa.builder()
            .id(1)
            .build();
    }

    public static List<OrganizacaoEmpresa> umaListaOrganizacoesEmpresa() {
        return List.of(
            umaOrganizacaoEmpresaBackoffice(1, "Organizacao 1", "CODIGO"),
            umaOrganizacaoEmpresaBackoffice(2, "Organizacao 2", "CODIGO2"),
            umaOrganizacaoEmpresaBackoffice(3, "Organizacao 3", "CODIGO3")
        );
    }

    public static OrganizacaoEmpresa umaOrganizacaoEmpresaBackoffice(Integer id, String nome, String codigo) {
        return OrganizacaoEmpresa.builder()
            .id(id)
            .nome(nome)
            .nivel(Nivel.builder()
                .id(2)
                .codigo(CodigoNivel.BACKOFFICE)
                .nome("BACKOFFICE")
                .situacao(ESituacao.A)
                .exibirCadastroUsuario(Eboolean.V)
                .build())
            .situacao(A)
            .dataCadastro(LocalDateTime.now())
            .usuarioCadastro(umUsuario())
            .codigo(codigo)
            .build();
    }

    public static OrganizacaoEmpresa umaOrganizacaoEmpresaReceptivo(Integer id, String nome, String codigo) {
        return OrganizacaoEmpresa.builder()
            .id(id)
            .nome(nome)
            .nivel(Nivel.builder()
                .id(3)
                .codigo(CodigoNivel.RECEPTIVO)
                .nome("RECEPTIVO")
                .situacao(ESituacao.A)
                .exibirCadastroUsuario(Eboolean.V)
                .build())
            .situacao(A)
            .dataCadastro(LocalDateTime.now())
            .usuarioCadastro(umUsuario())
            .codigo(codigo)
            .build();
    }

    private UsuarioAutenticado umUsuarioCoordenadorInternet() {
        return UsuarioAutenticado.builder()
            .nome("COORDENADOR")
            .cargoId(502)
            .departamentoId(3)
            .organizacaoId(1)
            .nivelCodigo("OPERACAO")
            .cpf("097.238.222-11")
            .email("COORDENADOR@INTERNET.COM")
            .organizacaoCodigo("INTERNET")
            .build();
    }

    private UsuarioAutenticado umUsuarioGerenteInternet() {
        return UsuarioAutenticado.builder()
            .nome("GERENTE")
            .cargoId(500)
            .departamentoId(3)
            .organizacaoId(1)
            .nivelCodigo("OPERACAO")
            .cpf("097.234.222-11")
            .email("GERENTE@INTERNET.COM")
            .organizacaoCodigo("INTERNET")
            .cargoCodigo(CodigoCargo.INTERNET_GERENTE)
            .build();
    }
}
