package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioComLoginNetSalesResponseTest {

    @Test
    public void of_deveRetornarUsuarioComLoginNetSalesResponse_quandoSolicitado() {
        assertThat(UsuarioComLoginNetSalesResponse.of(umUsuario()))
            .extracting(UsuarioComLoginNetSalesResponse::getId, UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales, UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getRazaoSocialEmpresa, UsuarioComLoginNetSalesResponse::getCpfNetSales,
                UsuarioComLoginNetSalesResponse::getOrganizacaoEmpresaNome,
                UsuarioComLoginNetSalesResponse::getNomeEquipeVendasNetSales,
                UsuarioComLoginNetSalesResponse:: getCanalNetSales)
            .containsExactlyInAnyOrder(9928, "Teste", "Login teste", "OPERACAO_INTERNET", "CLARO S.A.",
                "685.313.412-56", "Organizacao teste", "NOME EQUIPE VENDAS NET SALES", "CANAL NETSALES");
    }

    @Test
    public void of_deveRetornarUsuarioComLoginNetSalesResponse_quandoCodigoEquipeVendaNull() {
        var usuario = umUsuario();
        usuario.setNomeEquipeVendaNetSales(null);
        assertThat(UsuarioComLoginNetSalesResponse.of(usuario))
            .extracting(UsuarioComLoginNetSalesResponse::getId, UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales, UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getRazaoSocialEmpresa, UsuarioComLoginNetSalesResponse::getCpfNetSales,
                UsuarioComLoginNetSalesResponse::getOrganizacaoEmpresaNome,
                UsuarioComLoginNetSalesResponse::getNomeEquipeVendasNetSales,
                UsuarioComLoginNetSalesResponse:: getCanalNetSales)
            .containsExactlyInAnyOrder(9928, "Teste", "Login teste", "OPERACAO_INTERNET", "CLARO S.A.",
                "685.313.412-56", "Organizacao teste", null, "CANAL NETSALES");
    }

    @Test
    public void getNivelCodigo_deveRetornarNivelCodigoReceptivo_quandoSolicitadoEUsuarioReceptivo() {
        var usuario = umUsuario();
        usuario.getCargo().getNivel().setCodigo(CodigoNivel.RECEPTIVO);
        assertThat(UsuarioComLoginNetSalesResponse.getNivelCodigo(usuario))
            .isEqualTo("RECEPTIVO_Organizacaoteste");
    }

    @Test
    public void getNivelCodigo_deveRetornarNivelCodigoOperacao_quandoSolicitadoEUsuarioOperacao() {
        var usuario = umUsuario();
        assertThat(UsuarioComLoginNetSalesResponse.getNivelCodigo(usuario))
            .isEqualTo("OPERACAO_INTERNET");
    }

    public Usuario umUsuario() {
        return Usuario
            .builder()
            .id(9928)
            .nome("Teste")
            .cpf("685.313.412-56")
            .cargo(Cargo.builder()
                .id(7)
                .nome("Gerente teste")
                .codigo(CodigoCargo.INTERNET_GERENTE)
                .situacao(ESituacao.A)
                .nivel(Nivel.builder().codigo(CodigoNivel.OPERACAO).build())
                .build())
            .situacao(ESituacao.A)
            .unidadesNegocios(List.of())
            .loginNetSales("Login teste")
            .organizacaoEmpresa(OrganizacaoEmpresa.builder()
                .canal(ECanal.INTERNET)
                .descricao("Organizacao teste")
                .nome("Organizacaoteste")
                .codigo("ORGANIZACAO_TESTE")
                .build())
            .canais(Set.of(ECanal.INTERNET))
            .codigoEquipeVendaNetSales("T0909")
            .canalNetSales("CANAL NETSALES")
            .nomeEquipeVendaNetSales("NOME EQUIPE VENDAS NET SALES")
            .build();
    }
}
