package br.com.xbrain.autenticacao.modules.solicitacaoramal.util;

import br.com.xbrain.autenticacao.modules.feriado.model.FeriadoSingleton;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

import static org.junit.Assert.assertEquals;

public class SolicitacaoRamalExpiracaoAdjusterTest {

    @Before
    public void setUp() {
        FeriadoSingleton.getInstance().setFeriados(Sets.newHashSet(LocalDate.of(2019, 12, 25)));
    }

    @Test
    public void adjustInto_dataCadastroComTresDias_quandoDataCadastroAposTresDiasNaoCairNoDomingoOuFeriado() {
        LocalDateTime dataCadastro = LocalDateTime.of(2019, 12, 17, 12, 00);

        Temporal temporal = dataCadastro.with(new SolicitacaoRamalExpiracaoAdjuster());

        LocalDateTime dataEsperada = LocalDateTime.of(2019, 12, 20, 12, 00);
        assertEquals(dataEsperada, LocalDateTime.from(temporal));
        assertEquals(72, Duration.between(dataCadastro, dataEsperada).toHours());
    }

    @Test
    public void adjustInto_dataCadastroComCincoDias_quandoDataCadastroCairEmFeriadoEDomingo() {
        LocalDateTime dataCadastro = LocalDateTime.of(2019, 12, 25, 12, 23);

        Temporal temporal = dataCadastro.with(new SolicitacaoRamalExpiracaoAdjuster());

        LocalDateTime dataEsperada = LocalDateTime.of(2019, 12, 30, 12, 23);
        assertEquals(dataEsperada, LocalDateTime.from(temporal));
        assertEquals(120, Duration.between(dataCadastro, dataEsperada).toHours());
    }

    @Test
    public void adjustInto_dataCadastroComCincoDias_quandoDataCadastroCairNoDomingoEFeriado() {
        LocalDateTime dataCadastro = LocalDateTime.of(2019, 12, 29, 14, 00);

        Temporal temporal = dataCadastro.with(new SolicitacaoRamalExpiracaoAdjuster());

        LocalDateTime dataEsperada = LocalDateTime.of(2020, 01, 02, 14, 00);
        assertEquals(dataEsperada, LocalDateTime.from(temporal));
        assertEquals(96, Duration.between(dataCadastro, dataEsperada).toHours());
    }

    @Test
    public void adjustInto_dataCadastroComQuatroDias_quandoDataCadastroCairNoDomingo() {
        LocalDateTime dataCadastro = LocalDateTime.of(2019, 12, 15, 12, 00);

        Temporal temporal = dataCadastro.with(new SolicitacaoRamalExpiracaoAdjuster());

        LocalDateTime dataEsperada = LocalDateTime.of(2019, 12, 19, 12, 00);
        assertEquals(dataEsperada, LocalDateTime.from(temporal));
        assertEquals(96, Duration.between(dataCadastro, dataEsperada).toHours());
    }
}
