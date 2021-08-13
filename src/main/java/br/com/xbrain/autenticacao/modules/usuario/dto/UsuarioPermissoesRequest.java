package br.com.xbrain.autenticacao.modules.usuario.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPermissoesRequest {

    public static final String ROLE_PREFIX = "ROLE_";
    @NotEmpty
    private List<Integer> usuariosId;
    @NotEmpty
    private List<String> permissoes;

    @JsonIgnore
    public List<String> getPermissoesWithoutPrefixRole() {
        return permissoes
            .stream()
            .map(permissao -> permissao.replaceAll(ROLE_PREFIX, ""))
            .collect(Collectors.toList());
    }
}
