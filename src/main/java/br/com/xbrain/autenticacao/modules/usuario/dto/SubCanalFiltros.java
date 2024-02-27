package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.predicate.SubCanalPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCanalFiltros {

    private List<ETipoCanal> codigo;
    private String nome;
    private List<ESituacao> situacao;
    private Eboolean novaChecagemCredito;
    private Eboolean novaChecagemViabilidade;

    @JsonIgnore
    public SubCanalPredicate toPredicate() {
        return new SubCanalPredicate()
            .comCodigo(codigo)
            .comNome(nome)
            .comSituacao(situacao)
            .comNovaChecagemCredito(novaChecagemCredito)
            .comNovaChecagemViabilidade(novaChecagemViabilidade);
    }
}
