package br.com.xbrain.autenticacao.modules.importacaousuario.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CpfUtilTest {

    private static final String CPF_INVALIDO = "00011122234";

    private static final String CPF_VALIDO = "00011122285";

    @Test
    public void formata_deveFormatarCpf_quandoSolicitado() {
        assertThat(CpfUtil.formata(CPF_VALIDO))
            .isEqualTo("000.111.222-85");
    }

    @Test
    public void formata_deveRetornarStringVazia_quandoCpfForNull() {
        assertThat(CpfUtil.formata(null))
            .isEqualTo("");
    }

    @Test
    public void adicionarZerosAEsquerda_deveAdicionarZerosAEsquerda_quandoSolicitado() {
        assertThat(CpfUtil.adicionarZerosAEsquerda("11"))
            .isEqualTo("00000000011");
    }

    @Test
    public void isCpfValido_deveRetornarFalse_quandoCpfForNull() {
        assertThat(CpfUtil.isCpfValido(null))
            .isFalse();
    }

    @Test
    public void isCpfValido_deveRetornarFalse_quandoCpfMenorQueOnzeDigitos() {
        assertThat(CpfUtil.isCpfValido("123456"))
            .isFalse();
    }

    @Test
    public void isCpfValido_deveRetornarFalse_quandoValorCpfForNumerosIguais() {
        assertThat(CpfUtil.isCpfValido("00000000000"))
            .isFalse();
    }

    @Test
    public void isCpfValido_deveRetornarFalse_quandoCalculoDeVerificacaoCpfForInvalido() {
        assertThat(CpfUtil.isCpfValido(CPF_INVALIDO))
            .isFalse();
    }

    @Test
    public void isCpfValido_deveRetornarTrue_quandoCpfValido() {
        assertThat(CpfUtil.isCpfValido(CPF_VALIDO))
            .isTrue();
    }
}
