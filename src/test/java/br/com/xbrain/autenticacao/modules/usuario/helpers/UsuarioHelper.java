package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.EMPRESARIAL;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.RESIDENCIAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_TECNICO_VENDEDOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.EXECUTIVO_HUNTER;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;

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
            .build();
    }

    public static Usuario umUsuarioComCidades() {
        return Usuario.builder()
            .id(121314)
            .empresas(List.of())
            .unidadesNegocios(List.of())
            .canais(Set.of())
            .cargo(new Cargo())
            .departamento(new Departamento())
            .usuariosHierarquia(Set.of())
            .cidades(UsuarioCidadeHelper.listaUsuarioCidadesDoParana())
            .build();
    }

    public static Usuario umUsuarioComDistritos() {
        return Usuario.builder()
            .id(151617)
            .empresas(List.of())
            .unidadesNegocios(List.of())
            .canais(Set.of())
            .cargo(new Cargo())
            .departamento(new Departamento())
            .usuariosHierarquia(Set.of())
            .cidades(UsuarioCidadeHelper.listaUsuarioCidadeDeDistritosDeLondrina())
            .build();
    }

    public static Usuario umUsuarioSemCidades() {
        return Usuario.builder()
            .id(181920)
            .empresas(List.of())
            .unidadesNegocios(List.of())
            .canais(Set.of())
            .cargo(new Cargo())
            .departamento(new Departamento())
            .usuariosHierarquia(Set.of())
            .cidades(Set.of())
            .build();
    }

    public static Usuario umUsuarioSiteSupervisor() {
        return Usuario.builder()
            .id(1655626)
            .nome("NOME USUARIO SITE SUPERVISOR")
            .email("SITESUPERVISOR@XBRAIN.COM.BR")
            .cargo(CargoHelper.umCargo(10, CodigoCargo.SUPERVISOR_OPERACAO))
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuarioSiteCoordenador() {
        return Usuario.builder()
            .id(1655607)
            .nome("NOME USUARIO SITE COORDENADOR")
            .email("SITECOORDENADOR@XBRAIN.COM.BR")
            .cargo(CargoHelper.umCargo(4, CodigoCargo.COORDENADOR_OPERACAO))
            .situacao(ESituacao.A)
            .build();
    }
}
