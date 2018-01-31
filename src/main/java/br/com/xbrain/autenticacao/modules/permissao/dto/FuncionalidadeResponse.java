package br.com.xbrain.autenticacao.modules.permissao.dto;

import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class FuncionalidadeResponse {

    private Integer id;
    private String nome;
    private String role;
    private String aplicacao;
    private boolean especial;

    private static FuncionalidadeResponse convertFrom(Funcionalidade funcionalidade) {
        FuncionalidadeResponse funcionalidadeResponse = new FuncionalidadeResponse();
        BeanUtils.copyProperties(funcionalidade, funcionalidadeResponse);
        funcionalidadeResponse.setAplicacao(funcionalidade.getAplicacao().getNome());
        return funcionalidadeResponse;
    }

    public static List<FuncionalidadeResponse> convertFrom(List<Funcionalidade> funcionalidades) {
        return funcionalidades.stream().map(f -> convertFrom(f)).collect(Collectors.toList());
    }

    public static List<FuncionalidadeResponse> convertFromCargoDepartamentoFuncionalidade(
            List<CargoDepartamentoFuncionalidade> funcionalidades) {
        return funcionalidades.stream().map(f -> convertFrom(f.getFuncionalidade())).collect(Collectors.toList());
    }

}
