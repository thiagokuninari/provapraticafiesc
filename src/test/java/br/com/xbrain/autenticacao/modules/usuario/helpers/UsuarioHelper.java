package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.Marca;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.EMPRESARIAL;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.RESIDENCIAL;
import static br.com.xbrain.autenticacao.modules.comum.helper.OrganizacaoHelper.umaOrganizacaoCallink;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.MSO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.PAP_PREMIUM;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.*;
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
            .cargo(umCargoAdministrador())
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

    public static Usuario umUsuarioComCargo(Integer usuarioId, Cargo cargo) {
        return Usuario.builder()
            .id(usuarioId)
            .situacao(ESituacao.A)
            .alterarSenha(Eboolean.F)
            .cpf("38957979875")
            .dataCadastro(LocalDateTime.now())
            .email("ADMIN@XBRAIN.COM.BR")
            .nome("ADMIN")
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .telefone("99999")
            .cargo(cargo)
            .departamento(umDepartamentoAdministrador())
            .build();
    }

    public static List<Usuario> umaListaDeUsuariosDeLondrina() {
        return List.of(
            umUsuarioComCargo(1, umCargoExecutivo()),
            umUsuarioComCargo(2, umCargoExecutivo()),
            umUsuarioComCargo(3, umCargoGerente()),
            umUsuarioComCargo(4, umCargoGerente()),
            umUsuarioComCargo(5, umCargoCoordernador()),
            umUsuarioComCargo(6, umCargoCoordernador()),
            umUsuarioComCargo(7, umCargoExecutivoHunter()),
            umUsuarioComCargo(8, umCargoExecutivoHunter())
        );
    }

    public static List<Usuario> umaListaDeUsuariosDeSaoPaulo() {
        return List.of(
            umUsuarioComCargo(1, umCargoExecutivo()),
            umUsuarioComCargo(2, umCargoExecutivo()),
            umUsuarioComCargo(3, umCargoGerente()),
            umUsuarioComCargo(4, umCargoGerente()),
            umUsuarioComCargo(5, umCargoCoordernador()),
            umUsuarioComCargo(7, umCargoExecutivoHunter()),
            umUsuarioComCargo(8, umCargoExecutivoHunter())
        );
    }

    public static List<Usuario> umaListaDeUsuariosDeRioDeJaneiro() {
        return List.of(
            umUsuarioComCargo(1, umCargoExecutivo()),
            umUsuarioComCargo(3, umCargoGerente()),
            umUsuarioComCargo(4, umCargoGerente()),
            umUsuarioComCargo(5, umCargoCoordernador()),
            umUsuarioComCargo(7, umCargoExecutivoHunter()),
            umUsuarioComCargo(8, umCargoExecutivoHunter())
        );
    }

    public static List<Usuario> umaListaDeUsuariosDeCapitolio() {
        return List.of(
            umUsuarioComCargo(1, umCargoExecutivo()),
            umUsuarioComCargo(3, umCargoGerente()),
            umUsuarioComCargo(7, umCargoExecutivoHunter())
        );
    }

    public static Usuario umCoordenador() {
        return Usuario.builder()
            .id(11122)
            .nome("Coordenador operacao ativo local")
            .email("COORDENADOR_OPERACAO@NET.COM.BR")
            .telefone("99999")
            .cpf("54564564654")
            .cargo(umCargo(4, CodigoCargo.COORDENADOR_OPERACAO))
            .departamento(umDepartamento(3, "Comercial"))
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .alterarSenha(Eboolean.F)
            .situacao(ESituacao.A)
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .cidades(Set.of(
                UsuarioCidade.criar(new Usuario(11122), 1200, 10),
                UsuarioCidade.criar(new Usuario(11122), 1300, 10)
            ))
            .build();
    }

    public static Usuario outroCoordenador() {
        return Usuario.builder()
            .id(11126)
            .nome("Coordenador sem site operacao ativo local")
            .email("COORDENADOR3_OPERACAO@NET.COM.BR")
            .telefone("99999")
            .cpf("54564564654")
            .cargo(umCargo(4, CodigoCargo.COORDENADOR_OPERACAO))
            .departamento(umDepartamento(3, "Comercial"))
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .alterarSenha(Eboolean.F)
            .situacao(ESituacao.A)
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .cidades(Set.of(
                UsuarioCidade.criar(new Usuario(11126), 1700, 10)
            ))
            .build();
    }

    public static Usuario umSupervisor() {
        return Usuario.builder()
            .id(11127)
            .nome("Supervisor2 operacao ativo local")
            .email("SUPERVISOR3_OPERACAO@NET.COM.BR")
            .telefone("99999")
            .cpf("54564564654")
            .cargo(umCargo(10, CodigoCargo.SUPERVISOR_OPERACAO))
            .departamento(umDepartamento(3, "Comercial"))
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .alterarSenha(Eboolean.F)
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario outroSupervisor() {
        return Usuario.builder()
            .id(11123)
            .nome("Supervisor operacao ativo local")
            .email("SUPERVISOR_OPERACAO@NET.COM.BR")
            .telefone("99999")
            .cpf("54564564654")
            .usuariosHierarquia(Set.of(UsuarioHierarquia.criar(new Usuario(11123), 11122, 10)))
            .cargo(umCargo(10, CodigoCargo.SUPERVISOR_OPERACAO))
            .departamento(umDepartamento(3, "Comercial"))
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .alterarSenha(Eboolean.F)
            .situacao(ESituacao.A)
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .build();
    }

    public static Usuario umUsuarioMsoAnalistaClaroPessoal() {
        return Usuario.builder()
            .id(366)
            .nome("MSO ANALISTA ADM CLARO PESSOAL")
            .email("MSO_ANALISTAADM_CLAROMOVEL_PESSOAL@NET.COM.BR")
            .telefone("99999")
            .cpf("28667582506")
            .cargo(umCargoMsoAnalista())
            .departamento(umDepartamentoAdministrativoMso())
            .organizacao(umaOrganizacaoCallink())
            .dataCadastro(LocalDateTime.now())
            .situacao(ESituacao.A)
            .build();
    }

    public static Usuario umUsuarioHelpDesk() {
        return Usuario.builder()
            .id(101)
            .nome("HELPDESK")
            .email("HELPDESK@XBRAIN.COM.BR")
            .telefone("99999")
            .cpf("65710871036")
            .unidadesNegocios(List.of(new UnidadeNegocio(1)))
            .empresas(List.of(Empresa.builder()
                .id(1)
                .nome("Claro Móvel")
                .marca(new Marca(1))
                .codigo(CodigoEmpresa.CLARO_MOVEL)
                .unidadeNegocio(new UnidadeNegocio(1))
                .build()))
            .usuariosHierarquia(
                Set.of(
                    UsuarioHierarquia.criar(new Usuario(101), 104, 100),
                    UsuarioHierarquia.criar(new Usuario(101), 369, 100),
                    UsuarioHierarquia.criar(new Usuario(101), 370, 100)
                ))
            .departamento(new Departamento(51))
            .organizacao(umaOrganizacaoCallink())
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .alterarSenha(Eboolean.F)
            .situacao(ESituacao.A)
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO))
            .organizacao(new Organizacao(2))
            .cidades(Set.of(UsuarioCidade.criar(new Usuario(100), 5578, 100)))
            .cargo(umCargoAaSupervisorXbrain())
            .build();
    }

    public static UsuarioAfastamento umUsuarioAfastamento() {
        return UsuarioAfastamento.builder()
            .id(1)
            .usuario(umUsuarioHelpDesk())
            .dataCadastro(LocalDateTime.now())
            .inicio(LocalDate.of(2019, 1, 1))
            .fim(LocalDate.of(2019, 2, 1))
            .build();
    }

    public static UsuarioFerias umUsuarioFerias() {
        return UsuarioFerias.builder()
            .id(1)
            .usuario(umUsuarioHelpDesk())
            .dataCadastro(LocalDateTime.now())
            .inicio(LocalDate.of(2019, 1, 1))
            .fim(LocalDate.of(2019, 2, 1))
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticadoAdmin() {
        return UsuarioAutenticado.builder()
            .usuario(umUsuarioAdmin())
            .id(100)
            .nome("ADMIN")
            .email("ADMIN@XBRAIN.COM.BR")
            .cargoId(50)
            .cargo("Administrador")
            .departamentoId(50)
            .departamento("Administrador")
            .nivel("XBRAIN")
            .nivelId(4)
            .cpf("38957979875")
            .situacao(ESituacao.A)
            .empresasNome(List.of("Xbrain"))
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole()),
                new SimpleGrantedAuthority(AUT_VISUALIZAR_USUARIO.getRole()),
                new SimpleGrantedAuthority(AUT_VISUALIZAR_USUARIOS_AA.getRole())))
            .nivelCodigo("XBRAIN")
            .departamentoCodigo(CodigoDepartamento.ADMINISTRADOR)
            .cargoCodigo(CodigoCargo.ADMINISTRADOR)
            .organizacaoId(1)
            .organizacaoCodigo("BCC")
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO, ECanal.ATIVO_PROPRIO, ECanal.D2D_PROPRIO))
            .build();
    }

    public static Usuario umUsuarioAdmin() {
        return Usuario.builder()
            .id(100)
            .nome("ADMIN")
            .email("ADMIN@XBRAIN.COM.BR")
            .cpf("38957979875")
            .unidadesNegocios(List.of(new UnidadeNegocio(3)))
            .cidades(Set.of(UsuarioCidade.criar(new Usuario(100), 5578, 100)))
            .configuracao(umaConfiguracao())
            .empresas(List.of(new Empresa(1, "XBRAIN", CodigoEmpresa.XBRAIN)))
            .cargo(umCargoAdministrador())
            .departamento(umDepartamentoAdministrador())
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .alterarSenha(Eboolean.F)
            .situacao(ESituacao.A)
            .build();
    }

    private static Configuracao umaConfiguracao() {
        return new Configuracao(new Usuario(100), new Usuario(100), LocalDateTime.now(), 7006);
    }

    private static Usuario umUsuarioAdminSimples(Integer id, String nome, String email) {
        return Usuario.builder()
            .id(id)
            .nome(nome)
            .email(email)
            .cargo(new Cargo(50))
            .departamento(new Departamento(50))
            .build();
    }

    public static List<Usuario> umaListaDeUsuariosAdminSimples() {
        return List.of(
            umUsuarioAdminSimples(101, "USUARIO ADMIN 1", "ADMIN1@XBRAIN.COM.BR"),
            umUsuarioAdminSimples(102, "USUARIO ADMIN 2", "ADMIN2@XBRAIN.COM.BR"),
            umUsuarioAdminSimples(103, "USUARIO ADMIN 3", "ADMIN3@XBRAIN.COM.BR"),
            umUsuarioAdminSimples(104, "USUARIO ADMIN 4", "ADMIN4@XBRAIN.COM.BR")
        );
    }
}
