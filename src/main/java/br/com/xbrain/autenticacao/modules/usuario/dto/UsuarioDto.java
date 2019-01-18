package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UsuarioDto implements Serializable {

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
    @CPF
    @NotNull
    @Size(max = 14)
    private String cpf;
    @Size(max = 25)
    private String rg;
    @Size(max = 30)
    private String orgaoExpedidor;
    @Size(max = 120)
    private String loginNetSales;
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
    @NotNull
    private Integer departamentoId;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataInativacao;
    @Enumerated(EnumType.STRING)
    private Eboolean alterarSenha;
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;
    private Integer usuarioCadastroId;
    private List<Integer> hierarquiasId;
    private List<Integer> cidadesId;
    private Integer recuperarSenhaTentativa = 0;
    @NotEmpty
    private Set<ECanal> canais = Sets.newHashSet();

    public static Usuario convertFrom(UsuarioDto usuarioDto) {
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioDto, usuario);
        usuario.setEmpresasId(usuarioDto.getEmpresasId());
        usuario.setUnidadesNegociosId(usuarioDto.getUnidadesNegociosId());
        usuario.setCargo(new Cargo(usuarioDto.getCargoId()));
        usuario.setDepartamento(new Departamento(usuarioDto.getDepartamentoId()));
        if (!ObjectUtils.isEmpty(usuarioDto.getUsuarioCadastroId())) {
            usuario.setUsuarioCadastro(new Usuario(usuarioDto.getUsuarioCadastroId()));
        }
        return usuario;
    }

    public static UsuarioDto convertTo(Usuario usuario) {
        UsuarioDto usuarioDto = new UsuarioDto();
        BeanUtils.copyProperties(usuario, usuarioDto);
        usuarioDto.setCargoId(usuario.getCargoId());
        usuarioDto.setCargoCodigo(usuario.getCargoCodigo());
        usuarioDto.setDepartamentoId(usuario.getDepartamentoId());
        usuarioDto.setUnidadesNegociosId(usuario.getUnidadesNegociosId());
        usuarioDto.setEmpresasId(usuario.getEmpresasId());
        usuarioDto.setNivelId(usuario.getNivelId());
        usuarioDto.setNivelCodigo(usuario.getNivelCodigo());
        usuarioDto.setHierarquiasId(usuario.getUsuariosHierarquia().stream()
                .map(UsuarioHierarquia::getUsuarioSuperiorId)
                .collect(Collectors.toList()));
        usuarioDto.setUnidadeNegocioId(obterUnidadeNegocioId(usuario));
        return usuarioDto;
    }

    public static UsuarioDto parse(UsuarioMqRequest usuarioMqRequest) {
        UsuarioDto usuarioDto = new UsuarioDto();
        BeanUtils.copyProperties(usuarioMqRequest, usuarioDto);
        return usuarioDto;
    }

    private static Integer obterUnidadeNegocioId(Usuario usuario) {
        return !CollectionUtils.isEmpty(usuario.getUnidadesNegociosId())
                ? usuario.getUnidadesNegociosId().get(0) : 0;
    }
}
