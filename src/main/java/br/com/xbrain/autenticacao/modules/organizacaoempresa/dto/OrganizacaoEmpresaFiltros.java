package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.predicate.OrganizacaoEmpresaPredicate;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
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

    private Integer organizacaoId;
    private String nome;
    private String cnpj;
    private Integer nivelId;
    private List<Integer> modalidadesEmpresaIds;
    private ESituacaoOrganizacaoEmpresa situacao;
    private CodigoNivel codigoNivel;
    private String codigo;

    public OrganizacaoEmpresaPredicate toPredicate() {
        return new OrganizacaoEmpresaPredicate()
            .comId(organizacaoId)
            .comNome(nome)
            .comCnpj(cnpj)
            .comNivel(nivelId)
            .comModalidades(modalidadesEmpresaIds)
            .comSituacao(situacao)
            .comCodigoNivel(codigoNivel)
            .comCodigo(codigo);
    }
}
