package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioCadastroSucessoMqDtoTest {

    @Test
    public void of_deveRetornarDtoComDadosCorretos_quandoUsuarioOk() {
        assertThat(UsuarioCadastroSucessoMqDto.of(umUsuarioGeradorLeads(), umUsuarioGeradorLeadsMqDto()))
            .extracting("geradorLeadsId", "usuarioId", "usuarioCadastroId")
            .containsExactlyInAnyOrder(100, 2222, 9999);
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

    private Usuario umUsuarioGeradorLeads() {
        return Usuario.builder()
            .id(2222)
            .nome("JOHN DOE")
            .email("JOHN@GMAIL.COM")
            .cpf("47492951671")
            .telefone("43998281179")
            .usuarioCadastro(new Usuario(9999))
            .situacao(ESituacao.A)
            .dataCadastro(LocalDateTime.of(2020,1, 29, 11, 11, 11))
            .build();
    }
}
