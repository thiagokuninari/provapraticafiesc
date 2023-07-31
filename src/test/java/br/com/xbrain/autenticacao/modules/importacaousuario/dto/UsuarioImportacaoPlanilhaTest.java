package br.com.xbrain.autenticacao.modules.importacaousuario.dto;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioImportacaoPlanilhaTest {

    private static final String TELEFONE_RESIDENCIAL = "(43) 3321-3049";
    private static final String TELEFONE_RESIDENCIAL_SEM_HIFEN = "(43) 33213049";
    private static final String TELEFONE_CELULAR = "(43) 99820-3049";
    private static final String TELEFONE_CELULAR_SEM_HIFEN = "(43) 998203049";

    @Test
    public void of_deveConverterUsuarioImportacao_quandoUsuarioASerImportacaoPossuirTelefoneResidencialOuTelefoneCelular() {
        assertThat(UsuarioImportacaoPlanilha.of(umUsuarioImportacao(TELEFONE_RESIDENCIAL)))
            .extracting("nome", "email", "telefone", "telefone02", "cpf")
            .contains("RENATO ALEXSANDER", "RENATO1@HOTMAIL.COM", null, TELEFONE_RESIDENCIAL, "33344488800");

        assertThat(UsuarioImportacaoPlanilha.of(umUsuarioImportacao(TELEFONE_RESIDENCIAL_SEM_HIFEN)))
            .extracting("nome", "email", "telefone", "telefone02", "cpf")
            .contains("RENATO ALEXSANDER", "RENATO1@HOTMAIL.COM", null, TELEFONE_RESIDENCIAL_SEM_HIFEN, "33344488800");

        assertThat(UsuarioImportacaoPlanilha.of(umUsuarioImportacao(TELEFONE_CELULAR)))
            .extracting("nome", "email", "telefone", "telefone02", "cpf")
            .contains("RENATO ALEXSANDER", "RENATO1@HOTMAIL.COM", TELEFONE_CELULAR, null, "33344488800");

        assertThat(UsuarioImportacaoPlanilha.of(umUsuarioImportacao(TELEFONE_CELULAR_SEM_HIFEN)))
            .extracting("nome", "email", "telefone", "telefone02", "cpf")
            .contains("RENATO ALEXSANDER", "RENATO1@HOTMAIL.COM", TELEFONE_CELULAR_SEM_HIFEN, null, "33344488800");
    }

    public static UsuarioImportacaoPlanilha umUsuarioImportacao(String telefone) {
        return UsuarioImportacaoPlanilha.builder()
            .nome("RENATO ALEXSANDER")
            .email("RENATO1@HOTMAIL.COM")
            .telefone(telefone)
            .cpf("33344488800")
            .build();
    }
}
