package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteResponse {

    private Integer id;
    private String nome;
    private ETimeZone timeZone;
    private ESituacao situacao;
    private Set<Integer> coordenadoresIds;
    private Set<Integer> supervisoresIds;
    private Set<Integer> estadosIds;
    private Set<Integer> cidadesIds;
    private Integer discadoraId;
    private boolean siteNacional;

    public static SiteResponse of(Site site, boolean trazerTudo) {
        var siteResponse = new SiteResponse();
        BeanUtils.copyProperties(site, siteResponse, "siteNacional");
        siteResponse.setSiteNacional(site.getSiteNacional() == Eboolean.V);

        if (trazerTudo) {
            siteResponse.setCoordenadoresIds(Usuario.convertFrom(site.getCoordenadores()));
            siteResponse.setSupervisoresIds(Usuario.convertFrom(site.getSupervisores()));
            siteResponse.setEstadosIds(Uf.convertFrom(site.getEstados()));
            siteResponse.setCidadesIds(Cidade.convertFrom(site.getCidades()));
        }

        return siteResponse;
    }

    public static SiteResponse of(Site site) {
        return of(site, false);
    }
}
