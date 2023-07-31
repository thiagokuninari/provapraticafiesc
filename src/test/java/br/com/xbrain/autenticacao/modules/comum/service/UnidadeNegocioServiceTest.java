package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.predicate.UnidadeNegocioPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.ADMINISTRADOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.VENDEDOR_OPERACAO;
import static helpers.TestBuilders.umUsuarioAutenticado;
import static helpers.UnidadesNegocio.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UnidadeNegocioServiceTest {

    @InjectMocks
    private UnidadeNegocioService unidadeNegocioService;
    @Mock
    private UnidadeNegocioRepository unidadeNegocioRepository;
    @Mock
    private AutenticacaoService autenticacaoService;

    @Test
    public void getAll_deveRetornarTodasUnidadesNegocio_quandoForXbrain() {
        var usuarioAutenticadoXbrain = umUsuarioAutenticado(100, ADMINISTRADOR, "XBRAIN");

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticadoXbrain);

        when(unidadeNegocioRepository.findAll(getBooleanBuilder(usuarioAutenticadoXbrain)))
            .thenReturn(List.of(UNIDADE_PESSOAL, UNIDADE_RESIDENCIAL_E_COMBOS, UNIDADE_XBRAIN));

        assertThat(unidadeNegocioService.getAll())
            .hasSize(3)
            .extracting("nome")
            .containsExactly("Pessoal", "Residencial e Combos", "Xbrain");
    }

    @Test
    public void getAll_deveIgnorarXbrain_quandoNaoForXbrain() {
        var usuarioAutenticadoOperacao = umUsuarioAutenticado(200, VENDEDOR_OPERACAO, "OPERACAO");

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticadoOperacao);

        when(unidadeNegocioRepository.findAll(getBooleanBuilder(usuarioAutenticadoOperacao)))
            .thenReturn(List.of(UNIDADE_PESSOAL, UNIDADE_RESIDENCIAL_E_COMBOS));

        assertThat(unidadeNegocioService.getAll())
            .hasSize(2)
            .extracting("nome")
            .containsExactly("Pessoal", "Residencial e Combos");
    }

    private BooleanBuilder getBooleanBuilder(UsuarioAutenticado usuarioAutenticado) {
        return new UnidadeNegocioPredicate()
            .exibeXbrainSomenteParaXbrain(usuarioAutenticado.isXbrain())
            .build();
    }

}
