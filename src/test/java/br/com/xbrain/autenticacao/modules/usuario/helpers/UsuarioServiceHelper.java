package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento.COMERCIAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.D2D_PROPRIO;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;

public class UsuarioServiceHelper {

    public static UsuarioSubordinadoDto usuarioSubordinadoDtoDtoResponse(Integer id) {
        return UsuarioSubordinadoDto.builder()
            .id(id)
            .nome("Uma nome")
            .cpf("12345678911")
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuarioTopHierarquia() {
        return Usuario.builder()
            .id(1)
            .cpf("097.238.645-92")
            .nome("Seiya")
            .situacao(A)
            .build();
    }

    public static UsuarioSubordinadoDto umOutroUsuarioSubordinadoDtoDtoResponse(Integer id) {
        return UsuarioSubordinadoDto.builder()
            .id(id)
            .nome("Uma outro nome")
            .cpf("98765432111")
            .situacao(ESituacao.I)
            .build();
    }

    public static List<AgenteAutorizadoResponse> umaListaDeAgenteAutorizadoResponse() {
        return List.of(umAgenteAutorizadoAtivoResponse(),
            umAgenteAutorizadoInativoResponse(),
            umAgenteAutorizadoRejeitadoResponse(),
            umOutroAgenteAutorizadoAtivoResponse());
    }

    public static AgenteAutorizadoResponse umAgenteAutorizadoAtivoResponse() {
        return AgenteAutorizadoResponse
            .builder()
            .id("1")
            .razaoSocial("TESTE AA")
            .cnpj("00.000.0000/0001-00")
            .situacao("CONTRATO ATIVO")
            .build();
    }

    public static AgenteAutorizadoResponse umOutroAgenteAutorizadoAtivoResponse() {
        return AgenteAutorizadoResponse
            .builder()
            .id("2")
            .razaoSocial("OUTRO TESTE AA")
            .cnpj("00.000.0000/0001-20")
            .situacao("CONTRATO ATIVO")
            .build();
    }

    public static AgenteAutorizadoResponse umAgenteAutorizadoInativoResponse() {
        return AgenteAutorizadoResponse
            .builder()
            .id("3")
            .razaoSocial("TESTE AA INATIVO")
            .cnpj("00.000.0000/0001-30")
            .situacao("INATIVO")
            .build();
    }

    public static AgenteAutorizadoResponse umAgenteAutorizadoRejeitadoResponse() {
        return AgenteAutorizadoResponse
            .builder()
            .id("4")
            .razaoSocial("TESTE AA REJEITADO")
            .cnpj("00.000.0000/0001-40")
            .situacao("REJEITADO")
            .build();
    }

    public static Canal umCanal() {
        return Canal
            .builder()
            .usuarioId(1)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .build();
    }

    public static Canal umOutroCanal() {
        return Canal
            .builder()
            .usuarioId(1)
            .canal(ECanal.VAREJO)
            .build();
    }

    public static AgenteAutorizadoUsuarioDto umAgenteAutorizadoUsuarioDto() {
        return AgenteAutorizadoUsuarioDto
            .builder()
            .usuarioId(2)
            .cnpj("78300110000166")
            .razaoSocial("Razao Social")
            .build();
    }

    public static UsuarioCsvResponse umUsuarioOperacaoCsv() {
        return UsuarioCsvResponse
            .builder()
            .id(1)
            .nome("Usuario_1_teste")
            .email("usuario1@teste.com")
            .telefone("999999999")
            .cpf("11111111111")
            .cargo("cargo")
            .departamento("departamento")
            .unidadesNegocios("unidadeNegocio")
            .empresas("empresa")
            .situacao(ESituacao.A)
            .dataUltimoAcesso(LocalDateTime.of(2021, 1, 1, 1, 1))
            .loginNetSales("loginNetSales")
            .nivel("Operação")
            .hierarquia("hierarquia")
            .razaoSocial("razaoSocial")
            .cnpj("cnpj")
            .organizacao("organizacao")
            .build();
    }

    public static UsuarioCsvResponse umUsuarioAaCsv() {
        return UsuarioCsvResponse
            .builder()
            .id(2)
            .nome("Usuario_2_teste")
            .email("usuario2@teste.com")
            .telefone("999999998")
            .cpf("22222222222")
            .cargo("cargo")
            .departamento("departamento")
            .unidadesNegocios("unidadeNegocio")
            .empresas("empresa")
            .situacao(ESituacao.A)
            .dataUltimoAcesso(LocalDateTime.of(2021, 1, 1, 1, 1))
            .loginNetSales("loginNetSales")
            .nivel("Agente Autorizado")
            .organizacao("organizacao")
            .build();
    }

    public static UsuarioDtoVendas umUsuarioDtoVendas(Integer id) {
        return UsuarioDtoVendas
            .builder()
            .id(id)
            .build();
    }

    public static Usuario umUsuarioCompleto(CodigoCargo codigoCargo, Integer idCargo,
                                            CodigoNivel nivel, CodigoDepartamento departamento, ECanal canal) {
        var usuario = Usuario
            .builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .id(idCargo)
                .codigo(codigoCargo)
                .nivel(Nivel
                    .builder()
                    .codigo(nivel)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .codigo(departamento)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
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
                List.of(canal)
            )
        );

        return usuario;
    }

    public static Usuario umUsuarioCompleto(ESituacao situacao, CodigoCargo codigoCargo, Integer idCargo,
                                            CodigoNivel nivel, CodigoDepartamento departamento, ECanal canal) {
        var usuario = Usuario
            .builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(situacao)
            .loginNetSales("login123")
            .subCanais(Set.of(SubCanal.builder().codigo(ETipoCanal.PAP).build()))
            .cargo(Cargo
                .builder()
                .id(idCargo)
                .codigo(codigoCargo)
                .nivel(Nivel
                    .builder()
                    .codigo(nivel)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .codigo(departamento)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
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
                List.of(canal)
            )
        );

        return usuario;
    }

    public static Usuario umUsuarioCompleto() {
        var usuario = Usuario
            .builder()
            .id(1)
            .nome("NOME UM")
            .email("email@email.com")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .codigo(OPERACAO_TELEVENDAS)
                .nivel(Nivel
                    .builder()
                    .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                    .nome("AGENTE AUTORIZADO")
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

    public static Usuario umUsuarioD2D(ETipoCanal subcanal) {
        var usuario = Usuario
            .builder()
            .id(1)
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(ESituacao.I)
            .subCanais(Set.of(SubCanal.builder().codigo(subcanal).build()))
            .usuariosHierarquia(Set.of(usuarioSupervisorHierarquia()))
            .cargo(Cargo
                .builder()
                .id(120)
                .codigo(VENDEDOR_OPERACAO)
                .nivel(Nivel
                    .builder()
                    .codigo(OPERACAO)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .codigo(COMERCIAL)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
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
        usuario.setCanais(
            Sets.newHashSet(
                List.of(D2D_PROPRIO)
            )
        );
        return usuario;
    }

    public static Usuario umUsuarioD2DComCoordenador(ETipoCanal subcanal) {
        var usuario = Usuario
            .builder()
            .id(1)
            .nome("NOME DOIS")
            .cpf("111.111.111-11")
            .situacao(ESituacao.I)
            .subCanais(Set.of(SubCanal.builder().codigo(subcanal).build()))
            .usuariosHierarquia(Set.of(usuarioCoodernadorHierarquia()))
            .cargo(Cargo
                .builder()
                .id(120)
                .codigo(VENDEDOR_OPERACAO)
                .nivel(Nivel
                    .builder()
                    .codigo(OPERACAO)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .codigo(COMERCIAL)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
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
        usuario.setCanais(
            Sets.newHashSet(
                List.of(D2D_PROPRIO)
            )
        );
        return usuario;
    }

    public static Usuario umUsuarioD2DSemCoordenador(ETipoCanal subcanal) {
        var usuario = Usuario
            .builder()
            .id(1)
            .nome("NOME TRES")
            .cpf("111.111.111-11")
            .situacao(ESituacao.I)
            .subCanais(Set.of(SubCanal.builder().codigo(subcanal).build()))
            .usuariosHierarquia(Set.of(umDiretorHierarquia()))
            .cargo(Cargo
                .builder()
                .id(120)
                .codigo(VENDEDOR_OPERACAO)
                .nivel(Nivel
                    .builder()
                    .codigo(OPERACAO)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .codigo(COMERCIAL)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
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
        usuario.setCanais(
            Sets.newHashSet(
                List.of(D2D_PROPRIO)
            )
        );
        return usuario;
    }

    public static Usuario umUsuarioD2DSemSubcanal(ETipoCanal subcanal) {
        var usuario = Usuario
            .builder()
            .id(1)
            .nome("NOME QUATRO")
            .cpf("111.111.111-11")
            .situacao(ESituacao.I)
            .subCanais(Set.of())
            .usuariosHierarquia(Set.of(usuarioCoodernadorHierarquia()))
            .cargo(Cargo
                .builder()
                .id(120)
                .codigo(VENDEDOR_OPERACAO)
                .nivel(Nivel
                    .builder()
                    .codigo(OPERACAO)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .codigo(COMERCIAL)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
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
        usuario.setCanais(
            Sets.newHashSet(
                List.of(D2D_PROPRIO)
            )
        );
        return usuario;
    }

    public static Usuario umVendedorReceptivo() {
        var usuario = umUsuarioCompleto();
        var cargo = Cargo.builder()
            .codigo(CodigoCargo.VENDEDOR_RECEPTIVO)
            .nivel(Nivel.builder().codigo(CodigoNivel.RECEPTIVO).build())
            .build();
        var organizacao = Organizacao.builder().id(1).nome("Org teste").build();
        usuario.setCargo(cargo);
        usuario.setOrganizacao(organizacao);
        return usuario;
    }

    public static SelectResponse umSelectResponseDeVendedorReceptivoInativo() {
        var vendedorReceptivo = umVendedorReceptivo();
        return SelectResponse
            .builder()
            .label(vendedorReceptivo.getNome().concat(" (INATIVO)"))
            .value(vendedorReceptivo.getId())
            .build();
    }

    public static SelectResponse umSelectResponseDeVendedorReceptivoRealocado() {
        var vendedorReceptivo = umVendedorReceptivo();
        return SelectResponse
            .builder()
            .label(vendedorReceptivo.getNome().concat(" (REALOCADO)"))
            .value(vendedorReceptivo.getId())
            .build();
    }

    public static UsuarioFiltros umUsuarioFiltro() {
        return UsuarioFiltros.builder()
            .codigosCargos(List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO))
            .canal(D2D_PROPRIO)
            .build();
    }

    public static UsuarioAtivacaoDto umUsuarioAtivacaoDto() {
        return UsuarioAtivacaoDto.builder()
            .idUsuario(10)
            .idUsuarioAtivacao(20)
            .observacao("Teste")
            .build();
    }

    public static UsuarioAtivacaoDto umUsuarioAtivacaoDtoD2d() {
        return UsuarioAtivacaoDto.builder()
            .idUsuario(1)
            .idUsuarioAtivacao(20)
            .observacao("Teste")
            .build();
    }

    public static Usuario umUsuarioSocioPrincipalEAa() {
        var usuario = umUsuarioCompleto();
        usuario.setCargo(Cargo
            .builder()
            .codigo(AGENTE_AUTORIZADO_SOCIO)
            .nivel(Nivel
                .builder()
                .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                .nome("AGENTE AUTORIZADO")
                .build())
            .build());
        return usuario;
    }

    public static UsuarioExecutivoResponse umUsuarioExecutivo() {
        return new UsuarioExecutivoResponse(1, "bakugo@teste.com", "BAKUGO");
    }

    public static UsuarioSituacaoResponse umUsuarioSituacaoResponse(Integer id, String nome, ESituacao situacao) {
        return UsuarioSituacaoResponse
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }

    public static Usuario umUsuario() {
        return Usuario.builder()
            .id(1)
            .cpf("097.238.645-92")
            .nome("Seiya")
            .situacao(ESituacao.A)
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticado(int usuarioId, String nivelCodigo, CodigoCargo cargo,
                                                          CodigoFuncionalidade... permissoes) {
        return UsuarioAutenticado.builder()
            .usuario(getUser(usuarioId, getCargo(cargo)))
            .nivelCodigo(nivelCodigo)
            .cargoCodigo(cargo)
            .id(usuarioId)
            .permissoes(getPermissoes(permissoes))
            .build();
    }

    public static Usuario getUser(int usuarioId, Cargo cargo) {
        return Usuario.builder()
            .id(usuarioId)
            .cpf("097.238.645-92")
            .cargo(cargo)
            .build();
    }

    public static Cargo getCargo(CodigoCargo cargo) {
        return Cargo.builder()
            .codigo(cargo)
            .build();
    }

    public static List<SimpleGrantedAuthority> getPermissoes(CodigoFuncionalidade... permissoes) {
        return Objects.nonNull(permissoes)
            ? Arrays.stream(permissoes)
            .map(permissao -> new SimpleGrantedAuthority(permissao.getRole()))
            .collect(Collectors.toList())
            : null;
    }

    public static Nivel umNivelOperacao() {
        return Nivel.builder()
            .id(1)
            .codigo(OPERACAO)
            .nome(OPERACAO.name())
            .build();
    }

    private static UsuarioHierarquia usuarioSupervisorHierarquia() {
        return UsuarioHierarquia.builder()
            .usuario(Usuario.builder().id(1).build())
            .usuarioSuperior(umSupervisorD2d())
            .build();
    }

    private static UsuarioHierarquia usuarioCoodernadorHierarquia() {
        return UsuarioHierarquia.builder()
            .usuario(Usuario.builder().id(1).build())
            .usuarioSuperior(umCoordenadorD2d())
            .build();
    }

    private static UsuarioHierarquia umDiretorHierarquia() {
        return UsuarioHierarquia.builder()
            .usuario(Usuario.builder().id(1).build())
            .usuarioSuperior(umDiretor())
            .build();
    }
}
