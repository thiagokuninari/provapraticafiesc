package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.ECodigoObservacao;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ColaboradorInativacaoPolRequestTest {

    @Test
    public void of_deveRetornarColaboradorInativacaoPolRequest_quandoSolicitado() {
        assertThat(ColaboradorInativacaoPolRequest.of("emailteste@gmail.com", ECodigoObservacao.ITL))
            .extracting(ColaboradorInativacaoPolRequest::getEmail, ColaboradorInativacaoPolRequest::getCodigo)
            .containsExactlyInAnyOrder("emailteste@gmail.com", ECodigoObservacao.ITL);
    }
}
