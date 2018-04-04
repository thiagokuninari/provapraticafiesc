package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UsuarioCidadeDto {

    private Integer idRegional;
    private String nomeRegional;
    private Integer idGrupo;
    private String nomeGrupo;
    private Integer idCluster;
    private String nomeCluster;
    private Integer idSubCluster;
    private String nomeSubCluster;
    private Integer idCidade;
    private String nomeCidade;

    public static UsuarioCidadeDto parse(Cidade cidade) {
        UsuarioCidadeDto dto = new UsuarioCidadeDto();
        dto.setIdCidade(cidade.getId());
        dto.setNomeCidade(cidade.getNome());
        dto.setIdSubCluster(cidade.getSubClusterId());
        dto.setNomeSubCluster(cidade.getSubClusterNome());
        dto.setIdCluster(cidade.getClusterId());
        dto.setNomeCluster(cidade.getClusterNome());
        dto.setIdGrupo(cidade.getGrupoId());
        dto.setNomeGrupo(cidade.getGrupoNome());
        dto.setIdRegional(cidade.getRegionalId());
        dto.setNomeRegional(cidade.getRegionalNome());
        return dto;
    }

    public static List<UsuarioCidadeDto> parse(List<Cidade> cidades) {
        List<UsuarioCidadeDto> dtos = new ArrayList<>();
        cidades.forEach(c -> dtos.add(parse(c)));
        return dtos;
    }

}
