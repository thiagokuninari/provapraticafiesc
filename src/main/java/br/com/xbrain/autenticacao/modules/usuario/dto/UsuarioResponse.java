package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private String nomeNivel;
    private CodigoDepartamento codigoDepartamento;
    private String nomeCargo;
    private CodigoCargo codigoCargo;
    private List<CodigoUnidadeNegocio> codigoUnidadesNegocio;
    private List<CodigoEmpresa> codigoEmpresas;
    private List<String> permissoes;
    private LocalDateTime nascimento;
    private Integer aaId;
    private Set<ECanal> canais;
    private Set<SubCanalDto> subCanais;
    private LocalDateTime dataSaidaCnpj;
    private Integer organizacaoEmpresaId;
    private String organizacaoEmpresaNome;

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

    public UsuarioResponse(Integer id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public static UsuarioResponse of(Usuario usuario) {
        UsuarioResponse usuarioResponse = new UsuarioResponse();
        if (usuario != null) {
            BeanUtils.copyProperties(usuario, usuarioResponse);
            usuarioResponse.setCodigoNivel(usuario.getNivelCodigo());
            usuarioResponse.setNomeNivel(usuario.getNivelNome());
            usuarioResponse.setCodigoCargo(usuario.getCargoCodigo());
            usuarioResponse.setCodigoDepartamento(usuario.getDepartamentoCodigo());
            usuarioResponse.setCodigoUnidadesNegocio(usuario.getCodigosUnidadesNegocio());
            usuarioResponse.setCodigoEmpresas(usuario.getCodigosEmpresas());
            usuarioResponse.setAaId(usuario.getAgenteAutorizadoId());
            usuarioResponse.setCanais(!ObjectUtils.isEmpty(usuario.getCanais())
                ? usuario.getCanais() : Collections.emptySet());
            usuarioResponse.setSituacao(usuario.getSituacao());
            usuarioResponse.setSubCanais(!ObjectUtils.isEmpty(usuario.getSubCanais())
                ? usuario.getSubCanais()
                .stream()
                .map(SubCanalDto::of)
                .collect(Collectors.toSet())
                : null);
            Optional.ofNullable(usuario.getOrganizacaoEmpresa())
                .ifPresent(organizacaoEmpresa -> {
                    usuarioResponse.setOrganizacaoEmpresaId(organizacaoEmpresa.getId());
                    usuarioResponse.setOrganizacaoEmpresaNome(organizacaoEmpresa.getDescricao());
                });
        }
        return usuarioResponse;
    }

    public static UsuarioResponse of(Usuario usuario, List<String> permissoes) {
        var usuarioResponse = of(usuario);
        usuarioResponse.setNomeCargo(usuario.getCargo().getNome());
        usuarioResponse.setPermissoes(permissoes.stream().map(p -> "ROLE_" + p).collect(Collectors.toList()));

        return usuarioResponse;
    }
}
