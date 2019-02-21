package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SolicitacaoRamalColaboradorResponse {

    private Integer id;
    private String nome;
    private String cargo;

    public static SolicitacaoRamalColaboradorResponse convertFrom(Usuario usuario) {
        SolicitacaoRamalColaboradorResponse response = new SolicitacaoRamalColaboradorResponse();
        BeanUtils.copyProperties(usuario, response);

        response.cargo = usuario.getCargo().getNome();

        return response;
    }

}
