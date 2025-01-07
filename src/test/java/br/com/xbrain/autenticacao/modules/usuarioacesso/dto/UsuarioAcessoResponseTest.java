package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioAcessoResponseTest {

    @Test
    public void of_deveRetornarUsuarioAcessoResponse_quandoSolicitado() {
        assertThat(UsuarioAcessoResponse.of(umUsuarioAcesso("05578735609")))
            .extracting("id", "nome", "cpf", "email")
            .containsExactly(3, "NOME USUARIO", "055.787.356-09", "usuario@xbrain.com.br");
    }

    @Test
    public void of_deveRetornarUsuarioAcessoResponseComCpf_quandoCpfMenorQueOnze() {
        assertThat(UsuarioAcessoResponse.of(umUsuarioAcesso("9090")))
            .extracting("id", "nome", "cpf", "email")
            .containsExactly(3, "NOME USUARIO", "9090", "usuario@xbrain.com.br");
    }

    private UsuarioAcesso umUsuarioAcesso(String cpf) {
        return UsuarioAcesso.builder()
            .usuario(Usuario.builder()
                .id(3)
                .nome("NOME USUARIO")
                .cpf(cpf)
                .email("usuario@xbrain.com.br")
                .build())
            .build();
    }
}
