package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql(scripts = {"classpath:/tests_departamento.sql", "classpath:/tests_niveis.sql"})
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

    @Test
    public void getNiveisConfiguracoesTratativas_deveRetornarListaNiveis_quandoChamado() {
        var funcionalideAbrirTratativasVendas = 1001;
        var funcionalidadeAbrirTratativasBko = 1002;

        assertThat(repository.getNiveisByFuncionalidades(
            List.of(funcionalideAbrirTratativasVendas, funcionalidadeAbrirTratativasBko)))
            .extracting("id", "nome", "codigo")
            .containsExactly(
                tuple(101, "Lojas", CodigoNivel.LOJAS),
                tuple(102, "Receptivo", CodigoNivel.RECEPTIVO),
                tuple(103, "Backoffice Centralizado", CodigoNivel.BACKOFFICE_CENTRALIZADO));
    }
}
