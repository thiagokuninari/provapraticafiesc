package helpers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OAuthToken {

    @JsonProperty("access_token")
    private String accessToken;

    private String login;

    private String nome;

    private String email;

    private String usuarioId;

    private List<String> authorities;

    private String errorDescription;

    private String alterarSenha;

    private String nivel;

    private String nivelCodigo;

    private String cargo;

    private String cargoCodigo;

    private String departamento;

    private String departamentoCodigo;

    private String cpf;
}
