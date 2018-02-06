package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.beans.BeanUtils;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
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
    @NotNull
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
    private String orgaoExpeditor;
    @Size(max = 120)
    private String loginNetSales;
    private LocalDateTime nascimento;
    @NotEmpty
    private List<Integer> unidadesNegociosId = new ArrayList<>();
    private Integer nivelId;
    @NotEmpty
    private List<Integer> empresasId = new ArrayList<>();
    @NotNull
    private Integer cargoId;
    @NotNull
    private Integer departamentoId;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataInativacao;
    @Enumerated(EnumType.STRING)
    private Eboolean alterarSenha;
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;
    private Integer usuarioCadastroId;

    public static UsuarioDto parse(Usuario usuario) {
        UsuarioDto usuarioDto = new UsuarioDto();
        BeanUtils.copyProperties(usuario, usuarioDto);
        usuarioDto.setCargoId(usuario.getCargoId());
        usuarioDto.setDepartamentoId(usuario.getDepartamentoId());
        usuarioDto.setUnidadesNegociosId(usuario.getUnidadesNegociosId());
        usuarioDto.setEmpresasId(usuario.getEmpresasId());
        usuarioDto.setNivelId(usuario.getNivelId());
        return usuarioDto;
    }

    public static UsuarioDto parse(UsuarioMqRequest usuarioMqRequest) {
        UsuarioDto usuarioDto = new UsuarioDto();
        BeanUtils.copyProperties(usuarioMqRequest, usuarioDto);
        return usuarioDto;
    }

}
