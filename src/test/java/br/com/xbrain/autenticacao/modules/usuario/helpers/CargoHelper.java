package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.NivelHelper.*;

public class CargoHelper {

    public static Cargo umCargo(Integer id, CodigoCargo codigoCargo) {
        return Cargo
            .builder()
            .id(id)
            .codigo(codigoCargo)
            .nivel(NivelHelper.umNivelMso())
            .build();
    }

    public static Cargo umCargoVendedor() {
        return Cargo.builder()
            .nivel(Nivel.builder()
                .nome("Xbrain")
                .build())
            .nome("Vendedor")
            .build();
    }

    public static Cargo umCargoVendedorInternet() {
        return Cargo.builder()
            .id(7)
            .nome("Internet Vendedor")
            .codigo(CodigoCargo.INTERNET_VENDEDOR)
            .situacao(ESituacao.A)
            .nivel(umNivelOperacao())
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

    public static Cargo umCargoAaSocio() {
        return Cargo.builder()
            .id(41)
            .nome("Sócio Principal")
            .codigo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO)
            .nivel(umNivelAa())
            .build();
    }

    public static Cargo umCargoMsoConsultor() {
        return Cargo.builder()
            .id(22)
            .nome("Consultor")
            .codigo(CodigoCargo.MSO_CONSULTOR)
            .nivel(umNivelMso())
            .build();
    }

    public static Cargo umCargoVendedorOperacao() {
        return Cargo.builder()
            .id(8)
            .nome("Vendedor Operação")
            .codigo(CodigoCargo.VENDEDOR_OPERACAO)
            .nivel(umNivelOperacao())
            .build();
    }

    public static Cargo umCargoSupervisor() {
        return Cargo.builder()
            .id(28)
            .nome("Supervisor")
            .codigo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR)
            .nivel(Nivel.builder().codigo(CodigoNivel.AGENTE_AUTORIZADO).build())
            .build();
    }

    public static Cargo umCargoAaSupervisorXbrain() {
        return Cargo.builder()
            .id(51)
            .nome("Analista de Suporte")
            .codigo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_XBRAIN)
            .nivel(umNivelXbrain())
            .build();
    }

    public static Cargo umCargoAdministrador() {
        return Cargo.builder()
            .id(50)
            .nome("Administrador")
            .codigo(CodigoCargo.ADMINISTRADOR)
            .nivel(umNivelXbrain())
            .build();
    }

    public static Cargo umCargoSupervisorOperacao() {
        return Cargo.builder()
            .id(10)
            .nome("Supervisor")
            .codigo(CodigoCargo.SUPERVISOR_OPERACAO)
            .nivel(umNivelOperacao())
            .build();
    }

    public static Cargo umCargoAssistenteOperacao() {
        return Cargo.builder()
            .id(2)
            .nome("Assistente")
            .codigo(CodigoCargo.ASSISTENTE_OPERACAO)
            .nivel(umNivelOperacao())
            .build();
    }

    public static Cargo umCargoMsoAnalista() {
        return Cargo.builder()
            .id(20)
            .nome("Analista")
            .codigo(CodigoCargo.MSO_ANALISTA)
            .nivel(umNivelMso())
            .build();
    }

    public static Cargo umCargoOperacaoTelevendas() {
        return Cargo.builder()
            .id(120)
            .nome("Operador Televendas")
            .codigo(CodigoCargo.OPERACAO_TELEVENDAS)
            .nivel(umNivelOperacao())
            .build();
    }

    public static Cargo umCargoAnalistaOperacao() {
        return Cargo.builder()
            .id(1)
            .nome("Analista")
            .nivel(umNivelOperacao())
            .superiores(Set.of(umCargoCoordernador()))
            .build();
    }

    public static Page<Cargo> umCargoPage(Integer id, String nome, Integer nivelId) {
        return new PageImpl<>(List.of(Cargo
            .builder()
            .id(id)
            .nome(nome)
            .nivel(Nivel
                .builder()
                .id(nivelId)
                .build())
            .build()));
    }

    public static Cargo umCargoNivelAdministrador(Integer id, String nome) {
        return Cargo.builder()
            .id(id)
            .nome(nome)
            .nivel(Nivel
                .builder()
                .id(4)
                .nome("Administrador")
                .build())
            .build();
    }

    public static CargoRequest umCargoRequest(Integer id, String nome) {
        return CargoRequest
            .builder()
            .id(id)
            .nome(nome)
            .situacao(ESituacao.A)
            .nivel(Nivel
                .builder()
                .id(4)
                .build())
            .build();
    }
}
