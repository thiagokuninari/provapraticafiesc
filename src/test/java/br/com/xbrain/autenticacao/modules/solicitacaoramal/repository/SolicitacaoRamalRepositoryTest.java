package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate.SolicitacaoRamalPredicate;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
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

    @Test
    public void findAllByAaId_listaComDoisRegistros_quandoHouverSolicitacaoPeloAaIdComStatusEnviadoOuConcluido() {
        assertEquals(2, repository.findAllByAgenteAutorizadoIdAndSituacaoEnviadoOuConcluido(3334).size());
    }

    @Test
    public void findAllByAaId_listaVazia_quandoNaoHouverSolicitacaoPeloAaIdComStatusEnviadoOuConcluido() {
        assertEquals(0, repository.findAllByAgenteAutorizadoIdAndSituacaoEnviadoOuConcluido(2).size());
    }

    @Test
    public void findAllBySituacaoAndDataEnviadoEmailExpiracaoIsNull_deveRetonarListaSolicitacaoRamal_quandoEncontrado() {
        assertThat(repository.findAllBySituacaoAndDataEnviadoEmailExpiracaoIsNull())
            .extracting("id", "situacao", "dataEnviadoEmailExpiracao")
            .containsExactly(tuple(1, PENDENTE, null),
                tuple(2, EM_ANDAMENTO, null),
                tuple(3, EM_ANDAMENTO, null),
                tuple(7, EM_ANDAMENTO, null),
                tuple(12, PENDENTE, null),
                tuple(13, PENDENTE, null),
                tuple(14, PENDENTE, null),
                tuple(20, PENDENTE, null));
    }

    @Test
    public void findBySolicitacaoId_deveRetonarSolicitacaoRamal_quandoEncontrado() {
        assertThat(repository.findBySolicitacaoId(1)).get()
            .extracting("id", "situacao", "dataEnviadoEmailExpiracao")
            .containsExactly(1, PENDENTE, null);
    }

    @Test
    public void findAllByPredicate_deveRetonarListaSolicitacaoRamal_quandoEncontrado() {
        assertThat(repository.findAllByPredicate(new SolicitacaoRamalPredicate().build()))
            .extracting("id", "situacao", "usuario.id")
            .containsExactly(tuple(1, PENDENTE, 106),
                tuple(2, EM_ANDAMENTO, 230),
                tuple(3, EM_ANDAMENTO, 101),
                tuple(4, CONCLUIDO, 104),
                tuple(5, PENDENTE, 226),
                tuple(6, EM_ANDAMENTO, 226),
                tuple(7, EM_ANDAMENTO, 226),
                tuple(8, EM_ANDAMENTO, 226),
                tuple(9, REJEITADO, 226),
                tuple(10, REJEITADO, 226),
                tuple(11, CONCLUIDO, 106),
                tuple(12, PENDENTE, 106),
                tuple(13, PENDENTE, 106),
                tuple(14, PENDENTE, 106),
                tuple(15, ENVIADO, 106),
                tuple(16, ENVIADO, 106),
                tuple(20, PENDENTE, 104));
    }
}
