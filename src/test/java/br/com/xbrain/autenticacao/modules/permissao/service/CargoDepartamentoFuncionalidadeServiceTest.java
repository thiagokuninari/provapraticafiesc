package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import helpers.DepartamentoHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class CargoDepartamentoFuncionalidadeServiceTest {

    @Autowired
    private CargoDepartamentoFuncionalidadeService service;
    @MockBean
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
