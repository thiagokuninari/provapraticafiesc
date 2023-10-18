package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.util.List;
import java.util.Set;

public class UsuarioAutenticadoHelper {

    public static UsuarioAutenticado umUsuarioAutenticadoNivelBackoffice() {
        return UsuarioAutenticado.builder()
            .id(100)
            .nivelCodigo(CodigoNivel.BACKOFFICE.name())
            .organizacaoId(8)
            .cargo(CodigoNivel.OPERACAO.toString())
            .cargoId(114)
            .usuario(buildUsuario())
            .permissoes(List.of())
            .canais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO))
            .nivelId(18)
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticadoNivelMso() {
        return UsuarioAutenticado.builder()
                .id(101)
                .nivelCodigo(CodigoNivel.MSO.name())
                .organizacaoId(8)
                .cargoCodigo(CodigoCargo.MSO_CONSULTOR)
                .cargoId(6)
                .usuario(buildUsuario())
                .permissoes(List.of())
                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                .usuario(buildUsuarioMso())
                .nivelId(18)
                .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticadoAtivoProprioComCargo(Integer id, CodigoCargo codigoCargo,
                                                                              CodigoDepartamento codigoDepartamento) {
        return UsuarioAutenticado.builder()
                .id(id)
                .nivelCodigo(CodigoNivel.OPERACAO.name())
                .cargoCodigo(codigoCargo)
                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                .usuario(buildUsuario())
                .departamentoCodigo(codigoDepartamento)
                .build();
    }

    public static Usuario buildUsuario() {
        return Usuario.builder()
            .id(1)
            .cargo(Cargo.builder()
                .id(1)
                .nivel(Nivel.builder()
                    .codigo(CodigoNivel.OPERACAO)
                    .build())
                .build())
            .build();
    }

    public static Usuario buildUsuarioMso() {
        return Usuario.builder()
            .cargo(Cargo.builder()
                .nivel(Nivel.builder()
                    .codigo(CodigoNivel.MSO)
                    .build())
                .build())
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticadoNivelAa() {
        return UsuarioAutenticado.builder()
            .id(101)
            .nivelCodigo(CodigoNivel.AGENTE_AUTORIZADO.name())
            .organizacaoId(8)
            .cargoId(47)
            .nivelId(3)
            .build();
    }

    public static UsuarioAutenticado umUsuarioSuperiorAtivoLocal() {
        return UsuarioAutenticado.builder()
            .id(2)
            .subCanais(Set.of(SubCanalHelper.umSubCanalDto(1, ETipoCanal.PAP, "a")))
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .build();
    }

    public static UsuarioAutenticado umUsuarioSuperiorD2d() {
        return UsuarioAutenticado.builder()
            .id(2)
            .subCanais(Set.of(SubCanalHelper.umSubCanalDto(1, ETipoCanal.PAP, "a")))
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .build();
    }
}
