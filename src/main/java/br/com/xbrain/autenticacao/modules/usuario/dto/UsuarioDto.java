package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UsuarioDto implements Serializable {

    public static final Integer ID_NIVEL_MSO = 2;

    private Integer id;
    @NotNull
    @Size(max = 80)
    private String nome;
    @NotNull
    @Size(max = 80)
    private String email;
    @Size(max = 80)
    private String email02;
    @Size(max = 100)
    private String telefone;
    private String telefone02;
    private String telefone03;
    private Long imei;
    @Size(max = 14)
    private String cpf;
    @Size(max = 25)
    private String rg;
    @Size(max = 30)
    private String orgaoExpedidor;
    @Size(max = 120)
    @NotBlank
    private String loginNetSales;
    @Size(max = 120)
    private String nomeEquipeVendaNetSales;
    @Size(max = 120)
    private String codigoEquipeVendaNetSales;
    private LocalDateTime nascimento;
    @NotEmpty
    private List<Integer> unidadesNegociosId = new ArrayList<>();
    private Integer unidadeNegocioId;
    private Integer nivelId;
    private CodigoNivel nivelCodigo;
    @NotEmpty
    private List<Integer> empresasId = new ArrayList<>();
    @NotNull
    private Integer cargoId;
    private CodigoCargo cargoCodigo;
    private Integer cargoQuantidadeSuperior;
    private boolean possuiCargoSuperior;
    @NotNull
    private Integer departamentoId;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataInativacao;
    @Enumerated(EnumType.STRING)
    private Eboolean alterarSenha;
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;
    private Integer usuarioCadastroId;
    private String usuarioCadastroNome;
    private List<Integer> hierarquiasId;
    private List<Integer> cidadesId;
    private Integer recuperarSenhaTentativa = 0;
    private Set<ECanal> canais;
    private Set<Integer> subCanaisId;
    private String fotoDiretorio;
    private String fotoNomeOriginal;
    private String fotoContentType;
    private Integer organizacaoId;
    private Integer organizacaoEmpresaId;
    private boolean permiteEditarCompleto;
    private Integer agenteAutorizadoId;
    private String urlLojaBase;
    private String urlLojaProspect;
    private String urlLojaProspectNextel;
    private String cupomLoja;
    private Integer siteId;
    private Set<ETipoFeederMso> tiposFeeder;

    public UsuarioDto(Integer id) {
        this.id = id;
    }

    public static Usuario convertFrom(UsuarioDto usuarioDto) {
        var usuario = new Usuario();
        BeanUtils.copyProperties(usuarioDto, usuario);
        usuario.setEmpresasId(usuarioDto.getEmpresasId());
        usuario.setUnidadesNegociosId(usuarioDto.getUnidadesNegociosId());
        usuario.setCargo(new Cargo(usuarioDto.getCargoId()));
        usuario.getCargo().setCodigo(usuarioDto.getCargoCodigo());
        usuario.getCargo().setCodigo(usuarioDto.getCargoCodigo());
        Optional.ofNullable(usuarioDto.getNivelId()).ifPresent(user -> {
            usuario.getCargo().setNivel(new Nivel(usuarioDto.getNivelId()));
            usuario.getCargo().getNivel().setCodigo(usuarioDto.getNivelCodigo());
        });
        usuario.setDepartamento(new Departamento(usuarioDto.getDepartamentoId()));
        if (!isEmpty(usuarioDto.getOrganizacaoId())) {
            usuario.setOrganizacao(new Organizacao(usuarioDto.getOrganizacaoId()));
        }
        if (!isEmpty(usuarioDto.getOrganizacaoEmpresaId())) {
            usuario.setOrganizacaoEmpresa(new OrganizacaoEmpresa(usuarioDto.getOrganizacaoEmpresaId()));
        }
        if (!isEmpty(usuarioDto.getUsuarioCadastroId())) {
            usuario.setUsuarioCadastro(new Usuario(usuarioDto.getUsuarioCadastroId()));
        }
        if (!Objects.equals(ID_NIVEL_MSO, usuarioDto.getNivelId())) {
            usuario.setTiposFeeder(Set.of());
        }
        usuario.setSubCanaisId(usuarioDto.getSubCanaisId());

        return usuario;
    }

    public static UsuarioDto of(Usuario usuario) {
        var usuarioDto = new UsuarioDto();
        BeanUtils.copyProperties(usuario, usuarioDto);
        usuarioDto.setCargoId(usuario.getCargoId());
        usuarioDto.setCargoCodigo(usuario.getCargoCodigo());
        usuarioDto.setCargoQuantidadeSuperior(usuario.getCargo().getQuantidadeSuperior());
        usuarioDto.setPossuiCargoSuperior(!CollectionUtils.isEmpty(usuario.getCargo().getSuperiores()));
        usuarioDto.setDepartamentoId(usuario.getDepartamentoId());
        usuarioDto.setUnidadesNegociosId(usuario.getUnidadesNegociosId());
        usuarioDto.setEmpresasId(usuario.getEmpresasId());
        usuarioDto.setNivelId(usuario.getNivelId());
        usuarioDto.setNivelCodigo(usuario.getNivelCodigo());
        usuarioDto.setHierarquiasId(usuario.getUsuariosHierarquia().stream()
            .map(UsuarioHierarquia::getUsuarioSuperiorId)
            .collect(Collectors.toList()));
        usuarioDto.setUnidadeNegocioId(obterUnidadeNegocioId(usuario));
        usuarioDto.setOrganizacaoId(getOrganizacaoId(usuario));
        if (Objects.nonNull(usuario.getUsuarioCadastro())) {
            usuarioDto.setUsuarioCadastroId(usuario.getUsuarioCadastro().getId());
        }
        usuarioDto.setOrganizacaoEmpresaId(getOrganizacaoEmpresaId(usuario));
        usuarioDto.setSubCanaisId(usuario.getSubCanaisId());

        return usuarioDto;
    }

    public static UsuarioDto of(Usuario usuario, boolean permiteEditarCompleto) {
        var usuarioDto = UsuarioDto.of(usuario);
        usuarioDto.setPermiteEditarCompleto(permiteEditarCompleto);

        return usuarioDto;
    }

    private static Integer getOrganizacaoId(Usuario usuario) {
        return !isEmpty(usuario.getOrganizacao()) ? usuario.getOrganizacao().getId() : null;
    }

    private static Integer getOrganizacaoEmpresaId(Usuario usuario) {
        return !isEmpty(usuario.getOrganizacaoEmpresa()) ? usuario.getOrganizacaoEmpresa().getId() : null;
    }

    public static UsuarioDto parse(UsuarioMqRequest usuarioMqRequest) {
        var usuarioDto = new UsuarioDto();
        BeanUtils.copyProperties(usuarioMqRequest, usuarioDto);

        return usuarioDto;
    }

    private static Integer obterUnidadeNegocioId(Usuario usuario) {
        return !isEmpty(usuario.getUnidadesNegociosId()) ? usuario.getUnidadesNegociosId().get(0) : 0;
    }

    public boolean hasCanalD2dProprio() {
        return canais.contains(ECanal.D2D_PROPRIO);
    }

    public boolean hasIdAndCargoCodigo() {
        return id != null && cargoCodigo != null;
    }

    public boolean hasSubCanaisId() {
        return !subCanaisId.isEmpty();
    }

    public UsuarioDto(Integer id, String email) {
        this.id = id;
        this.email = email;
    }
}
