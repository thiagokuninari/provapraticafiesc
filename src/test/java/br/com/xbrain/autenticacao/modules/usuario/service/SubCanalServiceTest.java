package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalFiltros;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubCanalRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.PermissoesHelper.umaPermissaoIndicacaoInsideSalesPme;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.PermissoesHelper.umaPermissaoIndicacaoPremium;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService.FUNC_CONSULTAR_INDICACAO_INSIDE_SALES_PME;
import static br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService.FUNC_CONSULTAR_INDICACAO_PREMIUM;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubCanalServiceTest {

    @InjectMocks
    private SubCanalService subCanalService;
    @Mock
    private SubCanalRepository subCanalRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Captor
    private ArgumentCaptor<SubCanal> subCanalArgumentCaptor;

    @Test
    public void getAll_deveRetornarTodosSubCanais_quandoSolicitado() {
        when(subCanalRepository.findAll()).thenReturn(List.of(umSubCanal()));

        assertThat(subCanalService.getAll())
            .hasSize(1)
            .containsExactly(new SubCanalDto(1, PAP, "PAP", ESituacao.A, Eboolean.F));
    }

    @Test
    public void getSubCanalById_deveRetornarSubCanalResponse_quandoHouverSubCanal() {
        when(subCanalRepository.findById(anyInt())).thenReturn(Optional.of(umSubCanal()));

        assertThat(subCanalService.getSubCanalById(1))
            .isEqualTo(new SubCanalDto(1, PAP, "PAP", ESituacao.A, Eboolean.F));
    }

    @Test
    public void getSubCanalById_deveLancarException_quandoNaoHouverSubCanal() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> subCanalService.getSubCanalById(100))
            .withMessage("Erro, subcanal não encontrado.");
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoIndicacaoPremium_deveAdicionarPermissaoIndicacaoPremium_quandoUsuarioNivelOperacaoComSubCanalPapPremium() {
        doReturn(List.of(umaPermissaoIndicacaoPremium()))
            .when(usuarioService)
            .getPermissoesEspeciaisDoUsuario(101112, 23, FUNC_CONSULTAR_INDICACAO_PREMIUM);

        assertThatCode(() -> subCanalService
            .adicionarPermissaoIndicacaoPremium(umUsuarioOperacaoComSubCanal(101112, 3, PAP_PREMIUM)))
            .doesNotThrowAnyException();

        verify(usuarioService).getPermissoesEspeciaisDoUsuario(101112, 23, FUNC_CONSULTAR_INDICACAO_PREMIUM);
        verify(usuarioService).salvarPermissoesEspeciais(List.of(umaPermissaoIndicacaoPremium()));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoIndicacaoPremium_naoDeveAdicionarPermissaoIndicacaoPremium_quandoUsuarioNivelOperacaoSemSubCanalPapPremium() {
        assertThatCode(() -> subCanalService
            .adicionarPermissaoIndicacaoPremium(umUsuarioOperacaoComSubCanal(101112, 2, PAP_PME)))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).getPermissoesEspeciaisDoUsuario(anyInt(), anyInt(), anyList());
        verify(usuarioService, never()).salvarPermissoesEspeciais(anyList());
    }

    @Test
    public void adicionarPermissaoIndicacaoPremium_naoDeveAdicionarPermissaoIndicacaoPremium_quandoUsuarioNaoForNivelOperacao() {
        assertThatCode(() -> subCanalService
            .adicionarPermissaoIndicacaoPremium(umUsuarioMsoConsultor(3, PAP_PREMIUM)))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).getPermissoesEspeciaisDoUsuario(anyInt(), anyInt(), anyList());
        verify(usuarioService, never()).salvarPermissoesEspeciais(anyList());
    }

    @Test
    public void removerPermissaoIndicacaoPremium_deveRemoverPermissaoIndicacaoPremium_quandoUsuarioJaCadastradoENivelOperacao() {
        assertThatCode(() -> subCanalService
            .removerPermissaoIndicacaoPremium(umUsuarioOperacaoComSubCanal(101112, 2, PAP_PME)))
            .doesNotThrowAnyException();

        verify(usuarioService).removerPermissoesEspeciais(FUNC_CONSULTAR_INDICACAO_PREMIUM, List.of(101112));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void removerPermissaoIndicacaoPremium_naoDeveRemoverPermissaoIndicacaoPremium_quandoUsuarioJaCadastradoENaoNivelOperacao() {
        assertThatCode(() -> subCanalService
            .removerPermissaoIndicacaoPremium(umUsuarioMsoConsultor(3, PAP_PREMIUM)))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).removerPermissoesEspeciais(anyList(), anyList());
    }

    @Test
    public void removerPermissaoIndicacaoPremium_naoDeveRemoverPermissaoIndicacaoPremium_quandoUsuarioNovoCadastro() {
        assertThatCode(() -> subCanalService
            .removerPermissaoIndicacaoPremium(umUsuarioOperacaoComSubCanal(null, 2, PAP_PME)))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).removerPermissoesEspeciais(anyList(), anyList());
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoIndicacaoInsideSalesPme_deveAdicionarPermissaoIndicacaoInsideSalesPme_quandoUsuarioNivelOperacaoComSubCanalInsideSalesPme() {
        doReturn(List.of(umaPermissaoIndicacaoInsideSalesPme()))
            .when(usuarioService)
            .getPermissoesEspeciaisDoUsuario(101112, 23, FUNC_CONSULTAR_INDICACAO_INSIDE_SALES_PME);

        assertThatCode(() -> subCanalService
            .adicionarPermissaoIndicacaoInsideSalesPme(umUsuarioOperacaoComSubCanal(101112, 4, INSIDE_SALES_PME)))
            .doesNotThrowAnyException();

        verify(usuarioService).getPermissoesEspeciaisDoUsuario(101112, 23, FUNC_CONSULTAR_INDICACAO_INSIDE_SALES_PME);
        verify(usuarioService).salvarPermissoesEspeciais(List.of(umaPermissaoIndicacaoInsideSalesPme()));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoIndicacaoInsideSalesPme_naoDeveAdicionarPermissaoIndicacaoInsideSalesPme_quandoUsuarioNivelOperacaoSemSubCanalInsideSalesPme() {
        assertThatCode(() -> subCanalService
            .adicionarPermissaoIndicacaoInsideSalesPme(umUsuarioOperacaoComSubCanal(101112, 2, PAP_PME)))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).getPermissoesEspeciaisDoUsuario(anyInt(), anyInt(), anyList());
        verify(usuarioService, never()).salvarPermissoesEspeciais(anyList());
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoIndicacaoInsideSalesPme_naoDeveAdicionarPermissaoIndicacaoInsideSalesPme_quandoUsuarioNaoForNivelOperacao() {
        assertThatCode(() -> subCanalService
            .adicionarPermissaoIndicacaoInsideSalesPme(umUsuarioMsoConsultor(4, INSIDE_SALES_PME)))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).getPermissoesEspeciaisDoUsuario(anyInt(), anyInt(), anyList());
        verify(usuarioService, never()).salvarPermissoesEspeciais(anyList());
    }

    @Test
    @SuppressWarnings("LineLength")
    public void removerPermissaoIndicacaoInsideSalesPme_deveRemoverPermissaoIndicacaoInsideSalesPme_quandoUsuarioJaCadastradoENivelOperacao() {
        assertThatCode(() -> subCanalService
            .removerPermissaoIndicacaoInsideSalesPme(umUsuarioOperacaoComSubCanal(101112, 2, PAP_PME)))
            .doesNotThrowAnyException();

        verify(usuarioService).removerPermissoesEspeciais(FUNC_CONSULTAR_INDICACAO_INSIDE_SALES_PME, List.of(101112));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void removerPermissaoIndicacaoInsideSalesPme_naoDeveRemoverPermissaoIndicacaoInsideSalesPme_quandoUsuarioJaCadastradoENaoNivelOperacao() {
        assertThatCode(() -> subCanalService
            .removerPermissaoIndicacaoInsideSalesPme(umUsuarioMsoConsultor(4, INSIDE_SALES_PME)))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).removerPermissoesEspeciais(anyList(), anyList());
    }

    @Test
    @SuppressWarnings("LineLength")
    public void removerPermissaoIndicacaoInsideSalesPme_naoDeveRemoverPermissaoIndicacaoInsideSalesPme_quandoUsuarioNovoCadastro() {
        assertThatCode(() -> subCanalService
            .removerPermissaoIndicacaoInsideSalesPme(umUsuarioOperacaoComSubCanal(null, 2, PAP_PME)))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).removerPermissoesEspeciais(anyList(), anyList());
    }

    @Test
    public void getAllConfiguracoes_deveRetornarPageDeSubCanalDto_quandoTudoOk() {
        var pageRequest = new PageRequest();
        var filtros = new SubCanalFiltros();
        when(subCanalRepository
            .findAll(filtros.toPredicate().build(), pageRequest))
            .thenReturn(new PageImpl<>(List.of(
                new SubCanal(1, ETipoCanal.PAP, "PAP", ESituacao.A, Eboolean.V),
                new SubCanal(2, ETipoCanal.PAP_PME, "PAP PME", ESituacao.A, Eboolean.F))));

        assertThat(subCanalService.getAllConfiguracoes(pageRequest, filtros))
            .hasSize(2)
            .extracting("id", "codigo", "nome", "situacao", "novaChecagemCredito")
            .containsExactlyInAnyOrder(
                tuple(1, ETipoCanal.PAP, "PAP", ESituacao.A, Eboolean.V),
                tuple(2, ETipoCanal.PAP_PME, "PAP PME", ESituacao.A, Eboolean.F)
            );

        verify(subCanalRepository).findAll(filtros.toPredicate().build(), pageRequest);
    }

    @Test
    public void editar_deveEditarSubCanal_quandoTudoOk() {
        var dto = new SubCanalDto(1, PAP_PREMIUM, "PAP", ESituacao.I, Eboolean.F);
        var suCanal = new SubCanal(1, ETipoCanal.PAP, "PAP", ESituacao.A, Eboolean.V);

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAdmin());
        when(subCanalRepository.findById(1)).thenReturn(Optional.of(suCanal));

        subCanalService.editar(dto);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(subCanalRepository).findById(1);
        verify(subCanalRepository).save(subCanalArgumentCaptor.capture());
        assertThat(subCanalArgumentCaptor.getValue())
            .extracting("id", "codigo", "nome", "situacao", "novaChecagemCredito")
            .containsExactly(1, PAP_PREMIUM, "PAP", ESituacao.I, Eboolean.F);
    }

    @Test
    public void editar_deveLancarException_quandoSubCanalNaoEncontrado() {
        var dto = new SubCanalDto(1, PAP_PREMIUM, "PAP", ESituacao.I, Eboolean.F);

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAdmin());
        when(subCanalRepository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> subCanalService.editar(dto))
            .withMessage("Erro, subcanal não encontrado.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(subCanalRepository).findById(1);
        verify(subCanalRepository, never()).save(any(SubCanal.class));
    }

    @Test
    public void editar_deveLancarPermissaoException_quandoUsuarioNaoForAmdin() {
        var user = umUsuarioAutenticadoAdmin();
        user.setNivelCodigo(OPERACAO.name());
        var dto = new SubCanalDto(1, PAP_PREMIUM, "PAP", ESituacao.I, Eboolean.F);

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(user);

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> subCanalService.editar(dto))
            .withMessage("O usuário logado não possuí permissão para acessar essa funcionalidade.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(subCanalRepository, never()).findById(anyInt());
        verify(subCanalRepository, never()).save(any(SubCanal.class));
    }

    @Test
    public void isNovaChecagemCredito_deveRetornarEBoolean_quandoTudoOk() {
        when(subCanalRepository.findById(1)).thenReturn(Optional.of(umSubCanal()));
        assertEquals(subCanalService.isNovaChecagemCredito(1), Eboolean.F);
        verify(subCanalRepository).findById(eq(1));
    }

    @Test
    public void isNovaChecagemCredito_deveNotFoundException_quandoSubCanalNaoEncontrado() {
        when(subCanalRepository.findById(1)).thenReturn(Optional.empty());
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> subCanalService.isNovaChecagemCredito(1))
                .withMessage("Erro, subcanal não encontrado.");

        verify(subCanalRepository).findById(eq(1));
    }
}
