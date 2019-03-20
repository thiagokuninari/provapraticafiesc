package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UsuarioPermissaoCanal {

    private String permissao;
    private List<String> canais;

    public static UsuarioPermissaoCanal of(Funcionalidade funcionalidade) {
        return UsuarioPermissaoCanal
                .builder()
                .permissao("ROLE_" + funcionalidade.getRole())
                .canais(
                        funcionalidade
                                .getCanais()
                                .stream()
                                .map(c -> c.getCanal().name())
                                .collect(Collectors.toList()))
                .build();
    }
}
