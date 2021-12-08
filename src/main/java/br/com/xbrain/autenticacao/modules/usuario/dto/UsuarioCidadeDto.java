package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCidadeDto {

    private Integer idCidade;
    private String nomeCidade;
    private Integer idUf;
    private String nomeUf;
    private Integer idRegional;
    private String nomeRegional;

    public static UsuarioCidadeDto of(Cidade cidade) {
        UsuarioCidadeDto dto = new UsuarioCidadeDto();
        dto.setIdCidade(cidade.getId());
        dto.setNomeCidade(cidade.getNome());
        dto.setIdUf(cidade.getUf().getId());
        dto.setNomeUf(cidade.getUf().getNome());
        dto.setIdRegional(cidade.getRegionalId());
        dto.setNomeRegional(cidade.getRegionalNome());
        return dto;
    }

    public static List<UsuarioCidadeDto> of(List<Cidade> cidades) {
        List<UsuarioCidadeDto> dtos = new ArrayList<>();
        cidades.forEach(c -> dtos.add(of(c)));
        return dtos;
    }

}
