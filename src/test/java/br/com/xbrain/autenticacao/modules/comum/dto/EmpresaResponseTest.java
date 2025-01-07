package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa.CLARO_RESIDENCIAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EmpresaResponseTest {

    @Test
    public void convertFrom_deveRetornarEmpresaResponse_quandoSolicitado() {
        assertThat(EmpresaResponse.convertFrom(umaEmpresa()))
            .extracting("id", "nome", "codigo")
            .containsExactly(1, "CLARO_RESIDENCIAL", CLARO_RESIDENCIAL);
    }

    private Empresa umaEmpresa() {
        return Empresa.builder()
            .id(1)
            .codigo(CLARO_RESIDENCIAL)
            .nome(CLARO_RESIDENCIAL.name())
            .build();
    }
}
