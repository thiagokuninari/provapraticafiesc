package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql("classpath:/feriado-repository-test.sql")
public class ImportacaoAutomaticaRepositoryTest {

    @Autowired
    private ImportacaoAutomaticaFeriadoRepository repository;

    @Test
    public void findAllImportacaoHistorico_deveRetornarHistoricoDeImportacoes_seHouverRegistros() {
        assertThat(repository.findAllImportacaoHistorico(new PageRequest(), new FeriadoPredicate().build()))
            .extracting("id", "situacaoFeriadoAutomacao", "usuarioCadastroId",
                "usuarioCadastroNome")
            .containsExactlyInAnyOrder(
                tuple(1, ESituacaoFeriadoAutomacao.IMPORTADO, 1, "FIORILLO"),
                tuple(2, ESituacaoFeriadoAutomacao.IMPORTADO, 1, "FIORILLO"),
                tuple(3, ESituacaoFeriadoAutomacao.ERRO_IMPORTACAO, 1, "FIORILLO")
            );
    }

    @Test
    public void findAllImportacaoHistorico_deveRetornarImportacoesComErro_sePassadosComoParametro() {
        var predicate = new FeriadoPredicate();
        predicate.comSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.ERRO_IMPORTACAO);

        assertThat(repository.findAllImportacaoHistorico(new PageRequest(), predicate.build()))
            .extracting("id", "situacaoFeriadoAutomacao", "usuarioCadastroId",
                "usuarioCadastroNome")
            .containsExactlyInAnyOrder(
                tuple(3, ESituacaoFeriadoAutomacao.ERRO_IMPORTACAO, 1, "FIORILLO")
            );
    }

    @Test
    public void findAllImportacaoHistorico_deveRetornarPageVazia_seNaoHouverRegistros() {
        var predicate = new FeriadoPredicate();
        predicate.comSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.EM_IMPORTACAO);

        assertThat(repository.findAllImportacaoHistorico(new PageRequest(), predicate.build()))
            .isEmpty();
    }
}
