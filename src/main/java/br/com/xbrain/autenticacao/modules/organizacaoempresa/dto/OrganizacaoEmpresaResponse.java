package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.dto.NivelResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizacaoEmpresaResponse {

    private Integer id;
    private String razaoSocial;
    private String cnpj;
    private NivelResponse nivel;
    private List<SelectResponse> modalidadesEmpresa;
    private ESituacaoOrganizacaoEmpresa situacao;
    private String codigo;

    public static OrganizacaoEmpresaResponse of(OrganizacaoEmpresa organizacaoEmpresa) {
        var organizacaoEmpresaResponse = new OrganizacaoEmpresaResponse();
        if (organizacaoEmpresa != null) {
            BeanUtils.copyProperties(organizacaoEmpresa, organizacaoEmpresaResponse);
            organizacaoEmpresaResponse.setCnpj(organizacaoEmpresa.formataCnpj());
            organizacaoEmpresaResponse.setNivel(organizacaoEmpresa.getNivelIdNome().orElse(null));
            organizacaoEmpresaResponse.setModalidadesEmpresa(organizacaoEmpresa.getModalidadesEmpresaIdNome());
        }
        return organizacaoEmpresaResponse;
    }
}
