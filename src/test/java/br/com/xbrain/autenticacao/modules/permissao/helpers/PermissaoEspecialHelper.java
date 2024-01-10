package br.com.xbrain.autenticacao.modules.permissao.helpers;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.util.Arrays;
import java.util.List;

public class PermissaoEspecialHelper {

    public static List<PermissaoEspecial> umaListaPermissoesEspeciaisFuncFeederEAcompIndTecVend() {
        return Arrays.asList(
            umaPermissaoEspecial(1, 3046),
            umaPermissaoEspecial(2, 15000),
            umaPermissaoEspecial(3, 15005),
            umaPermissaoEspecial(4, 15012),
            umaPermissaoEspecial(5, 16101));
    }

    public static UsuarioDto umDtoNovoSocioPrincipal(Integer... antigosSociosPrincipaisIds) {
        return UsuarioDto
            .builder()
            .id(1)
            .antigosSociosPrincipaisIds(List.of(antigosSociosPrincipaisIds))
            .build();
    }

    private static PermissaoEspecial umaPermissaoEspecial(Integer permissaoId, Integer funcionalidadeId) {
        return PermissaoEspecial
            .builder()
            .id(permissaoId)
            .funcionalidade(Funcionalidade
                .builder()
                .id(funcionalidadeId)
                .build())
            .usuario(Usuario
                .builder()
                .id(32)
                .build())
            .build();
    }
}
