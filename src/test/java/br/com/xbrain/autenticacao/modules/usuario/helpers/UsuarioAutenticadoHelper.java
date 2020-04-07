package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

public class UsuarioAutenticadoHelper {

    public static UsuarioAutenticado umUsuarioAutenticadoNivelBackoffice() {
        return UsuarioAutenticado.builder()
            .id(100)
            .nivelCodigo(CodigoNivel.BACKOFFICE.name())
            .organizacaoId(8)
            .cargoId(114)
            .permissoes(List.of())
            .canais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .nivelId(18)
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticadoNivelOperacaoGerente() {
        return UsuarioAutenticado.builder()
                .id(100)
                .nivelCodigo(CodigoNivel.OPERACAO.name())
                .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
                .canais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                .permissoes(List.of(
                        new SimpleGrantedAuthority(CodigoFuncionalidade.AUT_2046.getRole()),
                        new SimpleGrantedAuthority(CodigoFuncionalidade.AUT_2047.getRole())))
                .build();
    }
}
