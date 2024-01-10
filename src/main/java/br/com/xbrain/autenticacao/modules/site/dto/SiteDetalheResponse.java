package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UfResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteDetalheResponse {

    private Integer id;
    private String nome;
    private ETimeZone timeZone;
    private ESituacao situacao;
    private Set<String> coordenadoresNomes;
    private Set<String> supervisoresNomes;
    private Set<UfResponse> estados;
    private Set<CidadeResponse> cidades;

    public static SiteDetalheResponse of(Site site) {
        return SiteDetalheResponse.builder()
            .id(site.getId())
            .nome(site.getNome())
            .timeZone(site.getTimeZone())
            .situacao(site.getSituacao())
            .coordenadoresNomes(getNomes(site.getCoordenadores()))
            .supervisoresNomes(getNomes(site.getSupervisores()))
            .estados(getEstados(site.getEstados()))
            .build();
    }

    private static Set<String> getNomes(Set<Usuario> usuarios) {
        return usuarios
            .stream()
            .map(Usuario::getNome)
            .collect(Collectors.toSet());
    }

    private static Set<UfResponse> getEstados(Set<Uf> estados) {
        return estados
            .stream()
            .map(UfResponse::parse)
            .collect(Collectors.toSet());
    }
}
