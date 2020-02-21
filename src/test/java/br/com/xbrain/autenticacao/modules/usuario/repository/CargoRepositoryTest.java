package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class CargoRepositoryTest {

    private static final int NIVEL_ID_RECEPTIVO = 8;
    @Autowired
    private CargoRepository repository;

    @Test
    public void findFirstByNomeIgnoreCaseAndNivelId_deveEncontrarCargoVendedorReceptivo_quandoInformarNivelIdValido() {
        assertThat(
            repository.findFirstByNomeIgnoreCaseAndNivelId("veNdEdor receptivo", NIVEL_ID_RECEPTIVO))
            .isPresent()
            .containsInstanceOf(Cargo.class);
    }

    @Test
    public void findFirstByNomeIgnoreCaseAndNivelId_deveRetornarOptionalEmpty_quandoInformarNivelIdInvalido() {
        assertThat(
            repository.findFirstByNomeIgnoreCaseAndNivelId("veNdEdor receptivo", 9999999))
            .isNotPresent();
    }
}
