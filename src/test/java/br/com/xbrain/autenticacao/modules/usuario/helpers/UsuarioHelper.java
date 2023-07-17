package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.EMPRESARIAL;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.RESIDENCIAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.PAP_PREMIUM;
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

    public static Usuario umUsuarioOperacaoComSubCanal(Integer usuarioId,
                                                       Integer subCanalId,
                                                       ETipoCanal codigoSubCanal) {
        return Usuario.builder()
            .id(usuarioId)
            .email("USUARIO.OPERACAO@CLARO.COM.BR")
            .cargo(Cargo.builder()
                .codigo(VENDEDOR_OPERACAO)
                .nivel(Nivel
                    .builder()
                    .codigo(OPERACAO)
                    .situacao(ESituacao.A)
                    .nome("OPERACAO")
                    .build())
                .build())
            .subCanais(Set.of(
                SubCanal.builder()
                    .id(subCanalId)
                    .codigo(codigoSubCanal)
                    .build()))
            .usuarioCadastro(umUsuarioMsoConsultor(3, PAP_PREMIUM))
            .usuariosHierarquia(new HashSet<>())
            .situacao(A)
            .build();
    }

    public static Usuario umUsuarioMsoConsultor(Integer subCanalId, ETipoCanal codigoSubCanal) {
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
            .subCanais(Set.of(
                SubCanal.builder()
                    .id(subCanalId)
                    .codigo(codigoSubCanal)
                    .build()))
            .build();
    }

    public static UsuarioDto umUsuarioOperacaoDto() {
        return UsuarioDto.builder()
            .nome("VENDEDOR OPERACAO D2D")
            .nivelId(1)
            .nivelCodigo(OPERACAO)
            .subCanaisId(Set.of(3))
            .build();
    }

    public static UsuarioDto umUsuarioDto(Integer usuarioId, String usuarioEmail) {
        return UsuarioDto.builder()
            .id(usuarioId)
            .email(usuarioEmail)
            .build();
    }

    public static UsuarioDto umUsuarioDtoMso() {
        return UsuarioDto.builder()
            .nome("MSO FEEDER")
            .cpf("873.616.099-70")
            .nivelId(2)
            .organizacaoId(1)
            .usuarioCadastroId(1)
            .tiposFeeder(Set.of(EMPRESARIAL, RESIDENCIAL))
            .build();
    }

    public static UsuarioDto umUsuarioDtoOuvidoria() {
        return UsuarioDto.builder()
            .nome("OUVIDORIA NAO FEEDER")
            .cpf("286.250.583-88")
            .nivelId(15)
            .organizacaoId(1)
            .usuarioCadastroId(1)
            .tiposFeeder(Set.of(EMPRESARIAL, RESIDENCIAL))
            .build();
    }

    public static Usuario umUsuarioMso() {
        return Usuario.builder()
            .id(150016)
            .nome("MSO FEEDER")
            .cpf("873.616.099-70")
            .email("MSO.FEEDER@MSO.COM.BR")
            .usuarioCadastro(umUsuarioCadastro())
            .usuariosHierarquia(new HashSet<>())
            .cargo(Cargo
                .builder()
                .quantidadeSuperior(50)
                .nivel(Nivel
                    .builder()
                    .id(2)
                    .build())
                .build())
            .tiposFeeder(Set.of(EMPRESARIAL, RESIDENCIAL))
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuarioCadastro() {
        return Usuario.builder()
            .id(101112)
            .nome("COLABORADOR SUPORTE")
            .build();
    }

    public static UsuarioMqRequest umUsuarioMqRequestSocioPrincipal() {
        return UsuarioMqRequest.builder()
            .isCadastroSocioPrincipal(true)
            .build();
    }

    public static UsuarioMqRequest umUsuarioMqRequestSocioSecundario() {
        return UsuarioMqRequest.builder()
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO_SECUNDARIO)
            .build();
    }

    public static UsuarioDto umUsuarioDtoSender() {
        return UsuarioDto.builder()
            .id(1)
            .email("EMAIL@TEST.COM")
            .cargoId(1)
            .cargoCodigo(AGENTE_AUTORIZADO_TECNICO_VENDEDOR)
            .departamentoId(1)
            .alterarSenha(Eboolean.F)
            .situacao(A)
            .unidadesNegociosId(List.of(1))
            .unidadeNegocioId(1)
            .nivelId(1)
            .empresasId(List.of(1))
            .hierarquiasId(List.of())
            .recuperarSenhaTentativa(0)
            .tiposFeeder(Set.of())
            .subCanaisId(Set.of())
            .build();
    }
}
