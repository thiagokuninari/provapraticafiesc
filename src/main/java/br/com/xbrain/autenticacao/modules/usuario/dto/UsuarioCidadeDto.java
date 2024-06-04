package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCidadeDto {

    private Integer idCidade;
    private String nomeCidade;
    private Integer idUf;
    private String nomeUf;
    private Integer idRegional;
    private String nomeRegional;
    private Integer fkCidade;
    private String cidadePai;

    public static UsuarioCidadeDto of(Cidade cidade) {
        return UsuarioCidadeDto.builder()
            .idCidade(cidade.getId())
            .nomeCidade(cidade.getNome())
            .idUf(cidade.getUf().getId())
            .nomeUf(cidade.getUf().getNome())
            .idRegional(cidade.getRegionalId())
            .nomeRegional(cidade.getRegionalNome())
            .fkCidade(cidade.getFkCidade())
            .build();
    }

    public static UsuarioCidadeDto of(CidadeResponse cidadeResponse) {
        return UsuarioCidadeDto.builder()
            .idCidade(cidadeResponse.getId())
            .nomeCidade(cidadeResponse.getNome())
            .idUf(cidadeResponse.getUf().getId())
            .nomeUf(cidadeResponse.getUf().getNome())
            .idRegional(cidadeResponse.getRegional().getId())
            .nomeRegional(cidadeResponse.getRegional().getNome())
            .fkCidade(cidadeResponse.getFkCidade())
            .cidadePai(cidadeResponse.getCidadePai())
            .build();
    }

    public static List<UsuarioCidadeDto> of(List<Cidade> cidades) {
        List<UsuarioCidadeDto> usuarioCidadesResponse = new ArrayList<>();
        cidades.forEach(cidade -> usuarioCidadesResponse.add(of(cidade)));

        return usuarioCidadesResponse
            .stream()
            .map(usuarioCidadeResponse -> definirNomeCidadePaiPorCidades(usuarioCidadeResponse, cidades))
            .collect(Collectors.toList());
    }

    public static UsuarioCidadeDto of(UsuarioCidade usuarioCidade) {
        return UsuarioCidadeDto.builder()
            .idCidade(usuarioCidade.getCidade().getId())
            .nomeCidade(usuarioCidade.getCidade().getNome())
            .idUf(usuarioCidade.getCidade().getUf().getId())
            .nomeUf(usuarioCidade.getCidade().getUf().getNome())
            .idRegional(usuarioCidade.getCidade().getRegional().getId())
            .nomeRegional(usuarioCidade.getCidade().getRegional().getNome())
            .fkCidade(usuarioCidade.getCidade().getFkCidade())
            .build();
    }

    public static UsuarioCidadeDto definirNomeCidadePaiPorCidades(UsuarioCidadeDto usuarioCidadeResponse,
                                                                  List<Cidade> cidades) {
        cidades
            .stream()
            .filter(cidade -> Objects.equals(cidade.getId(), usuarioCidadeResponse.getFkCidade()))
            .findFirst()
            .ifPresent(cidade -> usuarioCidadeResponse.setCidadePai(cidade.getNome()));

        return usuarioCidadeResponse;
    }

    public static List<UsuarioCidadeDto> ofCidadesResponse(List<CidadeResponse> cidadesResponse) {
        return cidadesResponse
            .stream()
            .map(UsuarioCidadeDto::of)
            .collect(Collectors.toList());
    }
}
