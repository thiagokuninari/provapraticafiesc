package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CargoDepartamentoFuncionalidadeServiceTest {

    @InjectMocks
    private CargoDepartamentoFuncionalidadeService service;
    @Mock
    private CargoDepartamentoFuncionalidadeRepository repository;

    @Test
    public void getPermitidosPorCargo_deveRetornarUmaListaDeDepartamentos_quandoExistirDepartamentosVinculadosAoCargo() {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();
        filtros.setCargoId(200);
        when(repository.findAllDepartamentos(filtros.toPredicate()))
            .thenReturn(DepartamentoHelper.umaListaDepartamentos());

        assertThat(service.getDepartamentoByCargo(200))
            .extracting("id", "nome", "codigo", "situacao")
            .containsExactly(tuple(1, "Departamento 1", CodigoDepartamento.COMERCIAL, ESituacao.A),
                tuple(2, "Departamento 2", CodigoDepartamento.COMERCIAL, ESituacao.A));
    }

    @Test
    public void getPermitidosPorCargo_deveRetornarVazio_quandoNaoExistirDepartamentosVinculadosAoCargoId() {
        when(service.getDepartamentoByCargo(201))
            .thenReturn(Collections.emptyList());

        assertThat(service.getDepartamentoByCargo(201))
            .isEmpty();
    }
}
