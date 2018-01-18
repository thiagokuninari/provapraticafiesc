package helpers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OAuthToken {

    @JsonProperty("access_token")
    public String accessToken;

    public String login;

    public String nome;

    public String email;

    public String usuarioId;

    public List<String> authorities;

    private String errorDescription;

    public String alterarSenha;

    public String nivel;

    public String nivelCodigo;

    public String cargo;

    public String cargoCodigo;

    public String departamento;

    public String departamentoCodigo;
}
