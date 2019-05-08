package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CidadeSubClusterResponse {
    Integer idCidade;
    Integer idSubcluster;
    Integer idUf;
    String nomeCidade;
    String nomeUf;

    public static CidadeSubClusterResponse parse(Cidade cidade) {
        return CidadeSubClusterResponse.builder()
                .idCidade(cidade.getId())
                .idSubcluster(cidade.getSubClusterId())
                .idUf(cidade.getIdUf())
                .nomeCidade(cidade.getNome())
                .nomeUf(cidade.getNomeUf()).build();
    }
}
