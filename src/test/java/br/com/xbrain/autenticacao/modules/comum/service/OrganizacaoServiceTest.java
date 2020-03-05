package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.repository.OrganizacaoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.helper.OrganizacaoHelper.umaOrganizacao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganizacaoServiceTest {

    @InjectMocks
    private OrganizacaoService organizacaoService = new OrganizacaoService();

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
        when(organizacaoRepository.findAll(any())).thenReturn(List.of());
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
}
