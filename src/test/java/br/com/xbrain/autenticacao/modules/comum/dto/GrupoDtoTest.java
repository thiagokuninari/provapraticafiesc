package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Grupo;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class GrupoDtoTest {

    @Test
    public void converterParaListaDto_deveRetornarListaGrupoDto_quandoSolicitado() {
        assertThat(GrupoDto.converterParaListaDto(List.of(umGrupo(1), umGrupo(2))))
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "grupo"),
                tuple(2, "grupo"));
    }

    private Grupo umGrupo(Integer id) {
        return Grupo.builder()
            .id(id)
            .nome("grupo")
            .build();
    }
}
