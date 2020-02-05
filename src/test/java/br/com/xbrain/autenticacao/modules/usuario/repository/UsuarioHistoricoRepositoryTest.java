package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql("classpath:/leads/tests_usuario_historico_leads.sql")
public class UsuarioHistoricoRepositoryTest {

    @Autowired
    private UsuarioHistoricoRepository repository;

    @Test
    public void inativarUsuarioHistoricoGeradorLead_deveInativarHistoricoDoUsuarioByUsuariosIds() {
        assertThatCode(() -> repository.inativarUsuarioHistoricoGeradorLead(List.of(100, 101)))
            .doesNotThrowAnyException();
        assertEquals(2, repository.findBySituacao(ESituacao.A).size());
    }
}