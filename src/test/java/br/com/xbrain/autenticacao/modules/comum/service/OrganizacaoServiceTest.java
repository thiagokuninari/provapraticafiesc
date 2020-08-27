package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.filtros.OrganizacaoFiltros;
import br.com.xbrain.autenticacao.modules.comum.repository.OrganizacaoRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.helper.OrganizacaoHelper.umaOrganizacao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganizacaoServiceTest {

    @InjectMocks
    private OrganizacaoService organizacaoService;

    @Mock
    private OrganizacaoRepository organizacaoRepository;

    @Mock
    private AutenticacaoService autenticacaoService;

    @Before
    public void setup() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(UsuarioAutenticado.builder().build());
    }

    @Test
    public void findAll_todasOrganizacoes_quandoPesquisar() {
        when(organizacaoRepository.findByPredicate(any()))
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
        var filtros = OrganizacaoFiltros.builder().nivelId(15).build();
        when(organizacaoRepository.findByPredicate(eq(filtros.toPredicate())))
            .thenReturn(List.of(
                umaOrganizacao(8, "CSU"),
                umaOrganizacao(9, "MOTIVA")));

        assertThat(organizacaoService.getAllSelect(filtros))
            .hasSize(2)
            .extracting("id", "codigo", "nome")
            .contains(tuple(8, "CSU", "CSU"),
                tuple(9, "MOTIVA", "MOTIVA"));
    }

    @Test
    public void findAll_organizacoesFiltradas_quandoUsuarioBackoffice() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioBackoffice());
        var filtros = OrganizacaoFiltros.builder().organizacaoId(1).build();

        when(organizacaoRepository.findByPredicate(eq(filtros.toPredicate())))
            .thenReturn(List.of(umaOrganizacao(1, "MOTIVA")));

        assertThat(organizacaoService.getAllSelect(null))
            .hasSize(1)
            .extracting("id", "codigo", "nome")
            .contains(tuple(1, "MOTIVA", "MOTIVA"));
    }

    @Test
    public void findAll_organizacoesFiltradas_quandoParametroCodigoNivel() {
        var filtros = OrganizacaoFiltros.builder().codigoNivel(CodigoNivel.BACKOFFICE).build();
        when(organizacaoRepository.findByPredicate(eq(filtros.toPredicate())))
            .thenReturn(List.of(
                umaOrganizacao(8, "CSU"),
                umaOrganizacao(9, "MOTIVA")));

        assertThat(organizacaoService.getAllSelect(filtros))
            .hasSize(2)
            .extracting("id", "codigo", "nome")
            .contains(tuple(8, "CSU", "CSU"),
                tuple(9, "MOTIVA", "MOTIVA"));
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
            .organizacaoCodigo("MOTIVA")
            .build();
    }
}
