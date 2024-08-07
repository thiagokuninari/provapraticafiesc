package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.EM_ANDAMENTO;
import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.PENDENTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_solicitacao_ramal_database.sql"})
public class SolicitacaoRamalHistoricoRepositoryTest {

    @Autowired
    SolicitacaoRamalHistoricoRepository repository;

    @Test
    public void findAllBySolicitacaoRamalId_deveRetornarListaSolicitacaoRamalHistorico_quandoEncontrado() {
        assertThat(repository.findAllBySolicitacaoRamalId(1))
            .extracting("id", "situacao", "comentario", "solicitacaoRamal.id", "usuario.id")
            .containsExactlyInAnyOrder(tuple(2, EM_ANDAMENTO, "NOVO COMENTARIO", 1, 106),
                tuple(1, PENDENTE, "SEM COMENTARIO", 1, 106));
    }

    @Test
    public void deleteAll_deveRemover_quandoEncontrado() {
        assertThat(repository.findAllBySolicitacaoRamalId(1).size()).isEqualTo(2);

        repository.deleteAll(1);

        assertThat(repository.findAllBySolicitacaoRamalId(1).size()).isEqualTo(0);
    }
}
