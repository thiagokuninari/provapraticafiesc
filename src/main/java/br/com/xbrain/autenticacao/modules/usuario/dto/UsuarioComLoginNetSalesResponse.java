package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioComLoginNetSalesResponse {

    private static final String CLARO_SA = "CLARO S.A.";

    private Integer id;
    private String nome;
    private String loginNetSales;
    private String nivelCodigo;
    private String razaoSocialEmpresa;

    public static UsuarioComLoginNetSalesResponse of(Usuario usuario) {
        return UsuarioComLoginNetSalesResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .loginNetSales(usuario.getLoginNetSales())
                .nivelCodigo(usuario.getNivelCodigo().name())
                .razaoSocialEmpresa(CLARO_SA)
                .build();
    }

    public boolean hasLoginNetSales() {
        return !StringUtils.isEmpty(loginNetSales);
    }
}
