package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeeder;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAaTipoFeederDto {

    private List<Integer> usuariosIds;
    private Integer usuarioCadastroId;
    private ETipoFeeder tipoFeeder;

    public static UsuarioAaTipoFeederDto of(Usuario usuario,  ETipoFeeder aaTipoFeeder) {
        return UsuarioAaTipoFeederDto.builder()
            .usuariosIds(List.of(usuario.getId()))
            .usuarioCadastroId(usuario.getUsuarioCadastro().getId())
            .tipoFeeder(aaTipoFeeder)
            .build();
    }

    public static UsuarioAaTipoFeederDto of(UsuarioMqRequest usuario) {
        return UsuarioAaTipoFeederDto.builder()
            .usuariosIds(List.of(usuario.getId()))
            .usuarioCadastroId(usuario.getUsuarioCadastroId())
            .tipoFeeder(usuario.getAgenteAutorizadoFeeder())
            .build();
    }
}
