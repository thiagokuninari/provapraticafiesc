package br.com.xbrain.autenticacao.modules.usuario.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql("classpath:/usuario-cidade-repository-test.sql")
public class UsuarioCidadeRepositoryTest {

    @Autowired
    private UsuarioCidadeRepository repository;

    @Test
    public void findCidadesIdByUsuarioId_deveRetornarOsIdsDasCidades_quandoEncontrarCidadesPorUsuarioId() {
        assertThat(repository.findCidadesIdByUsuarioId(100))
            .hasSize(6)
            .containsExactlyInAnyOrder(3237, 2466, 1443, 3022, 2617, 5189);
    }

    @Test
    public void findCidadesIdByUsuarioId_deveRetornarUmaListaVazia_quandoNaoEncontrarCidadesPorUsuarioId() {
        assertThat(repository.findCidadesIdByUsuarioId(999))
            .isEmpty();
    }

    @Test
    public void findCidadesIdByUsuarioIdComDataBaixaNull_deveRetornarListInteger_quandoEncontrar() {
        assertThat(repository.findCidadesIdByUsuarioIdComDataBaixaNull(100))
            .hasSize(5)
            .containsExactlyInAnyOrder(3237, 2466, 1443, 3022, 2617);
    }

    @Test
    public void findCidadesIdByUsuarioIdComDataBaixaNull_deveRetornarListaVazia_quandoNaoEncontrar() {
        assertThat(repository.findCidadesIdByUsuarioIdComDataBaixaNull(999))
            .isEmpty();
    }

    @Test
    public void findUsuarioCidadesByUsuarioId_deveRetornarasCidades_quandoEncontrarCidadesPorUsuarioId() {
        assertThat(repository.findUsuarioCidadesByUsuarioId(100))
            .extracting("cidade.uf.id", "cidade.uf.nome", "cidade.id", "cidade.nome")
            .hasSize(6)
            .containsExactlyInAnyOrder(
                tuple(1, "PARANA", 3237, "ARAPONGAS"),
                tuple(8, "MINAS GERAIS", 1443, "BELO VALE"),
                tuple(4, "PARA", 2466, "BELTERRA"),
                tuple(12, "PIAUI", 3022, "BENEDITINOS"),
                tuple(24, "PARAIBA", 2617, "BERNARDINO BATISTA"),
                tuple(2, "SAO PAULO", 5189, "OSASCO"));
    }

    @Test
    public void findUsuarioCidadesByUsuarioId_deveRetornarUmaListaVazia_quandoNaoEncontrarCidadesPorUsuarioId() {
        assertThat(repository.findUsuarioCidadesByUsuarioId(999))
            .isEmpty();
    }
}
