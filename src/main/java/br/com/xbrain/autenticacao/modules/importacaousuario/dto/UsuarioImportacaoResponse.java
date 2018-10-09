package br.com.xbrain.autenticacao.modules.importacaousuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UsuarioImportacaoResponse {
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private List<String> motivoNaoImportacao;
    private Departamento departamento;
    private Nivel nivel;
    private Cargo cargo;
    private LocalDateTime nascimento;


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
