package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import org.junit.Test;

import java.time.LocalDate;

import static br.com.xbrain.autenticacao.modules.comum.enums.Eboolean.F;
import static br.com.xbrain.autenticacao.modules.comum.enums.Eboolean.V;
import static br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado.MUNICIPAL;
import static org.assertj.core.api.Assertions.*;

public class FeriadoRequestTest {

    @Test
    public void convertFrom_deveRetonarFeriadoComFeriadoNacionalFalsoEComNovaCidade_quandoExistirCidadeId() {
        assertThat(FeriadoRequest.convertFrom(umFeriadoRequest(989898)))
            .extracting("nome", "dataFeriado", "feriadoNacional", "cidade.id", "tipoFeriado")
            .containsExactly("FERIADO MUNICIPAL", LocalDate.of(2019, 12, 12), F,
                989898, MUNICIPAL);
    }

    @Test
    public void convertFrom_deveRetonarFeriadoComFeriadoNacionalVerdadeiro_quandoNaoExistirCidadeId() {
        assertThat(FeriadoRequest.convertFrom(umFeriadoRequest(null)))
            .extracting("nome", "dataFeriado", "feriadoNacional", "cidade.id", "tipoFeriado")
            .containsExactly("FERIADO MUNICIPAL", LocalDate.of(2019, 12, 12), V,
                null, MUNICIPAL);
    }

    @Test
    public void validarDadosObrigatorios_deveLancarException_quandoFeriadoNacionalTiverEstadoId() {
        var feriadoNacionalComEstadoId = umFeriadoNacionalRequest();
        feriadoNacionalComEstadoId.setEstadoId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(feriadoNacionalComEstadoId::validarDadosObrigatorios)
            .withMessage("Para este Tipo de Feriado não é permitido cadastrar ESTADO.");
    }

    @Test
    public void validarDadosObrigatorios_deveLancarException_quandoFeriadoNacionalTiverCidadeId() {
        var feriadoNacionalComCidadeId = umFeriadoNacionalRequest();
        feriadoNacionalComCidadeId.setCidadeId(5543);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(feriadoNacionalComCidadeId::validarDadosObrigatorios)
            .withMessage("Para este Tipo de Feriado não é permitido cadastrar CIDADE.");
    }

    @Test
    public void validarDadosObrigatorios_naoDeveLancarException_quandoFeriadoNacionalTiverDadosCorretos() {
        assertThatCode(umFeriadoNacionalRequest()::validarDadosObrigatorios)
            .doesNotThrowAnyException();
    }

    @Test
    public void validarDadosObrigatorios_deveLancarException_quandoFeriadoEstadualNaoTiverEstadoId() {
        var feriadoEstadualSemEstadoId = umFeriadoEstadualRequest();
        feriadoEstadualSemEstadoId.setEstadoId(null);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(feriadoEstadualSemEstadoId::validarDadosObrigatorios)
            .withMessage("Para este Tipo de Feriado o campo ESTADO é obrigatório.");
    }

    @Test
    public void validarDadosObrigatorios_deveLancarException_quandoFeriadoEstadualTiverCidadeId() {
        var feriadoEstadualComCidadeId = umFeriadoEstadualRequest();
        feriadoEstadualComCidadeId.setCidadeId(5543);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(feriadoEstadualComCidadeId::validarDadosObrigatorios)
            .withMessage("Para este Tipo de Feriado não é permitido cadastrar CIDADE.");
    }

    @Test
    public void validarDadosObrigatorios_naoDeveLancarException_quandoFeriadoEstadualTiverDadosCorretos() {
        assertThatCode(umFeriadoEstadualRequest()::validarDadosObrigatorios)
            .doesNotThrowAnyException();
    }

    @Test
    public void validarDadosObrigatorios_deveLancarException_quandoFeriadoMunicipalNaoTiverEstadoId() {
        var feriadoMunicipalSemEstadoId = umFeriadoMunicipalRequest();
        feriadoMunicipalSemEstadoId.setEstadoId(null);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(feriadoMunicipalSemEstadoId::validarDadosObrigatorios)
            .withMessage("Para este Tipo de Feriado o campo ESTADO é obrigatório.");
    }

    @Test
    public void validarDadosObrigatorios_deveLancarException_quandoFeriadoMunicipalNaoTiverCidadeId() {
        var feriadoMunicipalSemCidadeId = umFeriadoMunicipalRequest();
        feriadoMunicipalSemCidadeId.setCidadeId(null);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(feriadoMunicipalSemCidadeId::validarDadosObrigatorios)
            .withMessage("Para este Tipo de Feriado o campo CIDADE é obrigatório.");
    }

    @Test
    public void validarDadosObrigatorios_naoDeveLancarException_quandoFeriadoMunicipalTiverDadosCorretos() {
        assertThatCode(umFeriadoMunicipalRequest()::validarDadosObrigatorios)
            .doesNotThrowAnyException();
    }

    @Test
    public void isFeriadoNacional_deveRetornarTrue_quandoFeriadoRequestNacional() {
        assertThat(umFeriadoNacionalRequest().isFeriadoNacional()).isTrue();
    }

    @Test
    public void isFeriadoNacional_deveRetornarFalse_quandoFeriadoRequestNaoNacional() {
        assertThat(umFeriadoEstadualRequest().isFeriadoNacional()).isFalse();
        assertThat(umFeriadoMunicipalRequest().isFeriadoNacional()).isFalse();
    }

    @Test
    public void isTipoFeriado_deveRetornarTrue_quandoTipoFeriadoIgualDoRequest() {
        assertThat(umFeriadoNacionalRequest().isTipoFeriado(ETipoFeriado.NACIONAL)).isTrue();
        assertThat(umFeriadoEstadualRequest().isTipoFeriado(ETipoFeriado.ESTADUAL)).isTrue();
        assertThat(umFeriadoMunicipalRequest().isTipoFeriado(MUNICIPAL)).isTrue();
    }

    @Test
    public void isTipoFeriado_deveRetornarFalse_quandoTipoFeriadoNaoIgualDoRequest() {
        assertThat(umFeriadoNacionalRequest().isTipoFeriado(ETipoFeriado.ESTADUAL)).isFalse();
        assertThat(umFeriadoEstadualRequest().isTipoFeriado(MUNICIPAL)).isFalse();
        assertThat(umFeriadoMunicipalRequest().isTipoFeriado(ETipoFeriado.NACIONAL)).isFalse();
    }

    private FeriadoRequest umFeriadoNacionalRequest() {
        return FeriadoRequest.builder()
            .nome("FERIADO NACIONAL")
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .dataFeriado("12/11/2019")
            .build();
    }

    private FeriadoRequest umFeriadoEstadualRequest() {
        return FeriadoRequest.builder()
            .nome("FERIADO ESTADUAL")
            .tipoFeriado(ETipoFeriado.ESTADUAL)
            .estadoId(1)
            .dataFeriado("12/07/2019")
            .build();
    }

    private FeriadoRequest umFeriadoMunicipalRequest() {
        return FeriadoRequest.builder()
            .nome("FERIADO MUNICIPAL")
            .tipoFeriado(MUNICIPAL)
            .estadoId(1)
            .cidadeId(5543)
            .dataFeriado("12/12/2019")
            .build();
    }

    private FeriadoRequest umFeriadoRequest(Integer cidadeId) {
        return FeriadoRequest.builder()
            .nome("FERIADO MUNICIPAL")
            .tipoFeriado(MUNICIPAL)
            .estadoId(1)
            .cidadeId(cidadeId)
            .dataFeriado("12/12/2019")
            .build();
    }
}
