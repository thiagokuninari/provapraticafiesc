package br.com.xbrain.autenticacao.modules.solicitacaoramal.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.model.FeriadoSingleton;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class SolicitacaoRamalTest {

    @Before
    public void setUp() {
        FeriadoSingleton.getInstance().setFeriados(Sets.newHashSet(LocalDate.of(2022, 01, 21)));
    }

    @Test
    public void atualizarDataCadastro_deveSetarDataFinalizacaoAcrescidaDe72HorasUteis() {
        var dataAtual = LocalDateTime.of(2022, 01, 20,12,00,00);

        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.atualizarDataCadastro(dataAtual);

        assertThat(solicitacaoRamal)
            .extracting("dataCadastro", "dataFinalizacao", "situacao", "enviouEmailExpiracao")
            .containsExactlyInAnyOrder(dataAtual,
                LocalDateTime.of(2022, 01, 25, 12,00,00),
                ESituacaoSolicitacao.PENDENTE, Eboolean.F);
    }

    @Test
    public void retirarMascaras_deveRemoverAsMascarasDeTelefoneECnpjDoAa() {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setTelefoneTi("(43) 3333-2222");
        solicitacaoRamal.setAgenteAutorizadoCnpj("12.123.123/0001-00");

        solicitacaoRamal.retirarMascara();
        assertThat(solicitacaoRamal)
            .extracting("telefoneTi", "agenteAutorizadoCnpj")
            .containsExactlyInAnyOrder("4333332222", "12123123000100");
    }
}
