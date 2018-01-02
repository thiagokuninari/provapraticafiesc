package br.com.xbrain.autenticacao.modules.usuario.dto;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class UsuarioDto {

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
    private LocalDate nascimento;
    @NotNull
    private Integer unidadeNegocioId;
    @NotEmpty
    private List<Integer> empresasId = new ArrayList<>();
    @NotNull
    private Integer cargoId;
    @NotNull
    private Integer departamentoId;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataInativacao;
    @Size(max = 80)
    private String senha;
    @Enumerated(EnumType.STRING)
    private Eboolean alterarSenha;

    public static UsuarioDto parse(Usuario usuario) {
        UsuarioDto usuarioDto = new UsuarioDto();
        BeanUtils.copyProperties(usuario, usuarioDto);
        usuarioDto.setCargoId(usuario.getCargo().getId());
        usuarioDto.setDepartamentoId(usuario.getDepartamento().getId());
        usuarioDto.setUnidadeNegocioId(usuario.getUnidadeNegocio().getId());
        usuarioDto.setEmpresasId(usuario.getEmpresasId());
        return usuarioDto;
    }

}
