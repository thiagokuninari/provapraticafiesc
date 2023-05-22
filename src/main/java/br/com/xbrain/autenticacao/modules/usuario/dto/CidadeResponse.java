package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    public static CidadeResponse definirNomeCidadePaiPorCidades(CidadeResponse cidadeResponse, List<Cidade> cidades) {
        cidades
            .stream()
            .filter(cidade -> Objects.equals(cidade.getId(), cidadeResponse.getFkCidade()))
            .findFirst()
            .ifPresent(cidade -> cidadeResponse.setCidadePai(cidade.getNome()));

        return cidadeResponse;
    }

    public static CidadeResponse definirNomeCidadePaiPorUsuarioCidades(CidadeResponse cidadeResponse,
                                                                       Set<UsuarioCidade> usuarioCidades) {
        usuarioCidades
            .stream()
            .filter(usuarioCidade -> Objects.equals(usuarioCidade.getCidade().getId(), cidadeResponse.getFkCidade()))
            .findFirst()
            .ifPresent(usuarioCidade -> cidadeResponse.setCidadePai(usuarioCidade.getCidade().getNome()));

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
