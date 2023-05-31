package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioHierarquiaDto {

    private Integer id;
    private String razaoSocialNome;
    private String cpf;
    private String cnpj;
    private String situacao;

    public static List<UsuarioHierarquiaDto> ofUsuarioSubordinadoDtoList(
        List<UsuarioSubordinadoDto> usuariosCompletoSubordinados) {

        return usuariosCompletoSubordinados.stream()
            .map(UsuarioHierarquiaDto::ofUsuarioSubordinadoDto).collect(Collectors.toList());
    }

    public static List<UsuarioHierarquiaDto> ofAgenteAutorizadoResponseList(
        List<AgenteAutorizadoResponse> aasResponse) {

        return aasResponse.stream()
            .map(UsuarioHierarquiaDto::ofAgenteAutorizadoResponse)
            .collect(Collectors.toList());
    }

    public static UsuarioHierarquiaDto ofUsuarioSubordinadoDto(UsuarioSubordinadoDto usuarioCompletoSubordinado) {
        return UsuarioHierarquiaDto.builder()
            .id(usuarioCompletoSubordinado.getId())
            .razaoSocialNome(usuarioCompletoSubordinado.getNome())
            .cpf(usuarioCompletoSubordinado.getCpf())
            .situacao(usuarioCompletoSubordinado.getSituacao().getDescricao())
            .build();
    }

    public static UsuarioHierarquiaDto ofAgenteAutorizadoResponse(AgenteAutorizadoResponse aaResponse) {
        return UsuarioHierarquiaDto.builder()
            .id(Integer.valueOf(aaResponse.getId()))
            .razaoSocialNome(aaResponse.getRazaoSocial())
            .cnpj(aaResponse.getCnpj())
            .situacao(aaResponse.getSituacao())
            .build();
    }

    public static UsuarioHierarquiaDto of(Usuario usuario) {
        return UsuarioHierarquiaDto.builder()
            .id(usuario.getId())
            .razaoSocialNome(usuario.getNome())
            .cpf(usuario.getCpf())
            .situacao(usuario.getSituacao().getDescricao())
            .build();
    }
}
