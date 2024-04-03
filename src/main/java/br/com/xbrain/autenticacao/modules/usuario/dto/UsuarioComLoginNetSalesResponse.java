package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
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
    private String cpfNetSales;
    private String organizacaoEmpresaNome;
    private String codigoEquipeVenda;
    private String canalNetSales;

    public static UsuarioComLoginNetSalesResponse of(Usuario usuario) {
        return UsuarioComLoginNetSalesResponse.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .loginNetSales(usuario.getLoginNetSales())
            .nivelCodigo(getNivelCodigo(usuario))
            .razaoSocialEmpresa(CLARO_SA)
            .cpfNetSales(usuario.getCpf())
            .organizacaoEmpresaNome(usuario.getOrganizacaoEmpresa() != null
                ? usuario.getOrganizacaoEmpresa().getDescricao()
                : null)
            .codigoEquipeVenda(usuario.getCodigoEquipeVendaNetSales())
            .canalNetSales(usuario.getCanalNetSales())
            .build();
    }

    public static String getNivelCodigo(Usuario usuario) {
        if (CodigoNivel.OPERACAO.equals(usuario.getNivelCodigo())) {
            return CodigoNivel.OPERACAO.name() + "_" + usuario.getCanais().iterator().next().name();
        } else if (CodigoNivel.RECEPTIVO.equals(usuario.getNivelCodigo())) {
            return CodigoNivel.RECEPTIVO + "_" + usuario.getOrganizacaoEmpresa().getNome();
        }
        return usuario.getNivelCodigo().name();
    }

    public boolean hasLoginNetSales() {
        return !StringUtils.isEmpty(loginNetSales);
    }
}
