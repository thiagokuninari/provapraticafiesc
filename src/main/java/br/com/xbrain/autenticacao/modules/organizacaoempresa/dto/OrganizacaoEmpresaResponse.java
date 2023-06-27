package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.dto.NivelResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizacaoEmpresaResponse {

    private Integer id;
    private String nome;
    private NivelResponse nivel;
    private ESituacaoOrganizacaoEmpresa situacao;
    private String codigo;

    public static OrganizacaoEmpresaResponse of(OrganizacaoEmpresa organizacaoEmpresa) {
        var organizacaoEmpresaResponse = new OrganizacaoEmpresaResponse();
        if (organizacaoEmpresa != null) {
            BeanUtils.copyProperties(organizacaoEmpresa, organizacaoEmpresaResponse);
            organizacaoEmpresaResponse.setNome(organizacaoEmpresa.getNome());
            organizacaoEmpresaResponse.setNivel(organizacaoEmpresa.getNivelIdNome().orElse(null));
        }
        return organizacaoEmpresaResponse;
    }
}
