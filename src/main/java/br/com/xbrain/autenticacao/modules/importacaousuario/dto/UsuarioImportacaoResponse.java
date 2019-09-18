package br.com.xbrain.autenticacao.modules.importacaousuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UsuarioImportacaoResponse {
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private List<String> motivoNaoImportacao;
    private String usuarioImportadoComSucesso;
    private Departamento departamento;
    private Nivel nivel;
    private Cargo cargo;
    private LocalDateTime nascimento;

    public static List<UsuarioImportacaoResponse> of(List<UsuarioImportacaoPlanilha> models) {
        return models.stream()
                .map(UsuarioImportacaoResponse::of)
                .collect(Collectors.toList());
    }

    public static UsuarioImportacaoResponse of(UsuarioImportacaoPlanilha model) {
        UsuarioImportacaoResponse response = new UsuarioImportacaoResponse();
        response.setUsuarioImportadoComSucesso(ObjectUtils.isEmpty(model.getMotivoNaoImportacao()) ? "SIM" : "NÃO");
        BeanUtils.copyProperties(model, response);
        return response;
    }
}
