package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.beans.BeanUtils;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class UsuarioBriefingRequest {

    private Integer id;
    @NotBlank
    @Size(max = 80)
    private String nome;
    @CPF
    @NotBlank
    @Size(max = 14)
    private String cpf;
    @Size(max = 25)
    private String rg;
    @NotBlank
    @Size(max = 80)
    private String email;
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate nascimento;
    @Size(max = 100)
    private String telefone;
    @NotNull
    private Integer cargoId;
    @NotNull
    private Integer departamentoId;
    @NotEmpty
    private List<Integer> unidadesNegociosId;
    @NotEmpty
    private List<Integer> empresasId;
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public static Usuario of(UsuarioBriefingRequest usuarioBriefingRequest) {
        var usuario = new Usuario();
        BeanUtils.copyProperties(usuarioBriefingRequest, usuario);
        usuario.setEmpresasId(usuarioBriefingRequest.getEmpresasId());
        usuario.setCargo(new Cargo(usuarioBriefingRequest.getCargoId()));
        usuario.setDepartamento(new Departamento(usuarioBriefingRequest.getDepartamentoId()));
        usuario.setNascimento(usuarioBriefingRequest.getNascimento().atStartOfDay());

        return usuario;
    }
}
