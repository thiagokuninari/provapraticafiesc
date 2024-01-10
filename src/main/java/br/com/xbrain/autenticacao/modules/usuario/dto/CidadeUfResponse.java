package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CidadeUfResponse {

    private Integer cidadeId;
    private String cidade;
    private String uf;
    private String ufSigla;
    private Integer ufId;
    private Integer fkCidade;
    private String cidadePai;
    private String logradouro;
    private String bairro;
    private Eboolean cepUnicoPorCidade;

    public static CidadeUfResponse of(Cidade cidade) {
        return CidadeUfResponse.builder()
            .cidadeId(cidade.getId())
            .cidade(cidade.getNome())
            .uf(cidade.getNomeUf())
            .ufSigla(cidade.getUf().getUf())
            .ufId(cidade.getIdUf())
            .fkCidade(cidade.getFkCidade())
            .build();
    }

    public static CidadeUfResponse definirNomeCidadePai(CidadeUfResponse cidadeUfResponse, List<Cidade> cidades) {
        cidades
            .stream()
            .filter(cidade -> Objects.equals(cidade.getId(), cidadeUfResponse.getFkCidade()))
            .findFirst()
            .ifPresent(cidade -> cidadeUfResponse.setCidadePai(cidade.getNome()));

        return cidadeUfResponse;
    }
}
