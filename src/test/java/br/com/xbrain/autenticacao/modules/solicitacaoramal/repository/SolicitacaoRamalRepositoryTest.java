package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
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
    public void findAll_deveRetornarApenasSolicitacoesDaEquipe_quandoHouverSolicitacao() {
        var predicate = new SolicitacaoRamalFiltros().toPredicate()
            .comEquipeId(1).build();
        assertEquals(1, repository.findAll(new PageRequest(), predicate).getTotalElements());
    }

    @Test
    public void findAll_deveRetornarListaVazia_quandoNaoHouverSolicitacaoDaEquipe() {
        var predicate = new SolicitacaoRamalFiltros().toPredicate()
            .comEquipeId(2).build();
        assertEquals(0, repository.findAll(new PageRequest(), predicate).getTotalElements());
    }

    @Test
    public void findAllByAaId_deveRetornarlistaVazia_quandoNaoHouverSolicitacaoComStatusPendenteOuEmAndamento() {
        assertEquals(0, repository.findAllByAgenteAutorizadoIdAndSituacaoPendenteOuEmAndamento(3).size());
    }

    @Test
    public void findAllByAaId_deveRetornarlistaComDoisRegistros_quandoHouverSolicitacaoComStatusPendenteOuEmAndamento() {
        assertEquals(3, repository.findAllByAgenteAutorizadoIdAndSituacaoPendenteOuEmAndamento(1).size());
    }

    @Test
    public void findAllBySubCanalId_deveRetornarlistaVazia_quandoNaoHouverSolicitacaoComStatusPendenteOuEmAndamento() {
        assertEquals(0, repository.findAllBySubCanalIdAndSituacaoPendenteOuEmAndamento(3).size());
    }

    @Test
    public void findAllBySubCanalId_deveRetornarlistaComDoisRegistros_quandoHouverSolicitacaoComStatusPendenteOuEmAndamento() {
        assertEquals(2, repository.findAllBySubCanalIdAndSituacaoPendenteOuEmAndamento(1).size());
    }

    @Test
    public void findAllGerencia_deveRetornarLista_quandoCanalForAgenteAutorizado() {
        var filtros = umaSolicitacaoFiltros();
        filtros.setCanal(ECanal.AGENTE_AUTORIZADO);
        assertEquals(10, repository.findAllGerenciaAa(new PageRequest(), filtros.toPredicate().build()).getSize());
    }

    @Test
    public void findAllGerencia_deveRetornarLista_quandoCanalForD2d() {
        var filtros = umaSolicitacaoFiltros();
        filtros.setCanal(ECanal.D2D_PROPRIO);
        assertEquals(10, repository.findAllGerenciaD2d(new PageRequest(), filtros.toPredicate().build()).getSize());
    }

    private SolicitacaoRamalFiltros umaSolicitacaoFiltros() {
        var filtros = new SolicitacaoRamalFiltros();
        return filtros;
    }

}
