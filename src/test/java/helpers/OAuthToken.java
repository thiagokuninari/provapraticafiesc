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

    @JsonProperty("login")
    public String login;

    @JsonProperty("nome")
    public String nome;

    @JsonProperty("permissoes")
    public List<String> permissoes;

    @JsonProperty("error_description")
    private String errorDescription;

    @JsonProperty("alterarSenha")
    public String alterarSenha;

    @JsonProperty("nivel")
    public String nivel;

    @JsonProperty("cargo")
    public String cargo;

    @JsonProperty("departamento")
    public String departamento;
}
