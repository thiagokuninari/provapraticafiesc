package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanalInativoCompletDto;
import static org.assertj.core.api.Assertions.assertThat;

public class SubCanalTest {

    @Test
    public void editar_deveEditarDadosDoSubCanal_quandoOk() {
        var dto = umSubCanalInativoCompletDto(2, ETipoCanal.PAP_PREMIUM, "Um Outro Nome");
        final var subCanal = umSubCanal();
        dto.setNovaChecagemCredito(Eboolean.V);
        dto.setNovaChecagemViabilidade(Eboolean.V);
        dto.setRealizarEnriquecimentoEnd(Eboolean.V);
        subCanal.editar(dto);
        assertThat(subCanal)
            .extracting("id", "codigo", "nome", "situacao", "novaChecagemCredito", "novaChecagemViabilidade",
                "realizarEnriquecimentoEnd")
            .containsExactly(1, ETipoCanal.PAP_PREMIUM, "Um Outro Nome", ESituacao.I, Eboolean.V, Eboolean.V,
                Eboolean.V);
    }
}
