package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioGeradorLeadsMqDto {

    private Integer geradorLeadsId;
    private Integer usuarioId;
    private String nome;
    private String cpf;
    private String telefone;
    private ESituacao situacao;
    private String email;
    private Integer usuarioCadastroId;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataCadastro;

    public static Usuario criarUsuarioNovo(UsuarioGeradorLeadsMqDto usuarioDto) {
        var usuario = new Usuario();
        BeanUtils.copyProperties(usuarioDto, usuario);
        if (!isEmpty(usuarioDto.getUsuarioCadastroId())) {
            usuario.setUsuarioCadastro(new Usuario(usuarioDto.getUsuarioCadastroId()));
        }
        return usuario;
    }

    public boolean isNovoCadastro() {
        return isEmpty(usuarioId);
    }
}
