package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.util.ObjectUtil;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private static final int COLUNA_USUARIO_ID = 0;
    @JsonIgnore
    private static final int COLUNA_USUARIO_NOME = 1;
    @JsonIgnore
    private static final int COLUNA_CARGO_CODIGO = 4;

    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String telefone02;
    private String telefone03;
    private ESituacao situacao;
    private LocalDateTime dataCadastro;
    private CodigoNivel codigoNivel;
    private CodigoDepartamento codigoDepartamento;
    private String nomeCargo;
    private CodigoCargo codigoCargo;
    private List<CodigoUnidadeNegocio> codigoUnidadesNegocio;
    private List<CodigoEmpresa> codigoEmpresas;
    private List<String> permissoes;

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

    public static UsuarioResponse ofUsuarioObjectArray(Object[] usuarioObject) {
        return UsuarioResponse
            .builder()
            .id(ObjectUtil.toInteger(usuarioObject[COLUNA_USUARIO_ID]))
            .nome(ObjectUtil.toString(usuarioObject[COLUNA_USUARIO_NOME]))
            .codigoCargo(CodigoCargo.valueOf(ObjectUtil.toString(usuarioObject[COLUNA_CARGO_CODIGO])))
            .build();
    }
}
