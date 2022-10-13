package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.xbrainutils.CnpjUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoEmpresaRequest {

    @NotNull
    private String nome;
    @CNPJ
    private String cnpj;
    @NotNull
    private Integer nivelId;

    private List<Integer> modalidadesEmpresaIds;
    @NotNull
    private String codigo;

    private ESituacaoOrganizacaoEmpresa situacao;

    public String getCnpjSemMascara() {
        if (cnpj != null) {
            return CnpjUtils.getNumerosCnpj(cnpj);
        }
        return null;
    }
}
