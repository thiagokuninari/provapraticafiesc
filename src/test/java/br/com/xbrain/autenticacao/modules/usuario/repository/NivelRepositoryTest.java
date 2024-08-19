package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
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
@Sql({"classpath:/tests_niveis.sql"})
public class NivelRepositoryTest {

    @Autowired
    private NivelRepository repository;

    @Test
    public void getNiveisConfiguracoesTratativas_deveRetornarListaNiveis_quandoChamado() {
        var funcionalideAbrirTratativasVendas = 1001;
        var funcionalidadeAbrirTratativasBko = 1002;

        assertThat(repository.getNiveisConfiguracoesTratativas(
            List.of(funcionalideAbrirTratativasVendas, funcionalidadeAbrirTratativasBko)))
            .extracting("id", "nome", "codigo")
            .containsExactly(
                tuple(101, "Lojas", CodigoNivel.LOJAS),
                tuple(102, "Receptivo", CodigoNivel.RECEPTIVO),
                tuple(103, "Backoffice Centralizado", CodigoNivel.BACKOFFICE_CENTRALIZADO));
    }
}
