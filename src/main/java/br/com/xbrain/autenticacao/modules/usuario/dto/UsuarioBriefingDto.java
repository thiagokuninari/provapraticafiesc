package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
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
public class UsuarioBriefingDto {

    private Integer id;
    @NotNull
    @Size(max = 80)
    private String nome;
    @CPF
    @NotNull
    @Size(max = 14)
    private String cpf;
    @Size(max = 25)
    private String rg;
    @NotNull
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
    private List<Integer> unidadesNegociosId = new ArrayList<>();
    @NotEmpty
    private List<Integer> empresasId = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public static Usuario of(UsuarioBriefingDto usuarioBriefingDto) {
        var usuario = new Usuario();
        BeanUtils.copyProperties(usuarioBriefingDto, usuario);
        usuario.setEmpresasId(usuarioBriefingDto.getEmpresasId());
        usuario.setCargo(new Cargo(usuarioBriefingDto.getCargoId()));
        usuario.setDepartamento(new Departamento(usuarioBriefingDto.getDepartamentoId()));
        usuario.setNascimento(usuarioBriefingDto.getNascimento().atStartOfDay());

        return usuario;
    }
}
