package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanal;

public class UsuarioHelper {

    public static Usuario umUsuario(Integer id, String nome, ESituacao situacao,
                                    CodigoCargo codigoCargo, CodigoNivel codigoNivel) {
        return Usuario
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .cargo(Cargo
                .builder()
                .codigo(codigoCargo)
                .nivel(Nivel
                    .builder()
                    .codigo(codigoNivel)
                    .build())
                .build())
            .build();
    }

    public static Usuario umUsuario(Integer id, Cargo cargo, Set<ECanal> canais, String cpf) {
        return Usuario
            .builder()
            .id(id)
            .cargo(cargo)
            .canais(canais)
            .cpf(cpf)
            .email("email@email.com")
            .usuariosHierarquia(new HashSet<>())
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuario(Integer id, Cargo cargo, Set<ECanal> canais, Integer departamentoId) {
        return Usuario
            .builder()
            .departamento(new Departamento(departamentoId))
            .id(id)
            .cargo(cargo)
            .canais(canais)
            .email("email@email.com")
            .usuariosHierarquia(new HashSet<>())
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuario(Integer id, Cargo cargo, Set<ECanal> canais) {
        return Usuario
            .builder()
            .id(id)
            .cargo(cargo)
            .canais(canais)
            .email("email@email.com")
            .usuariosHierarquia(new HashSet<>())
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuario() {
        return Usuario
            .builder()
            .id(100)
            .nome("NED STARK")
            .cargo(umCargo())
            .subCanais(Set.of(umSubCanal()))
            .build();
    }

    public static Usuario doisUsuario(Integer id, String nome, ESituacao situacao) {
        return Usuario
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }

    public static Usuario tresUsuario(Integer id, Cargo cargo, Set<ECanal> canais) {
        return Usuario
            .builder()
            .id(id)
            .cargo(cargo)
            .canais(canais)
            .email("email@email.com")
            .usuariosHierarquia(new HashSet<>())
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario outroUsuarioCompleto() {
        var usuario = Usuario
            .builder()
            .id(2)
            .nome("NOME DOIS")
            .email("email@email.com")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .codigo(EXECUTIVO_HUNTER)
                .nivel(Nivel
                    .builder()
                    .codigo(OPERACAO)
                    .situacao(ESituacao.A)
                    .nome("OPERACAO")
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .nome("UNIDADE NEGÓCIO UM")
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .nome("EMPRESA UM")
                .build()))
            .build();

        usuario.setCidades(
            Sets.newHashSet(
                List.of(UsuarioCidade.criar(
                    usuario,
                    3237,
                    100
                ))
            )
        );
        usuario.setUsuariosHierarquia(
            Sets.newHashSet(
                UsuarioHierarquia.criar(
                    usuario,
                    65,
                    100)
            )
        );
        usuario.setCanais(
            Sets.newHashSet(
                List.of(ECanal.ATIVO_PROPRIO)
            )
        );

        return usuario;
    }

    public static Cargo umCargo() {
        return Cargo
            .builder()
            .id(50)
            .codigo(ADMINISTRADOR)
            .nome("ADMINISTRADOR")
            .situacao(A)
            .nivel(umNivelXbrain())
            .build();
    }

    public static Nivel umNivelXbrain() {
        return Nivel
            .builder()
            .id(4)
            .codigo(XBRAIN)
            .nome("X-BRAIN")
            .situacao(A)
            .build();
    }

    public static Usuario umUsuarioOperacaoComSubCanal(Set<SubCanal> subCanais) {
        return Usuario.builder()
            .id(101112)
            .cargo(Cargo.builder()
                .codigo(VENDEDOR_OPERACAO)
                .nivel(Nivel
                    .builder()
                    .codigo(OPERACAO)
                    .situacao(ESituacao.A)
                    .nome("OPERACAO")
                    .build())
                .build())
            .subCanais(subCanais)
            .usuarioCadastro(umUsuarioMsoConsultor(Set.of()))
            .build();
    }

    public static Usuario umUsuarioMsoConsultor(Set<SubCanal> subCanais) {
        return Usuario.builder()
            .id(23)
            .cargo(Cargo.builder()
                .codigo(MSO_CONSULTOR)
                .nivel(Nivel
                    .builder()
                    .codigo(MSO)
                    .situacao(ESituacao.A)
                    .nome("MSO")
                    .build())
                .build())
            .subCanais(subCanais)
            .build();
    }

    public static UsuarioDto umUsuarioOperacaoDto() {
        return UsuarioDto.builder()
            .nome("VENDEDOR OPERACAO D2D")
            .nivelCodigo(OPERACAO)
            .subCanaisId(Set.of(3))
            .build();
    }
}
