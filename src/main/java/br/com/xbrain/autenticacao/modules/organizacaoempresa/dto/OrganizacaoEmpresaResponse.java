package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.dto.NivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
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
    private ECanal canal;
    private String codigo;
    private String descricao;
    private NivelResponse nivel;
    private String canalDescricao;
    private ESituacaoOrganizacaoEmpresa situacao;

    public static OrganizacaoEmpresaResponse of(OrganizacaoEmpresa organizacaoEmpresa) {
        var organizacaoEmpresaResponse = new OrganizacaoEmpresaResponse();
        if (organizacaoEmpresa != null) {
            BeanUtils.copyProperties(organizacaoEmpresa, organizacaoEmpresaResponse);
            organizacaoEmpresaResponse.setNivel(organizacaoEmpresa.getNivelIdNome().orElse(null));
            organizacaoEmpresaResponse.setCanalDescricao(organizacaoEmpresa.getCanal() != null
                ? organizacaoEmpresa.getCanal().getDescricao() : null);
        }
        return organizacaoEmpresaResponse;
    }
}
