package br.com.xbrain.autenticacao.modules.usuario.dto;

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

    public UsuarioImportacaoResponse(UsuarioImportacaoRequest usuario) {
        BeanUtils.copyProperties(convertFrom(usuario), this);
    }

    public static List<UsuarioImportacaoResponse> convertFrom(List<UsuarioImportacaoRequest> models) {
        return models.stream()
                .map(UsuarioImportacaoResponse::convertFrom)
                .collect(Collectors.toList());
    }

    public static UsuarioImportacaoResponse convertFrom(UsuarioImportacaoRequest model) {
        UsuarioImportacaoResponse response = new UsuarioImportacaoResponse();
        BeanUtils.copyProperties(model, response);
        return response;
    }

}
