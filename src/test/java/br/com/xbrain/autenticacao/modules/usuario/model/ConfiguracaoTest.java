package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioConfiguracaoDto;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoTest {

    @Test
    public void configurar_deveConfigurarOsCampos_quandoSolicitado() {
        var usuario = Usuario.builder().id(1).build();

        var configuracao = new Configuracao();
        assertThat(configuracao)
            .extracting("usuario", "usuarioCadastro", "ramal", "cadastro")
            .containsExactly(null, null, null, null);

        configuracao.configurar(umUsuarioConfiguracaoDto());

        assertThat(configuracao)
            .extracting("usuario", "usuarioCadastro", "ramal")
            .containsExactly(usuario, usuario, 123456);

        assertThat(configuracao.getCadastro()).isNotNull();
    }

    @Test
    public void removerRamal_deveRemoverRamal_quandoSolicitado() {
        var configuracao = new Configuracao();
        configuracao.setRamal(123456);

        assertThat(configuracao.getRamal()).isEqualTo(123456);

        configuracao.removerRamal();

        assertThat(configuracao.getRamal()).isNull();
    }

    private UsuarioConfiguracaoDto umUsuarioConfiguracaoDto() {
        var usuario = new UsuarioConfiguracaoDto();
        usuario.setUsuario(1);
        usuario.setRamal(123456);
        return usuario;
    }
}
