package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.NivelHelper.*;

public class CargoHelper {

    public static Cargo umCargo(Integer id, CodigoCargo codigoCargo) {
        return Cargo
            .builder()
            .id(id)
            .codigo(codigoCargo)
            .build();
    }

    public static Cargo umCargoCoordernador() {
        return Cargo.builder()
            .id(4)
            .nome("Coordenador")
            .codigo(CodigoCargo.COORDENADOR_OPERACAO)
            .situacao(ESituacao.A)
            .quantidadeSuperior(50)
            .nivel(umNivelOperacao())
            .superiores(Set.of(umCargoGerente()))
            .build();
    }

    public static Cargo umCargoExecutivo() {
        return Cargo.builder()
            .id(5)
            .nome("Executivo")
            .codigo(CodigoCargo.EXECUTIVO)
            .situacao(ESituacao.A)
            .quantidadeSuperior(50)
            .nivel(umNivelOperacao())
            .superiores(Set.of(
                umCargoCoordernador(),
                umCargoGerente()))
            .build();
    }

    public static Cargo umCargoDiretor() {
        return Cargo.builder()
            .id(6)
            .nome("Diretor")
            .codigo(CodigoCargo.DIRETOR_OPERACAO)
            .situacao(ESituacao.A)
            .quantidadeSuperior(50)
            .nivel(umNivelOperacao())
            .build();
    }

    public static Cargo umCargoGerente() {
        return Cargo.builder()
            .id(7)
            .nome("Gerente")
            .codigo(CodigoCargo.GERENTE_OPERACAO)
            .situacao(ESituacao.A)
            .quantidadeSuperior(50)
            .nivel(umNivelOperacao())
            .superiores(Set.of(umCargoDiretor()))
            .build();
    }

    public static Cargo umCargoReceptivo() {
        return Cargo.builder()
            .id(63)
            .nome("Vendedor Receptivo")
            .codigo(CodigoCargo.VENDEDOR_RECEPTIVO)
            .situacao(ESituacao.A)
            .quantidadeSuperior(50)
            .nivel(umNivelReceptivo())
            .build();
    }

    public static Cargo umCargoExecutivoHunter() {
        return Cargo.builder()
            .id(95)
            .nome("Executivo Hunter")
            .codigo(CodigoCargo.EXECUTIVO_HUNTER)
            .situacao(ESituacao.A)
            .quantidadeSuperior(50)
            .nivel(umNivelOperacao())
            .build();
    }
}
