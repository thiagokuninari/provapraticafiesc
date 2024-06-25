package br.com.xbrain.autenticacao.modules.comum.model;

import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UfTest {

    @Test
    public void convertFrom_deveRetornarListaDeUfIds_quandoSolicitado() {
        assertThat(Uf.convertFrom(Set.of(umUf(1), umUf(2), umUf(56))))
            .isEqualTo(Set.of(1, 2, 56));
    }

    private Uf umUf(Integer id) {
        return Uf.builder()
            .id(id)
            .nome("Parana")
            .uf("PR")
            .build();
    }
}
