package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql("classpath:/tests_usuario_repository.sql")
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
            .hasSize(4)
            .extracting("id", "email")
            .containsExactly(
                tuple(103, "CARLOS@HOTMAIL.COM"),
                tuple(104, "MARIA@HOTMAIL.COM"),
                tuple(110, "EXECUTIVOHUNTER1@TESTE.COM"),
                tuple(111, "EXECUTIVOHUNTER2@TESTE.COM"));
    }

    @Test
    public void findAllExecutivosDosIdsCoordenador_deveRetornarExecutivosEspecificos_quandoUsuarioForCoordenador() {
        assertThat(repository.findAllExecutivosDosIdsCoordenadorGerente(List.of(107, 108, 109), 109))
            .hasSize(2)
            .extracting("value", "text")
            .containsExactly(
                tuple(107, "EXECUTIVO 1"),
                tuple(108, "EXECUTIVO 2"));
    }

    @Test
    public void findAllExecutivosDosIdsCoordenador_deveRetornarListaVazia_quandoExecutivoNaoPertencerAoCoordenador() {
        assertThat(repository.findAllExecutivosDosIdsCoordenadorGerente(List.of(100, 101, 102), 109))
            .hasSize(0)
            .isEmpty();
    }

    @Test
    public void findAllExecutivosBySituacao_deveRetornarExecutivosAtivos() {
        assertThat(repository.findAllExecutivosBySituacao(ESituacao.A))
            .hasSize(4)
            .extracting("id", "email")
            .containsExactly(
                tuple(107, "EXECUTIVO1@TESTE.COM"),
                tuple(108, "EXECUTIVO2@TESTE.COM"),
                tuple(110, "EXECUTIVOHUNTER1@TESTE.COM"),
                tuple(111, "EXECUTIVOHUNTER2@TESTE.COM"));
    }

    @Test
    public void findAllExecutivosBySituacao_deveRetornarExecutivosInativos() {
        assertThat(repository.findAllExecutivosBySituacao(ESituacao.I))
            .hasSize(1)
            .extracting("id", "email")
            .contains(tuple(112, "EXECUTIVOHUNTER3@TESTE.COM"));
    }

    @Test
    public void findUsuarioByIds_deveRetornarUsuarios_quandoForPassadoIdsDosUsuarios() {
        assertThat(repository.findUsuariosByIds(List.of(107, 108, 110, 111)))
            .extracting("id", "nome")
            .containsExactly(
                tuple(107, "EXECUTIVO 1"),
                tuple(108, "EXECUTIVO 2"),
                tuple(110, "HUNTER 1"),
                tuple(111, "HUNTER 2"));
    }

    @Test
    public void findAllAtivosByNivelOperacaoCanalAa_doisUsuarios_quandoAtivoECanalAgenteAutorizado() {
        assertThat(repository.findAllAtivosByNivelOperacaoCanalAa())
            .extracting("value", "label")
            .containsExactly(
                tuple(110, "HUNTER 1"),
                tuple(111, "HUNTER 2")
            );
    }
}
