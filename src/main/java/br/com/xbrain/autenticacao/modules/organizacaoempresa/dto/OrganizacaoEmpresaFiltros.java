package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.predicate.OrganizacaoEmpresaPredicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoEmpresaFiltros {

    private Integer id;
    private String razaoSocial;
    private String cnpj;
    private Integer nivelEmpresaId;
    private List<Integer> modalidadesEmpresaIds;
    private ESituacaoOrganizacaoEmpresa situacao;

    public OrganizacaoEmpresaPredicate toPredicate() {
        return new OrganizacaoEmpresaPredicate()
            .comId(id)
            .comRazaoSocial(razaoSocial)
            .comCnpj(cnpj)
            .comNivelEmpresa(nivelEmpresaId)
            .comModalidades(modalidadesEmpresaIds)
            .comSituacao(situacao);
    }
}
