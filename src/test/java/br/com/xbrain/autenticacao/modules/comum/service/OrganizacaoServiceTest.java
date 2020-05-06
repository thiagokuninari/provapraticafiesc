package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.repository.OrganizacaoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.helper.OrganizacaoHelper.umaOrganizacao;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganizacaoServiceTest {

    @InjectMocks
    private OrganizacaoService organizacaoService;

    @Mock
    private OrganizacaoRepository organizacaoRepository;

    @Test
    public void findAll_todasOrganizacoes_quandoPesquisar() {
        when(organizacaoRepository.findAll())
            .thenReturn(List.of(
                umaOrganizacao(1, "BCC"),
                umaOrganizacao(2, "CALLINK")));

        assertThat(organizacaoService.getAllSelect(null))
            .hasSize(2)
            .extracting("id", "codigo", "nome")
            .contains(tuple(1, "BCC", "BCC"),
                tuple(2, "CALLINK", "CALLINK"));
    }

    @Test
    public void findAll_organizacoesFiltradas_quandoParametroNivelId() {
        when(organizacaoRepository.findAllByNiveisIdIn(any()))
            .thenReturn(List.of(
                umaOrganizacao(8, "CSU"),
                umaOrganizacao(9, "MOTIVA")));

        assertThat(organizacaoService.getAllSelect(15))
            .hasSize(2)
            .extracting("id", "codigo", "nome")
            .contains(tuple(8, "CSU", "CSU"),
                tuple(9, "MOTIVA", "MOTIVA"));
    }

    @Test
    public void getById_organizacao_quandoExistir() {
        when(organizacaoRepository.findById(any())).thenReturn(Optional.of(umaOrganizacao(1, "BCC")));

        var response = organizacaoService.getById(1);
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getNome()).isEqualTo("BCC");
        assertThat(response.getCodigo()).isEqualTo("BCC");
    }

    @Test
    public void getById_validacaoException_quandoNaoExistir() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> organizacaoService.getById(1))
            .withMessage("Organização não encontrada.");
    }
}
