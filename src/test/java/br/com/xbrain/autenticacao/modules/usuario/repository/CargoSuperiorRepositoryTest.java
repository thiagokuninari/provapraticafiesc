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

    private static final int GERENTE_OPERACAO_ID = 7;
    private static final int VENDEDOR_OPERACAO_ID = 8;

    @Autowired
    private CargoSuperiorRepository repository;

    @Test
    public void getCargosHierarquia_deveRetornarAHierarquiaDeCargos_quandoExistir() {

        assertThat(repository.getCargosHierarquia(GERENTE_OPERACAO_ID))
                .containsExactlyInAnyOrder(1, 4, 5, 3, 10);

        assertThat(repository.getCargosHierarquia(VENDEDOR_OPERACAO_ID))
                .isEmpty();

    }
}
