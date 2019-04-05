package br.com.xbrain.autenticacao.modules.usuario.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("oracle-test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class CargoSuperiorRepositoryTest {

    @Autowired
    private CargoSuperiorRepository repository;

    @Test
    public void fgetCargosHierarquia_deveRetornarAHierarquiaDeCargos_quandoExistir() {

        assertThat(repository.getCargosHierarquia(7))
                .containsExactly(4, 5, 10);

        assertThat(repository.getCargosHierarquia(10))
                .isEmpty();
    }
}
