package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UsuarioResponse {

    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private CodigoNivel codigoNivel;
    private CodigoDepartamento codigoDepartamento;
    private CodigoCargo codigoCargo;
    private List<CodigoUnidadeNegocio> codigoUnidadesNegocio;
    private List<CodigoEmpresa> codigoEmpresas;
    private List<String> permissoes;

    @Builder
    public UsuarioResponse(Integer id, String nome, String cpf, String email, CodigoNivel codigoNivel,
                           CodigoDepartamento codigoDepartamento, CodigoCargo codigoCargo,
                           List<CodigoUnidadeNegocio> codigoUnidadesNegocio, List<CodigoEmpresa> codigoEmpresas) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.codigoNivel = codigoNivel;
        this.codigoDepartamento = codigoDepartamento;
        this.codigoCargo = codigoCargo;
        this.codigoUnidadesNegocio = codigoUnidadesNegocio;
        this.codigoEmpresas = codigoEmpresas;
        this.permissoes = permissoes;
    }

    public static UsuarioResponse convertFrom(Usuario usuario) {
        UsuarioResponse usuarioResponse = new UsuarioResponse();
        BeanUtils.copyProperties(usuario, usuarioResponse);
        usuarioResponse.setCodigoNivel(usuario.getNivelCodigo());
        usuarioResponse.setCodigoCargo(usuario.getCargoCodigo());
        usuarioResponse.setCodigoDepartamento(usuario.getDepartamentoCodigo());
        usuarioResponse.setCodigoUnidadesNegocio(usuario.getCodigosUnidadesNegocio());
        usuarioResponse.setCodigoEmpresas(usuario.getCodigosEmpresas());
        return usuarioResponse;
    }

    public static UsuarioResponse convertFrom(Usuario usuario, List<String> permissoes) {
        UsuarioResponse usuarioResponse = new UsuarioResponse();
        BeanUtils.copyProperties(usuario, usuarioResponse);
        usuarioResponse.setCodigoNivel(usuario.getNivelCodigo());
        usuarioResponse.setCodigoCargo(usuario.getCargoCodigo());
        usuarioResponse.setCodigoDepartamento(usuario.getDepartamentoCodigo());
        usuarioResponse.setCodigoUnidadesNegocio(usuario.getCodigosUnidadesNegocio());
        usuarioResponse.setCodigoEmpresas(usuario.getCodigosEmpresas());
        usuarioResponse.setPermissoes(permissoes.stream().map(p -> "ROLE_" + p).collect(Collectors.toList()));
        return usuarioResponse;
    }

}
