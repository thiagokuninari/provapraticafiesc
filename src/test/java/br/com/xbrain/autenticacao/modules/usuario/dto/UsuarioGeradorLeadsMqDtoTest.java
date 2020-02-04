package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioGeradorLeadsMqDtoTest {

    @Test
    public void criarUsuarioNovo_deveRetornarUsuarioComDadosCorretos() {
        assertThat(UsuarioGeradorLeadsMqDto.criarUsuarioNovo(umUsuarioGeradorLeadsMqDto()))
            .extracting("id", "nome", "email", "cpf", "cargo",
                "usuarioCadastro.id", "dataCadastro", "departamento")
            .containsExactlyInAnyOrder(null, "JOHN DOE", "JOHN@GMAIL.COM", "47492951671", null, 9999,
                LocalDateTime.of(2020,1, 29, 11, 11, 11), null);
    }

    @Test
    public void criarUsuarioNovo_deveRetornarUsuarioComUsuarioCadastroIdNull_quandoDtoNaoTemUsuarioCadastroId() {
        var umUsuarioGeradorLeadsSemUsuarioCadastro = umUsuarioGeradorLeadsMqDto();
        umUsuarioGeradorLeadsSemUsuarioCadastro.setUsuarioCadastroId(null);
        assertThat(UsuarioGeradorLeadsMqDto.criarUsuarioNovo(umUsuarioGeradorLeadsSemUsuarioCadastro))
            .extracting("id", "nome", "email", "cpf", "cargo",
                "usuarioCadastro.id", "dataCadastro", "departamento")
            .containsExactlyInAnyOrder(null, "JOHN DOE", "JOHN@GMAIL.COM", "47492951671", null, null,
                LocalDateTime.of(2020,1, 29, 11, 11, 11), null);
    }

    private UsuarioGeradorLeadsMqDto umUsuarioGeradorLeadsMqDto() {
        return UsuarioGeradorLeadsMqDto.builder()
            .geradorLeadsId(100)
            .nome("JOHN DOE")
            .email("JOHN@GMAIL.COM")
            .cpf("47492951671")
            .telefone("43998281179")
            .usuarioCadastroId(9999)
            .situacao(ESituacao.A)
            .dataCadastro(LocalDateTime.of(2020,1, 29, 11, 11, 11))
            .build();
    }
}
