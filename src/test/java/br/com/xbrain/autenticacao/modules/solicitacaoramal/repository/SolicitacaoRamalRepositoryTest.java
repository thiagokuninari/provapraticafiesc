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
    public void findAllByAaId_listaVazia_quandoNaoHouverSolicitacaoPeloAaIdComStatusPendenteOuEmAndamento() {
        assertEquals(0, repository.findAllByAgenteAutorizadoIdAndSituacaoDiferentePendenteOuEmAndamento(3).size());
    }

    @Test
    public void findAllByAaId_listaComDoisRegistros_quandoHouverSolicitacaoPeloAaIdComStatusPendenteOuEmAndamento() {
        assertEquals(2, repository.findAllByAgenteAutorizadoIdAndSituacaoDiferentePendenteOuEmAndamento(1).size());
    }

    @Test
    public void findAllByAaId_listaComDoisRegistros_quandoHouverSolicitacaoPeloAaIdComStatusEnviadoOuConcluido() {
        assertEquals(2, repository.findAllByAgenteAutorizadoIdAndSituacaoEnviadoOuConcluido(3334).size());
    }

    @Test
    public void findAllByAaId_listaVazia_quandoNaoHouverSolicitacaoPeloAaIdComStatusEnviadoOuConcluido() {
        assertEquals(0, repository.findAllByAgenteAutorizadoIdAndSituacaoEnviadoOuConcluido(2).size());
    }

}
