package br.com.xbrain.autenticacao.modules.usuario.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPermissoesResponse {

    public static final String ROLE_PREFIX = "ROLE_";

    private Integer usuarioId;
    private List<String> permissoes;

    public UsuarioPermissoesResponse(Integer usuarioId, String permissoes, String permissoesEspeciais) {
        this.usuarioId = usuarioId;
        this.permissoes = Arrays.stream(getAllPermissoes(permissoes, permissoesEspeciais))
                .map(perm -> ROLE_PREFIX + perm)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    private static String[] getAllPermissoes(String permissoes, String permissoesEspeciais) {
        return Stream.of(permissoes, permissoesEspeciais)
                .filter(StringUtils::isNotBlank)
                .map(s -> s.split(","))
                .reduce(org.springframework.util.StringUtils::mergeStringArrays)
                .orElse(ArrayUtils.EMPTY_STRING_ARRAY);
    }
}
