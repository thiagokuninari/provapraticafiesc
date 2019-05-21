package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPermissoesResponse {
    public static final String ROLE_PREFIX = "ROLE_";

    private Integer usuarioId;
    private List<String> permissoes;

    public UsuarioPermissoesResponse(Integer usuarioId, String permissoes) {
        this.usuarioId = usuarioId;
        this.permissoes = Objects.nonNull(permissoes)
                ? Arrays.stream(permissoes.split(","))
                        .map(permissao -> ROLE_PREFIX + permissao)
                        .collect(Collectors.toList()) : Collections.emptyList();
    }
}
