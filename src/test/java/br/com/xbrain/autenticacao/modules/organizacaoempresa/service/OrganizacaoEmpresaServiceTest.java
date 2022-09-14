package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaFiltros;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.ModalidadeEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresaHistorico;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.ModalidadeEmpresaRepository;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.OrganizacaoEmpresaRepository;
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
    private ModalidadeEmpresaRepository modalidadeEmpresaRepository;

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
    public void validarModalidadeEmpresa_notFoundException_quandoNaoExistirModalidadeEmpresaCadastrada() {
        when(modalidadeEmpresaRepository.findAll(anyIterable()))
            .thenReturn(null);

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.validarModalidadeEmpresa(List.of(1,2)))
            .withMessage("Modalidade empresa não encontrada.");

        verify(modalidadeEmpresaRepository, times(1)).findAll(anyIterable());
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
            .extracting("id", "razaoSocial", "cnpj")
            .containsExactly(
                tuple(1, "Organizacao 1", "97527243000114"),
                tuple(2, "Organizacao 2", "06890869000135"),
                tuple(3, "Organizacao 3", "71111221000185")
            );
    }

    @Test
    public void save_validacaoException_quandoExistirUmaOrganizacaoEmpresaComAMesmaRazaoSocial() {
        when(organizacaoEmpresaRepository.existsByRazaoSocialIgnoreCase(eq("Organizacao 1")))
            .thenReturn(true);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umaOrganizacaoEmpresaRequest()))
            .withMessage("Organização já cadastrada com o mesmo nome.");

        verify(organizacaoEmpresaRepository, times(1))
            .existsByRazaoSocialIgnoreCase(eq("Organizacao 1"));
    }

    @Test
    public void save_validacaoException_quandoExistirUmaOrganizacaoEmpresaComOMesmoCnpj() {
        when(organizacaoEmpresaRepository.existsByCnpj("08112392000192"))
            .thenReturn(true);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umaOrganizacaoEmpresaRequest()))
            .withMessage("Organização já cadastrada com o mesmo CNPJ.");

        verify(organizacaoEmpresaRepository, times(1)).existsByCnpj(anyString());
    }

    @Test
    public void save_deveRetornarOrganizacaoEmpresa_quandoForSalvo() {
        when(nivelRepository.findById(eq(1))).thenReturn(Optional.of(OrganizacaoEmpresaHelper.umNivel()));
        when(modalidadeEmpresaRepository.findAll(anyIterable())).thenReturn(List.of(umaModalidadeEmpresaTelevendas(),
            umaModalidadeEmpresaPap()));
        when(organizacaoEmpresaRepository.save(any(OrganizacaoEmpresa.class)))
            .thenReturn(umaOrganizacaoEmpresa(1, "Organizacao 1", "08112392000192"));
        when(autenticacaoService.getUsuarioId()).thenReturn(1);

        assertThat(service.save(umaOrganizacaoEmpresaRequest()))
            .extracting("razaoSocial", "cnpj")
            .contains("Organizacao 1", "08112392000192");

        verify(organizacaoEmpresaRepository, times(1)).save(any(OrganizacaoEmpresa.class));

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
        var organizacaoEmpresa = umaOrganizacaoAtiva(1, ESituacaoOrganizacaoEmpresa.A);
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
    }

    @Test
    public void ativar_validacaoException_quandoOrganizacaoEmpresaAtiva() {
        var organizacaoEmpresa = umaOrganizacaoAtiva(1, ESituacaoOrganizacaoEmpresa.A);
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
            .containsExactlyInAnyOrder(1, ESituacaoOrganizacaoEmpresa.A);

        verify(historicoService, times(1)).salvarHistorico(organizacaoEmpresaCaptor.capture(),
            eq(EHistoricoAcao.ATIVACAO), any());
        Assertions.assertThat(organizacaoEmpresaCaptor.getValue())
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(1, ESituacaoOrganizacaoEmpresa.A);
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
    public void update_deveSalvarEGerarHistorico_quandoForChamado() {
        when(nivelRepository.findById(eq(1))).thenReturn(Optional.of(OrganizacaoEmpresaHelper.umNivel()));
        when(modalidadeEmpresaRepository.findAll(anyIterable())).thenReturn(List.of(umaModalidadeEmpresaTelevendas(),
            umaModalidadeEmpresaPap()));
        when(organizacaoEmpresaRepository.findById(1)).thenReturn(Optional.of(umaOrganizacaoEmpresa(1,
            "Organizacao 4", "08112392000192")));
        when(historicoService.salvarHistorico(any(), any(), any())).thenReturn(umaOrganizacaoEmpresaHistorico());

        service.update(1, umaOrganizacaoEmpresaRequest());
        Assertions.assertThat(service.findById(1))
            .extracting("id", "razaoSocial", "cnpj", "modalidadesEmpresa", "nivel", "situacao")
            .containsExactlyInAnyOrder(1, "Organizacao 1", "08112392000192", List.of(umaModalidadeEmpresaTelevendas(),
                    umaModalidadeEmpresaPap()), OrganizacaoEmpresaHelper.umNivel(), ESituacaoOrganizacaoEmpresa.A);

        verify(historicoService, times(1)).salvarHistorico(organizacaoEmpresaCaptor.capture(),
            eq(EHistoricoAcao.EDICAO), any());
        Assertions.assertThat(organizacaoEmpresaCaptor.getValue())
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(1, ESituacaoOrganizacaoEmpresa.A);
    }

    @Test
    public void findAllByNivelId_deveRetornarUmaListaDeOrganizacaoEmpresa_quandoSolicitado() {
        when(organizacaoEmpresaRepository.findAllByNivelId(1))
            .thenReturn(List.of(OrganizacaoEmpresaHelper.umaOutraOrganizacaoEmpresa()));

        Assertions.assertThat(service.findAllByNivelId(1))
            .extracting("id", "razaoSocial", "nivel")
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
    public void findAllByNivelIdAndSituacao_deveRetornarUmaListaDeOrganizacaoEmpresaAtiva_quandoSolicitado() {
        when(organizacaoEmpresaRepository.findAllByNivelIdAndSituacao(1, ESituacaoOrganizacaoEmpresa.A))
            .thenReturn(List.of(OrganizacaoEmpresaHelper.umaOutraOrganizacaoEmpresa()));

        Assertions.assertThat(service.findAllAtivosByNivelId(1))
            .extracting("id", "razaoSocial", "nivel")
            .containsExactly(
                tuple(2, "Teste AA Dois", OrganizacaoEmpresaHelper.umNivelResponse()));

        verify(organizacaoEmpresaRepository, times(1))
            .findAllByNivelIdAndSituacao(eq(1), eq(ESituacaoOrganizacaoEmpresa.A));
    }

    @Test
    public void findAllByNivelIdAndSituacao_deveLancarNotFoundException_quandoNivelIdNaoEncontrado() {
        when(organizacaoEmpresaRepository.findAllByNivelIdAndSituacao(1, ESituacaoOrganizacaoEmpresa.A))
            .thenReturn(List.of());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.findAllAtivosByNivelId(1))
            .withMessage("Organização não encontrada.");

        verify(organizacaoEmpresaRepository, times(1))
            .findAllByNivelIdAndSituacao(eq(1), eq(ESituacaoOrganizacaoEmpresa.A));
    }

    private OrganizacaoEmpresaHistorico umaOrganizacaoEmpresaHistorico() {
        return OrganizacaoEmpresaHistorico.builder()
            .organizacaoEmpresa(umaOrganizacaoEmpresa(1, "Organizacao 1", "08112392000192" ))
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
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
    }

    private OrganizacaoEmpresaRequest umaOrganizacaoEmpresaRequest() {
        return OrganizacaoEmpresaRequest.builder()
            .razaoSocial("Organizacao 1")
            .cnpj("08112392000192")
            .nivelId(1)
            .modalidadesEmpresaIds(List.of(1,2))
            .situacao(ESituacaoOrganizacaoEmpresa.A)
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
            umaOrganizacaoEmpresa(1, "Organizacao 1", "97527243000114"),
            umaOrganizacaoEmpresa(2, "Organizacao 2", "06890869000135"),
            umaOrganizacaoEmpresa(3, "Organizacao 3", "71111221000185")
        );
    }

    public static ModalidadeEmpresa umaModalidadeEmpresaPap() {
        var modalidadeEmpresa = new ModalidadeEmpresa();
        modalidadeEmpresa.setId(1);
        modalidadeEmpresa.setModalidadeEmpresa(null);
        return modalidadeEmpresa;
    }

    public static ModalidadeEmpresa umaModalidadeEmpresaTelevendas() {
        var modalidadeEmpresa = new ModalidadeEmpresa();
        modalidadeEmpresa.setId(2);
        modalidadeEmpresa.setModalidadeEmpresa(null);
        return modalidadeEmpresa;
    }

    public static OrganizacaoEmpresa umaOrganizacaoEmpresa(Integer id, String razaoSocial, String cnpj) {
        return OrganizacaoEmpresa.builder()
            .id(id)
            .razaoSocial(razaoSocial)
            .cnpj(cnpj)
            .modalidadesEmpresa(List.of(umaModalidadeEmpresaPap(),umaModalidadeEmpresaTelevendas()))
            .nivel(Nivel.builder().id(1).build())
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .dataCadastro(LocalDateTime.now())
            .usuarioCadastro(umUsuario())
            .build();
    }
}
