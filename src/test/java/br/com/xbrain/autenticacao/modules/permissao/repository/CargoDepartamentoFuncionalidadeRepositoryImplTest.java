package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_departamento.sql"})
public class CargoDepartamentoFuncionalidadeRepositoryImplTest {

    @Autowired
    private CargoDepartamentoFuncionalidadeRepositoryImpl repository;

    @Test
    public void findAllDepartamentos_deveRetornarListaDepatamento_quandoEncontrarDepartamentosVinculadosAoCargo() {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();
        filtros.setCargoId(200);

        var nivel = new Nivel(100);

        assertThat(repository.findAllDepartamentos(filtros.toPredicate()))
            .extracting("id", "codigo", "nome", "situacao", "nivel")
            .containsExactlyInAnyOrder(
                tuple(500, CodigoDepartamento.COMERCIAL, "Comercial", ESituacao.A, nivel));
    }

    @Test
    public void findAllDepartamentos_deveRetornarEmpty_quandoNaoHouverDepartamentosVinculadosAoCargo() {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();
        filtros.setCargoId(9999);

        assertThat(
            repository.findAllDepartamentos(filtros.toPredicate()))
            .isEmpty();
    }
}
