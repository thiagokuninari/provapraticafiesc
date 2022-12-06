package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_solicitacao_ramal_database.sql"})
public class SolicitacaoRamalRepositoryTest {

    @Autowired
    private SolicitacaoRamalRepository repository;

    @Test
    public void findAllByAaId_deveRetornarlistaVazia_quandoNaoHouverSolicitacaoComStatusPendenteOuEmAndamento() {
        assertEquals(0, repository.findAllByAgenteAutorizadoIdAndSituacaoPendenteOuEmAndamento(3).size());
    }

    @Test
    public void findAllByAaId_deveRetornarlistaComDoisRegistros_quandoHouverSolicitacaoComStatusPendenteOuEmAndamento() {
        assertEquals(2, repository.findAllByAgenteAutorizadoIdAndSituacaoPendenteOuEmAndamento(1).size());
    }

    @Test
    public void findAllBySubCanalId_deveRetornarlistaVazia_quandoNaoHouverSolicitacaoComStatusPendenteOuEmAndamento() {
        assertEquals(0, repository.findAllBySubCanalIdAndSituacaoPendenteOuEmAndamento(3).size());
    }

    @Test
    public void findAllBySubCanalId_deveRetornarlistaComDoisRegistros_quandoHouverSolicitacaoComStatusPendenteOuEmAndamento() {
        assertEquals(2, repository.findAllBySubCanalIdAndSituacaoPendenteOuEmAndamento(1).size());
    }

}
