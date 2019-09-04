package br.com.xbrain.autenticacao.modules.importacaousuario.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UsuarioImportacaoPlanilhaTest {

    private static final String TELEFONE_RESIDENCIAL = "(43) 3321-3049";
    private static final String TELEFONE_CELULAR = "(43) 99820-3049";

    @Test
    public void of_deveConverterUsuarioImportacao_quandoUsuarioASerImportacaoPossuirTelefoneResidencialOuTelefoneCelular() {
        assertThat(UsuarioImportacaoPlanilha.of(umUsuarioImportacao(TELEFONE_RESIDENCIAL)))
            .extracting("nome", "email", "telefone", "telefone02", "cpf")
            .contains("RENATO ALEXSANDER", "RENATO1@HOTMAIL.COM", null, TELEFONE_RESIDENCIAL, "33344488800");

        assertThat(UsuarioImportacaoPlanilha.of(umUsuarioImportacao(TELEFONE_CELULAR)))
            .extracting("nome", "email", "telefone", "telefone02", "cpf")
            .contains("RENATO ALEXSANDER", "RENATO1@HOTMAIL.COM", TELEFONE_CELULAR, null, "33344488800");
    }

    private UsuarioImportacaoPlanilha umUsuarioImportacao(String telefone) {
        return UsuarioImportacaoPlanilha.builder()
            .nome("RENATO ALEXSANDER")
            .email("RENATO1@HOTMAIL.COM")
            .telefone(telefone)
            .cpf("33344488800")
            .build();
    }
}
