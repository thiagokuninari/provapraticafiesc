package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Grupo;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GrupoDto {

    private Integer id;
    private String nome;

    public GrupoDto(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static List<GrupoDto> converterParaListaDto(Iterable<Grupo> grupoList) {
        return Lists.newArrayList(grupoList)
                .stream()
                .map(c -> new GrupoDto(c.getId(), c.getNome()))
                .collect(Collectors.toList());
    }
}
