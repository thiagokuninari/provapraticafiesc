package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.xbrainutils.CnpjUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoEmpresaRequest {

    @NotNull
    private String razaoSocial;
    @NotBlank
    private String cnpj;
    @NotNull
    private Integer nivelId;
    @NotEmpty
    private List<Integer> modalidadesEmpresaIds;

    private ESituacaoOrganizacaoEmpresa situacao;

    public String getCnpjSemMascara() {
        return isNotBlank(cnpj) ? CnpjUtils.getNumerosCnpj(cnpj) : "";
    }
}
