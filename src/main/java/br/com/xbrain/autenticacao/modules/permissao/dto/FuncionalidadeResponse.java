package br.com.xbrain.autenticacao.modules.permissao.dto;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FuncionalidadeResponse {

    private Integer id;
    private Integer funcionalidadeId;
    private String nome;
    private String role;
    private String aplicacao;
    private boolean especial;

    public static FuncionalidadeResponse convertFrom(Funcionalidade funcionalidade) {
        FuncionalidadeResponse funcionalidadeResponse = new FuncionalidadeResponse();
        BeanUtils.copyProperties(funcionalidade, funcionalidadeResponse);
        funcionalidadeResponse.setFuncionalidadeId(funcionalidade.getId());
        funcionalidadeResponse.setAplicacao(funcionalidade.getAplicacao().getNome());
        return funcionalidadeResponse;
    }

    public static List<FuncionalidadeResponse> convertFrom(List<Funcionalidade> funcionalidades) {
        return funcionalidades.stream().map(FuncionalidadeResponse::convertFrom).collect(Collectors.toList());
    }
}
