package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UsuarioFiltros {

    private String nome;
    private String cpf;

    @JsonIgnore
    public UsuarioPredicate toPredicate() {
        return new UsuarioPredicate()
                .comNome(nome)
                .comCpf(cpf != null ? cpf.replaceAll("[.-]", "") : null);
    }
}
