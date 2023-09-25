package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioFiltrosTest {

    @Test
    public void toPredicate_deveMontarBooleanBuilder_quandoSolicitado() {
        var predicate = umUsuarioFiltros()
            .toPredicate()
            .build()
            .toString();

        assertThat(predicate)
            .isEqualTo("usuario.cpf = 67852746489 && "
                + "lower(usuario.nome) like %lucas gomez% && "
                + "lower(usuario.email) like %lucas@xbrain.com% && "
                + "any(usuario.canais) = D2D_PROPRIO && "
                + "any(usuario.subCanais).id = 1 && "
                + "usuario.cargo.nivel.id = 1 && "
                + "usuario.cargo.id = 1 && "
                + "usuario.departamento.id = 1 && "
                + "usuario.id = 1");
    }

    private static UsuarioFiltros umUsuarioFiltros() {
        return UsuarioFiltros.builder()
            .id(1)
            .nome("LUCAS GOMEZ")
            .emailUsuario("LUCAS@XBRAIN.COM")
            .cpf("67852746489")
            .canal(ECanal.D2D_PROPRIO)
            .subCanalId(1)
            .cargoId(1)
            .cnpjAa("11146546000171")
            .departamentoId(1)
            .nivelId(1)
            .build();
    }

}
