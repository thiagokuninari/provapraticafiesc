package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import org.hibernate.LazyInitializationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.ADMINISTRADOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.CLIENTE_LOJA_FUTURO;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql({"classpath:/tests_usuario_repository.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UsuarioLazyRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    public void findById_deveCausarLazyLoading_quandoBuscarCargoDoUsuario() {
        var usuario = repository.findById(100).get();

        assertThatExceptionOfType(LazyInitializationException.class)
            .isThrownBy(() -> usuario.isCargo(CLIENTE_LOJA_FUTURO));
    }

    @Test
    public void findComplete_naoDeveCausarLazyLoading_quandoBuscarCargoDoUsuario() {
        var usuario = repository.findComplete(100).get();

        assertThat(usuario.getCargo())
            .extracting(
                Cargo::getId,
                Cargo::getCodigo)
            .containsExactlyInAnyOrder(
                50,
                ADMINISTRADOR);

        assertThatCode(() -> usuario.isCargo(CLIENTE_LOJA_FUTURO))
            .doesNotThrowAnyException();
    }
}
