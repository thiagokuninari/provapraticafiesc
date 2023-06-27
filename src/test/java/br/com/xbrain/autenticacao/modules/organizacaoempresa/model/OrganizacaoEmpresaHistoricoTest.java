package br.com.xbrain.autenticacao.modules.organizacaoempresa.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganizacaoEmpresaHistoricoTest {

    @Test
    public void of_deveRetornarObjetoCorreto_quandoRecebeOrganizacaoEmpresa() {
        assertThat(OrganizacaoEmpresaHistorico.of(umaOrganizacaoEmpresaCadastrada(), EHistoricoAcao.EDICAO,
            umUsuarioAutenticado()))
            .extracting("situacao", "observacao", "usuarioNome")
            .containsExactly(ESituacaoOrganizacaoEmpresa.A, EHistoricoAcao.EDICAO, "Thiago");

    }

    private OrganizacaoEmpresa umaOrganizacaoEmpresaCadastrada() {
        return OrganizacaoEmpresa.builder()
            .id(1)
            .nome("THIAGO TESTE")
            .nivel(Nivel.builder()
                .codigo(CodigoNivel.RECEPTIVO)
                .build())
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .dataCadastro(LocalDateTime.of(2022, 1, 5, 9, 10, 10))
            .usuarioCadastro(umUsuario())
            .build();
    }

    public static Usuario umUsuario() {
        var usuario = new Usuario();
        usuario.setId(100);
        usuario.setNome("Thiago");
        return usuario;
    }

    public static UsuarioAutenticado umUsuarioAutenticado() {
        var usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setId(2);
        usuarioAutenticado.setNome("Thiago");
        return usuarioAutenticado;
    }
}
