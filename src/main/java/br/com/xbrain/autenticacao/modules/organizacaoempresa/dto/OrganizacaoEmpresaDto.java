package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizacaoEmpresaDto {

    private Integer id;
    private String nome;
    private String codigo;
    private ESituacaoOrganizacaoEmpresa situacao;

    public static OrganizacaoEmpresaDto of(OrganizacaoEmpresa organizacaoEmpresa) {
        var organizacaoEmpresaDto = new OrganizacaoEmpresaDto();
        if (organizacaoEmpresa != null) {
            BeanUtils.copyProperties(organizacaoEmpresa, organizacaoEmpresaDto);
            organizacaoEmpresaDto.setNome(organizacaoEmpresa.getNome());
            organizacaoEmpresaDto.setSituacao(ESituacaoOrganizacaoEmpresa.A);
        }
        return organizacaoEmpresaDto;
    }
}
