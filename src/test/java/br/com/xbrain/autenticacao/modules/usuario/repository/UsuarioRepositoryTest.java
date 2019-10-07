package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_usuario_repository.sql"})
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    public void getSubclustersUsuario_deveRetornarOsSubclusters_somenteAtivosSemDuplicar() {

        assertThat(repository.getSubclustersUsuario(100))
                .extracting("id", "nome")
                .containsExactly(
                        tuple(26600, "CHAPECÓ"),
                        tuple(189, "LONDRINA"));

        assertThat(repository.getSubclustersUsuario(101))
                .extracting("id", "nome")
                .containsExactly(
                        tuple(164, "BRI - LINS - SP"));
    }

    @Test
    public void findAllUsuariosSemDataUltimoAcesso_deveRetornarUsuario_quandoNaoPossuirDataUltimoAcessoAndEstiverAtivo() {
        assertThat(repository.findAllUsuariosSemDataUltimoAcesso())
                .hasSize(2)
                .extracting("id", "email")
                .containsExactly(
                        tuple(103, "CARLOS@HOTMAIL.COM"),
                        tuple(104, "MARIA@HOTMAIL.COM"));
    }

    @Test
    public void findAllExecutivosDosIdsCoordenador_deveRetornarExecutivosEspecificos_quandoUsuarioForCoordenador() {
        assertThat(repository.findAllExecutivosDosIdsCoordenador(List.of(107, 108, 109), new Usuario(109)))
            .hasSize(2)
            .extracting("value", "text")
            .containsExactly(
                tuple(107, "EXECUTIVO 1"),
                tuple(108, "EXECUTIVO 2"));
    }

    @Test
    public void findAllExecutivosDosIdsCoordenador_deveRetornarListaVazia_quandoExecutivoNaoPertencerAoCoordenador() {
        assertThat(repository.findAllExecutivosDosIdsCoordenador(List.of(100, 101, 102), new Usuario(109)))
            .hasSize(0)
            .isEmpty();
    }
}
