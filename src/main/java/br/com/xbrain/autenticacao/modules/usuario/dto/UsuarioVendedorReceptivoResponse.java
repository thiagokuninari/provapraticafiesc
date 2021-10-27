package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Optional;

@Data
public class UsuarioVendedorReceptivoResponse {
    private Integer id;
    private String nome;
    private String email;
    private String loginNetSales;
    private String nivel;
    private String organizacao;

    public static UsuarioVendedorReceptivoResponse of(Usuario usuario) {
        var response = new UsuarioVendedorReceptivoResponse();
        BeanUtils.copyProperties(usuario, response);
        response.setNivel(usuario.getNivelNome());
        response.setOrganizacao(Optional.ofNullable(usuario.getOrganizacao())
            .map(Organizacao::getNome)
            .orElse(null));
        return response;
    }
}
