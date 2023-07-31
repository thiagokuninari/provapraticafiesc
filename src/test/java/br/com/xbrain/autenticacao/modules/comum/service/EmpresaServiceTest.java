package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.filtros.EmpresaPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.ADMINISTRADOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.VENDEDOR_OPERACAO;
import static helpers.Empresas.*;
import static helpers.TestBuilders.umUsuarioAutenticado;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;

@RunWith(MockitoJUnitRunner.class)
public class EmpresaServiceTest {

    @InjectMocks
    private EmpresaService empresaService;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private AutenticacaoService autenticacaoService;

    @Test
    public void getAll_deveRetornarTodasEmpresas_quandoForXbrain() {
        var usuarioAutenticadoXbrain = umUsuarioAutenticado(100, ADMINISTRADOR, "XBRAIN");

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticadoXbrain);

        when(empresaRepository.findAll(getBooleanBuilder(null, usuarioAutenticadoXbrain), new Sort(ASC, "nome")))
            .thenReturn(List.of(CLARO_MOVEL, CLARO_TV, NET, XBRAIN));

        assertThat(empresaService.getAll(null))
            .hasSize(4)
            .extracting("nome")
            .containsExactly("Claro Móvel", "Claro TV", "NET", "XBRAIN");
    }

    @Test
    public void getAll_deveIgnorarXbrain_quandoNaoForXbrain() {
        var usuarioAutenticadoOperacao = umUsuarioAutenticado(200, VENDEDOR_OPERACAO, "OPERACAO");

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticadoOperacao);

        when(empresaRepository.findAll(getBooleanBuilder(null, usuarioAutenticadoOperacao), new Sort(ASC, "nome")))
            .thenReturn(List.of(CLARO_MOVEL, CLARO_TV, NET));

        assertThat(empresaService.getAll(null))
            .hasSize(3)
            .extracting("nome")
            .containsExactly("Claro Móvel", "Claro TV", "NET");
    }

    @Test
    public void getAll_deveFiltrarPorUnidadeDeNegocio_quandoPassarParametro() {
        var usuarioAutenticadoOperacao = umUsuarioAutenticado(200, VENDEDOR_OPERACAO, "OPERACAO");

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticadoOperacao);

        when(empresaRepository.findAll(
            getBooleanBuilder(1, usuarioAutenticadoOperacao),
            new Sort(ASC, "nome")
        )).thenReturn(List.of(CLARO_MOVEL));

        assertThat(empresaService.getAll(1))
            .hasSize(1)
            .extracting("nome")
            .containsExactly("Claro Móvel");
    }

    private BooleanBuilder getBooleanBuilder(Integer unidadeNegocioId, UsuarioAutenticado usuarioAutenticado) {
        return new EmpresaPredicate()
            .daUnidadeDeNegocio(unidadeNegocioId)
            .exibeXbrainSomenteParaXbrain(usuarioAutenticado.isXbrain())
            .build();
    }
}
