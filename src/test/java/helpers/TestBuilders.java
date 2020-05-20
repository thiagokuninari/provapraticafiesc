package helpers;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioConfiguracaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDadosAcessoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.util.List;
import java.util.Set;

public class TestBuilders {

    public static UsuarioHierarquiaResponse umUsuarioHierarquia() {
        return UsuarioHierarquiaResponse.builder()
            .id(100)
            .nome("XBRAIN")
            .cargoNome("COORDENADOR_OPERACAO")
            .build();
    }

    public static UsuarioConfiguracaoDto umUsuarioConfiguracaoDto() {
        UsuarioConfiguracaoDto dto = new UsuarioConfiguracaoDto();
        dto.setRamal(1000);
        dto.setUsuario(100);
        return dto;
    }

    public static UsuarioDadosAcessoRequest umEsqueciSenha() {
        UsuarioDadosAcessoRequest dto = new UsuarioDadosAcessoRequest();
        dto.setEmailAtual("HELPDESK@XBRAIN.COM.BR");
        return dto;
    }

    public static UsuarioConfiguracaoDto umUsuarioComRamalDuplicado() {
        UsuarioConfiguracaoDto dto = new UsuarioConfiguracaoDto();
        dto.setRamal(1008);
        dto.setUsuario(105);
        return dto;
    }

    public static List<UsuarioConfiguracaoDto> umaListaDeUsuarioComRamal() {
        UsuarioConfiguracaoDto dto1 = new UsuarioConfiguracaoDto();
        dto1.setRamal(1008);
        dto1.setUsuario(105);
        UsuarioConfiguracaoDto dto2 = new UsuarioConfiguracaoDto();
        dto2.setRamal(7006);
        dto2.setUsuario(100);
        return List.of(dto1, dto2);
    }

    public static List<UsuarioConfiguracaoDto> umaListaDeUsuarioComRamalInconsistente() {
        UsuarioConfiguracaoDto dto1 = new UsuarioConfiguracaoDto();
        dto1.setRamal(1008);
        dto1.setUsuario(110);
        return List.of(dto1);
    }

    public static List<UsuarioConfiguracaoDto> umaListDeUsuarioConfiguracaoDto() {
        UsuarioConfiguracaoDto dto = new UsuarioConfiguracaoDto();
        dto.setRamal(1000);
        dto.setUsuario(100);
        return List.of(dto);
    }

    public static UsuarioAutenticado buildUsuarioAutenticadoComTodosCanais() {
        return UsuarioAutenticado.builder()
            .usuario(Usuario.builder()
                .canais(Set.of(ECanal.D2D_PROPRIO, ECanal.ATIVO_PROPRIO))
                .build()).build();
    }
}
