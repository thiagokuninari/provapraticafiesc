package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CidadeResponse {

    private Integer id;
    private String nome;
    private String codigoIbge;
    private UfResponse uf;
    private RegionalDto regional;
    private Eboolean netUno;
    private Integer fkCidade;
    private String cidadePai;

    public static CidadeResponse of(Cidade cidade) {
        var response = new CidadeResponse();
        BeanUtils.copyProperties(cidade, response);
        response.setUf(UfResponse.parse(cidade.getUf()));
        response.setRegional(RegionalDto.of(cidade.getRegional()));

        return response;
    }

    public static CidadeResponse definirNomeCidadePaiPorCidades(CidadeResponse cidadeResponse,
                                                                Map<Integer, Cidade> cidades) {
        if (cidades.containsKey(cidadeResponse.fkCidade)) {
            cidadeResponse.setCidadePai(cidades.get(cidadeResponse.fkCidade).getNome());
        }

        return cidadeResponse;
    }

    public static CidadeResponse definirNomeCidadePaiPorDistritos(CidadeResponse cidadeResponse,
                                                                  Map<Integer, CidadeResponse> distritos) {
        if (distritos.containsKey(cidadeResponse.id)) {
            cidadeResponse.setCidadePai(distritos.get(cidadeResponse.id).cidadePai);
        }

        return cidadeResponse;
    }

    @JsonIgnore
    public String getNomeComCidadePaiEUf() {
        return cidadePai != null
            ? nome + " - " + cidadePai + " - " + uf.getUf()
            : getNomeComUf();
    }

    @JsonIgnore
    public String getNomeComUf() {
        return uf != null
            ? nome + " - " + uf.getUf()
            : nome;
    }
}
