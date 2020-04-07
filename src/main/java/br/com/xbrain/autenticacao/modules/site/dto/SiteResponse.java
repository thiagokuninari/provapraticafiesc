package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SiteResponse {

    private Integer id;
    private String nome;
    private ETimeZone timeZone;
    private Set<Integer> coordenadoresIds;
    private Set<Integer> supervisoresIds;
    private Set<Integer> cidadesIds;

    public static SiteResponse of(Site site) {
        return SiteResponse.builder()
                .id(site.getId())
                .nome(site.getNome())
                .timeZone(site.getTimeZone())
                .coordenadoresIds(Usuario.convertFrom(site.getCoordenadores()))
                .supervisoresIds(Usuario.convertFrom(site.getSupervisores()))
                .cidadesIds(Cidade.convertFrom(site.getCidades()))
                .build();
    }
}
