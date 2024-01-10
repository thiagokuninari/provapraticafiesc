package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresaHistorico;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganizacaoEmpresaHistoricoResponseTest {

    @Test
    public void of_deveRetornarOrganizacaoEmpresaHistoricoResponseComDadosCorretos_quandoChamado() {
        assertThat(OrganizacaoEmpresaHistoricoResponse.of(umaOrganizacaoEmpresaHistorico()))
            .extracting("dataAlteracao", "observacao", "usuarioNome")
            .containsExactly(umaOrganizacaoEmpresaHistorico().getDataAlteracao(), EHistoricoAcao.EDICAO, "THIAGO");
    }

    private OrganizacaoEmpresa umaOrganizacaoEmpresaCadastrada() {
        return OrganizacaoEmpresa.builder()
            .id(1)
            .nome("THIAGO TESTE")
            .nivel(Nivel.builder()
                .codigo(CodigoNivel.BACKOFFICE)
                .build())
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .dataCadastro(LocalDateTime.of(2022, 1, 5, 9, 10, 10))
            .usuarioCadastro(umUsuario())
            .build();
    }

    private OrganizacaoEmpresaHistorico umaOrganizacaoEmpresaHistorico() {
        return OrganizacaoEmpresaHistorico.builder()
            .organizacaoEmpresa(umaOrganizacaoEmpresaCadastrada())
            .dataAlteracao(LocalDateTime.of(2022, 1, 5, 9, 10, 10))
            .observacao(EHistoricoAcao.EDICAO)
            .usuarioNome("THIAGO")
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
