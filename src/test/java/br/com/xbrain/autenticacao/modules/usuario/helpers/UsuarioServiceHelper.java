package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.google.common.collect.Sets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;

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

    public static UsuarioAgenteAutorizadoResponse umUsuarioAgenteAutorizadoResponse(Integer id, Integer aaId) {
        return UsuarioAgenteAutorizadoResponse.builder()
            .id(id)
            .nome("FULANO DE TESTE")
            .email("TESTE@TESTE.COM")
            .agenteAutorizadoId(aaId)
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

    public static Usuario outroUsuarioNivelOpCanalAa() {
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
                .codigo(CodigoUnidadeNegocio.CLARO_RESIDENCIAL)
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .codigo(CodigoEmpresa.CLARO_TV)
                .build()))
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
            .build();
        return usuario;
    }

    public static UsuarioResponse outroUsuarioNivelOpCanalAaResponse() {
        var usuarioResponse = UsuarioResponse
            .builder()
            .id(2)
            .nome("NOME DOIS")
            .email("email@email.com")
            .cpf("111.111.111-11")
            .rg(null)
            .telefone(null)
            .telefone02(null)
            .telefone03(null)
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .dataCadastro(null)
            .codigoNivel(OPERACAO)
            .nomeNivel("OPERACAO")
            .codigoDepartamento(null)
            .codigoCargo(EXECUTIVO_HUNTER)
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
            .permissoes(null)
            .nascimento(null)
            .aaId(null)
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
            .tipoCanal(null)
            .codigoUnidadesNegocio(List.of(CodigoUnidadeNegocio.CLARO_RESIDENCIAL))
            .codigoEmpresas(List.of(CodigoEmpresa.CLARO_TV))
            .build();

        return usuarioResponse;
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

    public static Usuario umUsuarioCompleto(int cargoId, CodigoNivel nivel, int departamentoId) {
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
                .codigo(VENDEDOR_OPERACAO)
                .nivel(Nivel
                    .builder()
                    .codigo(nivel)
                    .nome(nivel.name())
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .id(departamentoId)
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
            .canal(ECanal.D2D_PROPRIO)
            .build();
    }

    public static UsuarioAtivacaoDto umUsuarioAtivacaoDto() {
        return UsuarioAtivacaoDto.builder()
            .idUsuario(10)
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

    public static List<EquipeVendaUsuarioResponse> listaVazia() {
        var lista = new ArrayList<EquipeVendaUsuarioResponse>();
        return lista;
    }

    public static Usuario criaNovoUsuario(int cargoId, CodigoDepartamento departamento) {
        return Usuario.builder().id(1)
            .cargo(new Cargo(cargoId))
            .departamento(new Departamento(3))
            .build();
    }

    public static EquipeVendaUsuarioResponse criaEquipeVendaUsuarioResponse() {
        return EquipeVendaUsuarioResponse.builder().id(1)
            .build();
    }

    public static UsuarioInativacaoDto umUsuarioInativoDto() {
        return UsuarioInativacaoDto.builder()
            .idUsuario(1)
            .codigoMotivoInativacao(CodigoMotivoInativacao.DEMISSAO)
            .build();
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

    public static Usuario umUsuarioAtivo() {
        return Usuario.builder()
            .id(10)
            .cpf("98471883007")
            .nome("Usuario Ativo")
            .situacao(ESituacao.A)
            .email("usuarioativo@email.com")
            .build();
    }

    public static Usuario umUsuarioInativo() {
        return Usuario.builder()
            .id(11)
            .cpf("31114231827")
            .nome("Usuario Inativo")
            .situacao(ESituacao.I)
            .email("usuarioinativo@email.com")
            .build();
    }

    public static Usuario umUsuarioBackoffice() {
        return Usuario.builder()
            .nome("Backoffice")
            .cargo(new Cargo(110))
            .departamento(new Departamento(69))
            .organizacao(new Organizacao(5))
            .cpf("097.238.645-92")
            .email("usuario@teste.com")
            .telefone("43995565661")
            .hierarquiasId(List.of())
            .usuariosHierarquia(new HashSet<>())
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

    public static List<Usuario> umaUsuariosList() {
        return List.of(
            Usuario.builder()
                .id(1)
                .nome("Caio")
                .loginNetSales("H")
                .email("caio@teste.com")
                .situacao(ESituacao.A)
                .build(),
            Usuario.builder()
                .id(2)
                .nome("Mario")
                .loginNetSales("QQ")
                .email("mario@teste.com")
                .situacao(ESituacao.I)
                .build(),
            Usuario.builder()
                .id(3)
                .nome("Maria")
                .loginNetSales("LOG")
                .email("maria@teste.com")
                .situacao(ESituacao.R)
                .build()
        );
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

    public static UsuarioHierarquia umUsuarioHierarquia() {
        return UsuarioHierarquia.builder()
            .usuarioSuperior(umUsuarioSuperior())
            .usuario(new Usuario(100))
            .usuarioCadastro(new Usuario(103))
            .dataCadastro(LocalDateTime.now())
            .build();
    }

    public static Usuario umUsuarioSuperior() {
        return Usuario.builder()
            .id(100)
            .telefone("43 3322-0000")
            .cpf("097.238.645-92")
            .situacao(ESituacao.A)
            .cargo(umCargoSupervisorOperacao())
            .departamento(umDepartamentoComercial())
            .nome("RENATO")
            .email("RENATO@GMAIL.COM")
            .situacao(ESituacao.A)
            .unidadesNegocios(List.of(
                umaUnidadeNegocio(CodigoUnidadeNegocio.CLARO_RESIDENCIAL),
                umaUnidadeNegocio(CodigoUnidadeNegocio.RESIDENCIAL_COMBOS)))
            .empresas(List.of(umaEmpresa()))
            .build();
    }

    public static Cargo umCargoSupervisorOperacao() {
        return Cargo.builder()
            .id(1)
            .codigo(CodigoCargo.SUPERVISOR_OPERACAO)
            .nivel(umNivelOperacao())
            .nome(CodigoCargo.SUPERVISOR_OPERACAO.name())
            .situacao(ESituacao.A)
            .build();
    }

    public static Nivel umNivelOperacao() {
        return Nivel.builder()
            .id(1)
            .codigo(OPERACAO)
            .nome(OPERACAO.name())
            .build();
    }

    public static Departamento umDepartamentoComercial() {
        return Departamento.builder()
            .id(1)
            .codigo(CodigoDepartamento.COMERCIAL)
            .nome(CodigoDepartamento.COMERCIAL.name())
            .build();
    }

    public static UnidadeNegocio umaUnidadeNegocio(CodigoUnidadeNegocio codigoUnidadeNegocio) {
        return UnidadeNegocio.builder()
            .codigo(codigoUnidadeNegocio)
            .nome(codigoUnidadeNegocio.name())
            .situacao(ESituacao.A)
            .build();
    }

    public static Empresa umaEmpresa() {
        return Empresa.builder()
            .id(1)
            .codigo(CodigoEmpresa.CLARO_RESIDENCIAL)
            .nome(CodigoEmpresa.CLARO_RESIDENCIAL.name())
            .build();
    }

    public static Usuario umUsuarioComLoginNetSales(int id) {
        return Usuario.builder()
            .id(id)
            .nome("UM USUARIO COM LOGIN")
            .loginNetSales("UM LOGIN NETSALES")
            .cargo(Cargo.builder()
                .codigo(CodigoCargo.VENDEDOR_ATIVO_LOCAL_PROPRIO)
                .nivel(Nivel.builder().codigo(CodigoNivel.ATIVO_LOCAL_PROPRIO).build())
                .build())
            .cpf("123.456.887-91")
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuarioSemLoginNetSales(int id) {
        var usuario = umUsuarioComLoginNetSales(id);
        usuario.setLoginNetSales(null);
        return usuario;
    }

    public static Usuario umUsuarioDoIdECodigoCargo(int id, CodigoCargo codigoCargo) {
        return Usuario.builder()
            .id(id)
            .nome("FULANO DE TESTE")
            .email("TESTE@TESTE.COM")
            .cargo(Cargo.builder()
                .codigo(codigoCargo)
                .nivel(Nivel.builder().codigo(CodigoNivel.XBRAIN).build())
                .build())
            .cpf("123.456.887-91")
            .situacao(ESituacao.A)
            .build();
    }

    public static Page<Usuario> umaPageUsuario(PageRequest pageRequest, List<Usuario> usuariosList) {
        return new PageImpl<>(
            usuariosList,
            pageRequest,
            usuariosList.size());
    }

    public static UsuarioPredicate umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List<Integer> ids) {
        var predicate = new UsuarioPredicate();
        predicate.comCodigosCargos(FeederUtil.CARGOS_BACKOFFICE_AND_SOCIO_PRINCIPAL_AA);
        predicate.comIds(ids);
        return predicate;
    }

    public static List<Usuario> umaListaUsuariosExecutivosAtivo() {
        return List.of(
            Usuario.builder()
                .id(1)
                .nome("JOSÉ")
                .email("JOSE@HOTMAIL.COM")
                .situacao(ESituacao.A)
                .departamento(Departamento.builder()
                    .id(1)
                    .codigo(CodigoDepartamento.AGENTE_AUTORIZADO)
                    .build())
                .cargo(Cargo.builder()
                    .id(1)
                    .codigo(CodigoCargo.EXECUTIVO)
                    .nivel(Nivel.builder()
                        .id(1)
                        .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                        .build())
                    .build())
                .build(),
            Usuario.builder()
                .id(2)
                .nome("HIGOR")
                .email("HIGOR@HOTMAIL.COM")
                .situacao(ESituacao.A)
                .departamento(Departamento.builder()
                    .id(1)
                    .codigo(CodigoDepartamento.AGENTE_AUTORIZADO)
                    .build())
                .cargo(Cargo.builder()
                    .id(1)
                    .codigo(CodigoCargo.EXECUTIVO)
                    .nivel(Nivel.builder()
                        .id(1)
                        .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                        .build())
                    .build())
                .build()
        );
    }

    public static List<UnidadeNegocio> umaListaUnidadesNegocio() {
        return List.of(
            umaUnidadeNegocio(CodigoUnidadeNegocio.CLARO_RESIDENCIAL),
            umaUnidadeNegocio(CodigoUnidadeNegocio.RESIDENCIAL_COMBOS)
        );
    }

    public static List<File> umaListFotos() {
        return List.of(
            new File("src/test/resources/foto_usuario/file.png"),
            new File("src/test/resources/foto_usuario/download.jpeg")
        );
    }
}
