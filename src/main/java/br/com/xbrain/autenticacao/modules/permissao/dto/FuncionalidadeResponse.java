package br.com.xbrain.autenticacao.modules.permissao.dto;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import lombok.Data;

import java.util.List;

@Data
public class FuncionalidadeResponse {

    private Integer id;
    private String nome;
    private String role;
    private String aplicacao;
    private boolean especial;

    public static FuncionalidadeResponse convertFrom(Funcionalidade funcionalidade) {
        return null;
    }

    public static List<FuncionalidadeResponse> convertFrom(Iterable<Funcionalidade> funcionalidades) {
        return null;
    }

}
