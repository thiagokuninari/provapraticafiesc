package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UsuarioFiltros {

    private String nome;
    private String cpf;
    private Integer regionalId;
    private Integer grupoId;
    private Integer clusterId;
    private Integer subClusterId;

    @JsonIgnore
    public UsuarioPredicate toPredicate() {
        return new UsuarioPredicate()
                .comNome(nome)
                .comCpf(cpf != null ? cpf.replaceAll("[.-]", "") : null)
                .comRegional(regionalId)
                .comGrupo(grupoId)
                .comCluster(clusterId)
                .comSubCluster(subClusterId);
    }
}
