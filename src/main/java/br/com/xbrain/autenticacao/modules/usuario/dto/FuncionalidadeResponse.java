package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class FuncionalidadeResponse {

    private Integer id;

    private String nome;

    private String role;

    private boolean especial;

    public static FuncionalidadeResponse convertFrom(Funcionalidade funcionalidade) {
        FuncionalidadeResponse funcionalidadeResponse = new FuncionalidadeResponse();
        BeanUtils.copyProperties(funcionalidade, funcionalidadeResponse);
        return funcionalidadeResponse;
    }

}
