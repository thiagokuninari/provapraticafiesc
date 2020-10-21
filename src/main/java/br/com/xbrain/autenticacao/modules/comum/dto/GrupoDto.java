package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Grupo;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoDto {

    private Integer id;
    private String nome;
    private RegionalDto regional;

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

    public static GrupoDto of(Grupo grupo) {
        GrupoDto dto = new GrupoDto();
        BeanUtils.copyProperties(grupo, dto);
        dto.setRegional(getRegional(grupo));
        return dto;
    }

    private static RegionalDto getRegional(Grupo grupo) {
        return Objects.nonNull(grupo.getRegional())
            ? RegionalDto.of(grupo.getRegional())
            : null;
    }
}
