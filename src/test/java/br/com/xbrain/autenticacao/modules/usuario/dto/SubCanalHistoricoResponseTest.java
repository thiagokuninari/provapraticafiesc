package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.EAcao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanalHistorico;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

public class SubCanalHistoricoResponseTest {

    @Test
    public void of_deveConverterParaDto_quandoReceberObjeto() {
        var subCanalHistorico = SubCanalHistorico.builder()
            .id(1)
            .codigo(ETipoCanal.PAP_PREMIUM)
            .nome("PAP PREMIUM")
            .situacao(ESituacao.A)
            .novaChecagemCreditoAntiga(Eboolean.V)
            .novaChecagemViabilidadeAntiga(Eboolean.V)
            .novaChecagemCreditoNova(Eboolean.F)
            .novaChecagemCreditoAntiga(Eboolean.F)
            .acao(EAcao.ATUALIZACAO)
            .dataAcao(LocalDateTime.now())
            .usuarioAcaoId(123)
            .usuarioAcaoNome("Teste")
            .build();

        var response = SubCanalHistoricoResponse.of(subCanalHistorico);

        Assert.assertEquals(subCanalHistorico.getId(), response.getId());
        Assert.assertEquals(subCanalHistorico.getCodigo(), response.getCodigo());
        Assert.assertEquals(subCanalHistorico.getSituacao(), response.getSituacao());
        Assert.assertEquals(subCanalHistorico.getNovaChecagemCreditoAntiga(), response.getNovaChecagemCreditoAntiga());
        Assert.assertEquals(subCanalHistorico.getNovaChecagemViabilidadeAntiga(), response.getNovaChecagemViabilidadeAntiga());
        Assert.assertEquals(subCanalHistorico.getNovaChecagemCreditoNova(), response.getNovaChecagemCreditoNova());
        Assert.assertEquals(subCanalHistorico.getNovaChecagemViabilidadeNova(), response.getNovaChecagemViabilidadeNova());
        Assert.assertEquals(subCanalHistorico.getAcao(), response.getAcao());
        Assert.assertEquals(subCanalHistorico.getDataAcao(), response.getDataAcao());
        Assert.assertEquals(subCanalHistorico.getUsuarioAcaoId(), response.getUsuarioAcaoId());
        Assert.assertEquals(subCanalHistorico.getUsuarioAcaoNome(), response.getUsuarioAcaoNome());
    }

}
