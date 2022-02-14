package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertFalse;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_solicitacao_ramal_database.sql"})
public class SolicitacaoRamalServiceIT {

    @Autowired
    private SolicitacaoRamalService service;
    @Autowired
    private SolicitacaoRamalRepository repository;

    @Test
    public void remover_deveRetornarFalse_quandoRemoverSolicitacaoRamalComStatusPendente() {
        service.remover(1);

        assertFalse(repository.findById(1).isPresent());
    }

    @Test(expected = NotFoundException.class)
    public void remover_deveRetornarNotFoundException_quandoSolicitacaoIdNaoExistir() {
        service.remover(1000);
    }

    @Test(expected = ValidacaoException.class)
    public void remover_deveRetornarValidacaoException_quandoSolicitacaoEstiverComStatusDiferenteDePendente() {
        service.remover(6);
    }
}
