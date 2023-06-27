package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.beans.BeanUtils;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Optional;

@Data
public class UsuarioBackofficeDto {

    private Integer id;
    @NotNull
    @Size(max = 80)
    private String nome;
    @NotNull
    @Size(max = 80)
    private String email;
    @Size(max = 100)
    private String telefone;
    @CPF
    @NotNull
    @Size(max = 14)
    private String cpf;
    @Size(max = 25)
    private String rg;
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate nascimento;
    @NotNull
    private Integer cargoId;
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;
    @NotNull
    private Integer departamentoId;
    private Integer organizacaoId;

    public static Usuario of(UsuarioBackofficeDto usuarioBackoffice) {
        var usuario = new Usuario();
        BeanUtils.copyProperties(usuarioBackoffice, usuario);
        Optional.ofNullable(usuarioBackoffice.getOrganizacaoId())
            .map(OrganizacaoEmpresa::new)
            .ifPresent(usuario::setOrganizacaoEmpresa);
        usuario.setCargo(new Cargo(usuarioBackoffice.getCargoId()));
        usuario.setDepartamento(new Departamento(usuarioBackoffice.getDepartamentoId()));
        usuario.setNascimento(usuarioBackoffice.getNascimento().atStartOfDay());

        return usuario;
    }
}
