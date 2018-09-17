package br.com.xbrain.autenticacao.modules.importacaousuario.dto;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UsuarioImportacaoResponse {
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private List<String> motivoNaoImportacao;

    public UsuarioImportacaoResponse() {
    }

    public static List<UsuarioImportacaoResponse> convertFrom(List<UsuarioImportacaoPlanilha> models) {
        return models.stream()
                .map(UsuarioImportacaoResponse::convertFrom)
                .collect(Collectors.toList());
    }

    public static UsuarioImportacaoResponse convertFrom(UsuarioImportacaoPlanilha model) {
        UsuarioImportacaoResponse response = new UsuarioImportacaoResponse();
        BeanUtils.copyProperties(model, response);
        return response;
    }

}
