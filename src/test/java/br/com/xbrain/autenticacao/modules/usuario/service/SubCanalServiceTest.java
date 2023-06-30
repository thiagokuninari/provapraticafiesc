package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubCanalRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.PAP;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.PAP_PREMIUM;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioMsoConsultor;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioOperacaoComSubCanal;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SubCanalServiceTest {

    @Autowired
    private SubCanalService service;
    @MockBean
    private SubCanalRepository repository;
    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void getAll_deveRetornarTodosSubCanais_quandoSolicitado() {
        when(repository.findAll()).thenReturn(List.of(umSubCanal()));

        assertThat(service.getAll())
            .hasSize(1)
            .containsExactly(new SubCanalDto(1, PAP, "PAP", ESituacao.A));
    }

    @Test
    public void getSubCanalById_deveRetornarSubCanalResponse_quandoHouverSubCanal() {
        when(repository.findById(anyInt())).thenReturn(Optional.of(umSubCanal()));

        assertThat(service.getSubCanalById(1))
            .isEqualTo(new SubCanalDto(1, PAP, "PAP", ESituacao.A));
    }

    @Test
    public void getSubCanalById_deveLancarException_quandoNaoHouverSubCanal() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getSubCanalById(100))
            .withMessage("Erro, subcanal não encontrado.");
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoIndicacaoPremium_deveAdicionarPermissaoIndicacaoPremium_quandoUsuarioNivelOperacaoComSubCanalPapPremium() {
        var permissaoPapPremium = PermissaoEspecial.builder()
            .id(3062)
            .build();

        var usuario = umUsuarioOperacaoComSubCanal(Set.of(
            SubCanal.builder()
                .id(3)
                .codigo(PAP_PREMIUM)
                .build()));

        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(101112), eq(23), eq(List.of(3062))))
            .thenReturn(List.of(permissaoPapPremium));

        assertThatCode(() -> service.adicionarPermissaoIndicacaoPremium(usuario))
            .doesNotThrowAnyException();

        verify(usuarioService, times(1))
            .getPermissoesEspeciaisDoUsuario(eq(101112), eq(23), eq(List.of(3062)));
        verify(usuarioService, times(1))
            .salvarPermissoesEspeciais(eq(List.of(permissaoPapPremium)));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoIndicacaoPremium_naoDeveAdicionarPermissaoIndicacaoPremium_quandoUsuarioNivelOperacaoSemSubCanalPapPremium() {
        var usuario = umUsuarioOperacaoComSubCanal(Set.of(
            SubCanal.builder()
                .id(2)
                .codigo(PAP)
                .build()));

        assertThatCode(() -> service.adicionarPermissaoIndicacaoPremium(usuario))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).getPermissoesEspeciaisDoUsuario(anyInt(), anyInt(), anyList());
        verify(usuarioService, never()).salvarPermissoesEspeciais(anyList());
    }

    @Test
    public void adicionarPermissaoIndicacaoPremium_naoDeveAdicionarPermissaoIndicacaoPremium_quandoUsuarioNaoForNivelOperacao() {
        var usuario = umUsuarioMsoConsultor(Set.of(
            SubCanal.builder()
                .id(3)
                .codigo(PAP_PREMIUM)
                .build()));

        assertThatCode(() -> service.adicionarPermissaoIndicacaoPremium(usuario))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).getPermissoesEspeciaisDoUsuario(anyInt(), anyInt(), anyList());
        verify(usuarioService, never()).salvarPermissoesEspeciais(anyList());
    }

    @Test
    public void removerPermissaoIndicacaoPremium_deveRemoverPermissaoIndicacaoPremium_quandoUsuarioJaCadastradoENivelOperacao() {
        var usuario = umUsuarioOperacaoComSubCanal(Set.of(
            SubCanal.builder()
                .id(2)
                .codigo(PAP)
                .build()));

        assertThatCode(() -> service.removerPermissaoIndicacaoPremium(usuario))
            .doesNotThrowAnyException();

        verify(usuarioService, times(1)).removerPermissoesEspeciais(List.of(3062), List.of(usuario.getId()));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void removerPermissaoIndicacaoPremium_naoDeveRemoverPermissaoIndicacaoPremium_quandoUsuarioJaCadastradoENaoNivelOperacao() {
        var usuario = umUsuarioMsoConsultor(Set.of(
            SubCanal.builder()
                .id(3)
                .codigo(PAP_PREMIUM)
                .build()));

        assertThatCode(() -> service.removerPermissaoIndicacaoPremium(usuario))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).removerPermissoesEspeciais(anyList(), anyList());
    }

    @Test
    public void removerPermissaoIndicacaoPremium_naoDeveRemoverPermissaoIndicacaoPremium_quandoUsuarioNovoCadastro() {
        var usuario = umUsuarioMsoConsultor(Set.of(
            SubCanal.builder()
                .id(3)
                .codigo(PAP_PREMIUM)
                .build()));

        usuario.setId(null);

        assertThatCode(() -> service.removerPermissaoIndicacaoPremium(usuario))
            .doesNotThrowAnyException();

        verify(usuarioService, never()).removerPermissoesEspeciais(anyList(), anyList());
    }
}
