package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanalDto;
import static org.assertj.core.api.Assertions.assertThat;

public class SubCanalTest {

    @Test
    public void editar_deveEditarDadosDoSubCanal_quandoOk() {
        var subCanal = umSubCanal();
        var dto = umSubCanalDto(2, ETipoCanal.PAP_PREMIUM, "Um Outro Nome");
        dto.setSituacao(ESituacao.I);
        dto.setNovaChecagemCredito(Eboolean.V);
        subCanal.editar(dto);
        assertThat(subCanal)
            .extracting("id", "codigo", "nome", "situacao", "novaChecagemCredito")
            .containsExactly(1, ETipoCanal.PAP_PREMIUM, "Um Outro Nome", ESituacao.I, Eboolean.V);
    }

}
