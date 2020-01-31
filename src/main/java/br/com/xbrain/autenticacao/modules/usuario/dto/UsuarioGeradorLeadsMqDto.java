package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioGeradorLeadsMqDto {

    private Integer geradorLeadsId;
    private Integer usuarioId;
    private String nome;
    private String cpf;
    private String telefone;
    @Enumerated(EnumType.STRING)
    private String situacao;
    private String email;
    private Integer usuarioCadastroId;
    private LocalDateTime dataCadastro;

    public static Usuario criarUsuario(UsuarioGeradorLeadsMqDto usuarioDto) {
        var usuario = new Usuario();
        BeanUtils.copyProperties(usuarioDto, usuario);
        if (!isEmpty(usuarioDto.getUsuarioCadastroId())) {
            usuario.setUsuarioCadastro(new Usuario(usuarioDto.getUsuarioCadastroId()));
        } else {
            usuario.setUsuarioCadastro(new Usuario(usuario.getId()));
        }
        return usuario;
    }

    public boolean isNovoCadastro() {
        return isEmpty(usuarioId);
    }
}
