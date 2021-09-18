package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class SiteCsvResponseTest {

    @Test
    public void getCabecalhoCsv_deveRetornarCabecalho_seSolicitado() {
        assertThat(SiteCsvResponse.getCabecalhoCsv())
            .isEqualTo("NOME;FUSO HORARIO;DISCADORA;SITUACAO;\n");
    }

    @Test
    public void getLinhas_deveRetornarLinhasDoCsv_seListaPreenchida() {
        assertThat(SiteCsvResponse.getLinhasCsv(umaListaSitesCsv()))
            .isEqualTo("Teste 1;Horário de Brasília;;Ativo\n"
                + "Teste 2;Horário de Brasília;;Ativo\n"
                + "Teste 3;Horário de Brasília;;Ativo\n"
                + "Teste 4;Horário de Brasília;;Ativo"
            );
    }

    @Test
    public void getLinhas_deveRetornarLinhasSemRegistro_seListaVazia() {
        assertThat(SiteCsvResponse.getLinhasCsv(Collections.emptyList()))
            .isEqualTo("Registros não encontrados.\n");
    }

    @Test
    public void of_deveRetornarListaCsv_seListaPreenchida() {
        assertThat(SiteCsvResponse.of(umaListaSites()))
            .asList().isEqualTo(umaListaSitesCsv());
    }

    @Test
    public void of_deveRetornarCsv_seListaPreenchida() {
        assertThat(SiteCsvResponse.ofCsv(umaListaSitesCsv()))
            .isEqualTo("NOME;FUSO HORARIO;DISCADORA;SITUACAO;\n"
                + "Teste 1;Horário de Brasília;;Ativo\n"
                + "Teste 2;Horário de Brasília;;Ativo\n"
                + "Teste 3;Horário de Brasília;;Ativo\n"
                + "Teste 4;Horário de Brasília;;Ativo"
            );
    }

    @Test
    public void of_deveRetornarCsvSemRegistros_seListaVazia() {
        assertThat(SiteCsvResponse.ofCsv(Collections.emptyList()))
            .isEqualTo("NOME;FUSO HORARIO;DISCADORA;SITUACAO;\n"
                + "Registros não encontrados.\n");
    }

    private List<Site> umaListaSites() {
        return List.of(
            umSite("Teste 1"),
            umSite("Teste 2"),
            umSite("Teste 3"),
            umSite("Teste 4")
        );
    }

    private List<SiteCsvResponse> umaListaSitesCsv() {
        return List.of(
            umSiteCsv("Teste 1"),
            umSiteCsv("Teste 2"),
            umSiteCsv("Teste 3"),
            umSiteCsv("Teste 4")
        );
    }

    private Site umSite(String nome) {
        return Site.builder()
            .nome(nome)
            .timeZone(ETimeZone.BRT)
            .situacao(ESituacao.A)
            .build();
    }

    private SiteCsvResponse umSiteCsv(String nome) {
        return SiteCsvResponse.builder()
            .nome(nome)
            .timeZone(ETimeZone.BRT)
            .situacao(ESituacao.A)
            .build();
    }
}
