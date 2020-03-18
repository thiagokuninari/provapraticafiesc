package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioComLoginNetSalesResponse {

    private Integer id;
    private String nome;
    private String loginNetSales;
    private String nivelCodigo;

    public static UsuarioComLoginNetSalesResponse of(Usuario usuario) {
        return UsuarioComLoginNetSalesResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .loginNetSales(usuario.getLoginNetSales())
                .nivelCodigo(usuario.getNivelCodigo().name())
                .build();
    }
}
