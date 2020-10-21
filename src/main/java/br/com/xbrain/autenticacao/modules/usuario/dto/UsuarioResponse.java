package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String rg;
    private String telefone;
    private String telefone02;
    private String telefone03;
    private String loginNetSales;
    private ESituacao situacao;
    private LocalDateTime dataCadastro;
    private CodigoNivel codigoNivel;
    private CodigoDepartamento codigoDepartamento;
    private String nomeCargo;
    private CodigoCargo codigoCargo;
    private List<CodigoUnidadeNegocio> codigoUnidadesNegocio;
    private List<CodigoEmpresa> codigoEmpresas;
    private List<String> permissoes;
    private LocalDateTime nascimento;

    public UsuarioResponse(Integer id, String nome, CodigoCargo codigoCargo) {
        this.id = id;
        this.nome = nome;
        this.codigoCargo = codigoCargo;
    }

    public UsuarioResponse(Integer id, String nome, String email, String nomeCargo, CodigoCargo codigoCargo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.nomeCargo = nomeCargo;
        this.codigoCargo = codigoCargo;
    }

    public static UsuarioResponse of(Usuario usuario) {
        UsuarioResponse usuarioResponse = new UsuarioResponse();
        BeanUtils.copyProperties(usuario, usuarioResponse);
        usuarioResponse.setCodigoNivel(usuario.getNivelCodigo());
        usuarioResponse.setCodigoCargo(usuario.getCargoCodigo());
        usuarioResponse.setCodigoDepartamento(usuario.getDepartamentoCodigo());
        usuarioResponse.setCodigoUnidadesNegocio(usuario.getCodigosUnidadesNegocio());
        usuarioResponse.setCodigoEmpresas(usuario.getCodigosEmpresas());
        usuarioResponse.setLoginNetSales(usuario.getLoginNetSales());
        return usuarioResponse;
    }

    public static UsuarioResponse of(Usuario usuario, List<String> permissoes) {
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
