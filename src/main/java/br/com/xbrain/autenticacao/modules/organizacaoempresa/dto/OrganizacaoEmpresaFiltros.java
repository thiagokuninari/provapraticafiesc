package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.predicate.OrganizacaoEmpresaPredicate;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoEmpresaFiltros {

    private Integer organizacaoId;
    private String nome;
    private String descricao;
    private Integer nivelId;
    private ESituacaoOrganizacaoEmpresa situacao;
    private CodigoNivel codigoNivel;
    private String codigo;
    private ECanal canal;

    public OrganizacaoEmpresaPredicate toPredicate() {
        return new OrganizacaoEmpresaPredicate()
            .comId(organizacaoId)
            .comNome(nome)
            .comDescricao(descricao)
            .comNivel(nivelId)
            .comSituacao(situacao)
            .comCodigoNivel(codigoNivel)
            .comCodigo(codigo)
            .comECanal(canal);
    }
}
