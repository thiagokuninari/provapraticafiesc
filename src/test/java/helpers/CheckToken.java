package helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Data
public class CheckToken {

    @JsonProperty("user_name")
    private String username = "user_name";

    private String[] authorities = {};

    @JsonProperty("client_id")
    private String clientId = "xbrain-app-client";

    private String scope = "app";

    private String jti = "fake";

    private Integer usuarioId;

    private String cpf;

    private String nome;

    private String email;

    private String cargoCodigo;

    private String nivelCodigo;

    private List<Integer> agentesAutorizados;

    public static CheckToken create() {
        return create(100, "ADMIN@XBRAIN.COM.BR");
    }

    public static CheckToken create(Integer id, String email) {
        CheckToken checkToken = new CheckToken();
        checkToken.setUsername(id + "-" + email);
        checkToken.setUsuarioId(id);
        checkToken.setEmail(email);
        return checkToken;
    }

    public CheckToken comPermissoes(String... permissoes) {
        this.setAuthorities(permissoes);
        return this;
    }

    public CheckToken comCpf(String cpf) {
        this.setCpf(cpf);
        return this;
    }

    public CheckToken comNome(String nome) {
        this.setNome(nome);
        return this;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user_name", username);
        hashMap.put("authorities", Arrays.asList(authorities));
        hashMap.put("jti", jti);
        hashMap.put("scope", scope);
        hashMap.put("client_id", clientId);
        hashMap.put("usuarioId", usuarioId);
        hashMap.put("cpf", cpf);
        hashMap.put("nome", nome);
        hashMap.put("email", email);
        hashMap.put("cargoCodigo", cargoCodigo);
        hashMap.put("nivelCodigo", nivelCodigo);
        hashMap.put("agentesAutorizados", agentesAutorizados);
        return hashMap;
    }
}
