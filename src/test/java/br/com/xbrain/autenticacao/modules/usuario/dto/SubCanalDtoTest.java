package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSetSubCanais;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class SubCanalDtoTest {

    @Test
    public void of_deveRetornarResponse_quandoSolicitado() {
        var response = SubCanalDto.of(umSubCanal());

        assertThat(response).isEqualTo(
            new SubCanalDto(1, ETipoCanal.PAP, "PAP", ESituacao.A));
    }

    @Test
    public void of_deveRetornarListaResponse_quandoSolicitado() {
        var response = SubCanalDto.of(umSetSubCanais());

        assertThat(response).hasSize(2)
            .extracting("id", "codigo", "nome", "situacao")
            .containsExactlyInAnyOrder(
                tuple(1, ETipoCanal.PAP, "PAP", ESituacao.A),
                tuple(2, ETipoCanal.PAP_PME, "PAP PME", ESituacao.A));
    }
}
