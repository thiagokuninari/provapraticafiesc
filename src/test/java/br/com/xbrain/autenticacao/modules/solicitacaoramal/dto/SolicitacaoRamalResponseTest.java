package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.AgenteAutorizadoResponse;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SolicitacaoRamalResponseTest {

    @Test
    public void convertFrom_deveRetornarDadosAdicionaisResponse_seParametrosD2dCorretos() {
        var atual = SolicitacaoRamalDadosAdicionaisResponse
            .convertFrom("nome discadora", 2);

        var esperado = SolicitacaoRamalDadosAdicionaisResponse
            .builder()
            .nome("nome discadora")
            .discadoraId(2)
            .build();

        assertThat(atual)
            .isEqualToComparingFieldByField(esperado);
    }

    @Test
    public void convertFrom_deveRetornarDadosAdicionaisResponse_seParametrosAaCorretos() {
        var atual = SolicitacaoRamalDadosAdicionaisResponse
            .convertFrom("nome discadora", "nome sócio", 5, 4,
                AgenteAutorizadoResponse.builder().razaoSocial("teste").build());

        var esperado = SolicitacaoRamalDadosAdicionaisResponse
            .builder()
            .discadora("nome discadora")
            .socioPrincipal("nome sócio")
            .usuariosAtivos(5)
            .quantidadeRamais(4)
            .agenteAutorizadoRazaoSocial("teste")
            .build();

        assertThat(atual)
            .isEqualToComparingFieldByField(esperado);
    }
}
