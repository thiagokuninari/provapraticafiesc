package br.com.xbrain.autenticacao.modules.comum.helper;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DeslogarUsuarioPorExcessoDeUsoHelper {

    public static List<UsuarioParaDeslogar> umaListaDeUsuariosParaDeslogar() {
        return List.of(
            new UsuarioParaDeslogar(1, 1, LocalDateTime.now(), Eboolean.F),
            new UsuarioParaDeslogar(2, 2, LocalDateTime.now(), Eboolean.F),
            new UsuarioParaDeslogar(3, 3, LocalDateTime.now(), Eboolean.F),
            new UsuarioParaDeslogar(4, 4, LocalDateTime.now(), Eboolean.F));
    }

    public static Optional<Usuario> umUsuarioComSituacao(ESituacao situacao) {
        return Optional.of(
            Usuario
                .builder()
                .id(1)
                .email("TESTE@TESTE.COM")
                .situacao(situacao)
                .build()
        );
    }

    public static List<UsuarioParaDeslogar> umaListaDeUsuariosDeslogados() {
        return List.of(
            new UsuarioParaDeslogar(1, 1, LocalDateTime.now(), Eboolean.V),
            new UsuarioParaDeslogar(2, 2, LocalDateTime.now(), Eboolean.V),
            new UsuarioParaDeslogar(3, 3, LocalDateTime.now(), Eboolean.V),
            new UsuarioParaDeslogar(4, 4, LocalDateTime.now(), Eboolean.V));
    }

    public static List<UsuarioParaDeslogar> umaListaDeUsuariosParaDeslogados_Vazia() {
        return List.of();
    }
}
