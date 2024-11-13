package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.*;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.Marca;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.R;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.EMPRESARIAL;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.RESIDENCIAL;
import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper.organizacaoEmpresa;
import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeHelper.umaFuncionalidadeBko;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.PAP;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.PAP_PREMIUM;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umaListaSubcanal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoAtivoProprioComCargo;

@UtilityClass
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

    public static Usuario umUsuario(CodigoCargo cargo) {
        return Usuario.builder()
            .id(3)
            .situacao(ESituacao.I)
            .usuarioCadastro(Usuario.builder().id(1).build())
            .cargo(Cargo.builder()
                .id(3)
                .codigo(cargo)
                .build())
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

    public static Usuario umUsuarioComDadosNetSales() {
        return Usuario.builder()
            .loginNetSales("login123")
            .canalNetSalesCodigo("CANAL NETSALES")
            .nomeEquipeVendaNetSales("EQUIPE VENDA NETSALES")
            .codigoEquipeVendaNetSales("codigo123")
            .canalNetSalesId(1)
            .build();
    }

    public static Usuario umUsuarioComDadosNetSales2() {
        return Usuario.builder()
            .loginNetSales("login123")
            .canalNetSalesCodigo("CANAL NETSALES")
            .nomeEquipeVendaNetSales("EQUIPE VENDA NETSALES")
            .codigoEquipeVendaNetSales("codigo123")
            .canalNetSalesId(1)
            .build();
    }

    public static Usuario umUsuarioComDadosNetSales3() {
        return Usuario.builder()
            .loginNetSales("login123")
            .canalNetSalesCodigo("CANAL NETSALES")
            .nomeEquipeVendaNetSales("EQUIPE VENDA NETSALES")
            .codigoEquipeVendaNetSales("codigo123")
            .canalNetSalesId(1)
            .build();
    }

    public static List<Usuario> umaListaDeUsuariosComDadosNetSales() {
        return List.of(
            umUsuarioComDadosNetSales(),
            umUsuarioComDadosNetSales2(),
            umUsuarioComDadosNetSales3());
    }

    public static Usuario umUsuarioOperacaoComSubCanal(Integer usuarioId,
                                                       Integer subCanalId,
                                                       ETipoCanal codigoSubCanal) {
        return Usuario.builder()
            .id(usuarioId)
            .nome("NAKANO")
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
                    .nome(PAP.getDescricao())
                    .situacao(A)
                    .build()))
            .usuarioCadastro(umUsuarioMsoConsultor(3, PAP_PREMIUM))
            .usuariosHierarquia(new HashSet<>())
            .situacao(A)
            .loginNetSales("login123")
            .canalNetSalesId(1)
            .canalNetSalesCodigo("UM CANAL NETSALES")
            .codigoEquipeVendaNetSales("123")
            .nomeEquipeVendaNetSales("EQUIPE DE VENDA NETSALES")
            .build();
    }

    public static Usuario umUsuarioMsoConsultor(Integer subCanalId, ETipoCanal codigoSubCanal) {
        return Usuario.builder()
            .id(23)
            .cargo(Cargo.builder()
                .codigo(MSO_CONSULTOR)
                .nivel(Nivel.builder()
                    .id(2)
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

    public static UsuarioDto umUsuarioDto() {
        return UsuarioDto.builder()
            .id(1)
            .email("teste@gmail.com")
            .cpf("123456")
            .build();
    }

    public static UsuarioDto umUsuarioDto(Integer usuarioId) {
        return UsuarioDto.builder()
            .id(usuarioId)
            .nome("MSO FEEDER")
            .cpf("873.616.099-70")
            .nivelId(2)
            .organizacaoId(1)
            .usuarioCadastroId(1)
            .tiposFeeder(Set.of(EMPRESARIAL, RESIDENCIAL))
            .build();
    }

    public static UsuarioDto umUsuarioMsoBackofficeDto(Integer usuarioId) {
        return UsuarioDto.builder()
            .id(usuarioId)
            .nome("MSO BACKOFFICE")
            .cpf("873.616.099-70")
            .email("MSOBACKOFFICE@TESTE.COM.BR")
            .nivelId(2)
            .organizacaoId(1)
            .usuarioCadastroId(1)
            .subNiveisIds(Set.of(1))
            .build();
    }

    public static UsuarioDto umUsuarioFeederDto(Integer usuarioId, String usuarioEmail) {
        return UsuarioDto.builder()
            .id(usuarioId)
            .email(usuarioEmail)
            .nivelCodigo(FEEDER)
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
                    .codigo(MSO)
                    .build())
                .build())
            .tiposFeeder(Set.of(EMPRESARIAL, RESIDENCIAL))
            .situacao(ESituacao.A)
            .subNiveis(new HashSet<>())
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
            .subNiveisIds(Set.of())
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

    public static Usuario umCoordenadorD2d() {
        return Usuario.builder()
            .id(11122)
            .nome("Coordenador operacao D2D")
            .email("COORDENADOR_OPERACAO@NET.COM.BR")
            .telefone("99999")
            .cpf("54564564654")
            .cargo(umCargo(4, CodigoCargo.COORDENADOR_OPERACAO))
            .departamento(umDepartamento(3, "Comercial"))
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .alterarSenha(Eboolean.F)
            .situacao(ESituacao.A)
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .subCanais(umaListaSubcanal())
            .empresas(List.of(Empresa.builder().codigo(CodigoEmpresa.NET).build()))
            .usuariosHierarquia(Set.of())
            .unidadesNegocios(List.of(new UnidadeNegocio()))
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

    public static Usuario umDiretor() {
        return Usuario.builder()
            .id(11126)
            .nome("diretor sem site operacao ativo local")
            .email("COORDENADOR3_OPERACAO@NET.COM.BR")
            .telefone("99999")
            .cpf("54564564654")
            .cargo(umCargo(4, CodigoCargo.DIRETOR_OPERACAO))
            .departamento(umDepartamento(3, "Comercial"))
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .alterarSenha(Eboolean.F)
            .situacao(ESituacao.A)
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .subCanais(Set.of(SubCanal.builder().codigo(PAP_PREMIUM).build()))
            .empresas(List.of(Empresa.builder().codigo(CodigoEmpresa.NET).build()))
            .usuariosHierarquia(Set.of())
            .unidadesNegocios(List.of(new UnidadeNegocio()))
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

    public static Usuario umSupervisorD2d() {
        return Usuario.builder()
            .id(11127)
            .nome("Supervisor2 operacao ativo local")
            .email("SUPERVISOR3_OPERACAO@NET.COM.BR")
            .telefone("99999")
            .cpf("54564564654")
            .cargo(umCargo(10, SUPERVISOR_OPERACAO))
            .departamento(umDepartamento(3, "Comercial"))
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .alterarSenha(Eboolean.F)
            .situacao(A)
            .subCanais(Set.of(SubCanal.builder().codigo(PAP_PREMIUM).build()))
            .empresas(List.of(Empresa.builder().codigo(CodigoEmpresa.NET).build()))
            .cidades(Set.of(new UsuarioCidade()))
            .usuariosHierarquia(Set.of())
            .unidadesNegocios(List.of(new UnidadeNegocio()))
            .canais(Set.of(ECanal.D2D_PROPRIO))
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
            .dataCadastro(LocalDateTime.now())
            .senha("$2a$10$5Km7U7CyDD5VIrkJPXPK8.px0hJE9n.NgGx2tGRa/Gu3e3xEumipm")
            .alterarSenha(Eboolean.F)
            .situacao(ESituacao.A)
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO))
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

    public static List<Usuario> umUsuariosListComCargoSemPermissao() {
        return List.of(
            Usuario.builder()
                .id(1)
                .nome("Caio")
                .loginNetSales("H")
                .email("caio@teste.com")
                .situacao(A)
                .cargo(umCargoAnalistaOperacao())
                .build(),
            Usuario.builder()
                .id(2)
                .nome("Mario")
                .loginNetSales("QQ")
                .email("mario@teste.com")
                .situacao(ESituacao.I)
                .cargo(umCargoAnalistaOperacao())
                .build(),
            Usuario.builder()
                .id(3)
                .nome("Maria")
                .loginNetSales("LOG")
                .email("maria@teste.com")
                .situacao(ESituacao.R)
                .cargo(umCargoAnalistaOperacao())
                .build()
        );
    }

    public static List<Usuario> umUsuariosListComCargoComPermissao() {
        return List.of(
            Usuario.builder()
                .id(88)
                .nome("Caio")
                .loginNetSales("H")
                .email("caio@teste.com")
                .situacao(A)
                .cargo(umCargoAaSocio())
                .usuarioCadastro(umUsuarioCadastro())
                .build()
        );
    }

    public static UsuarioMqRequest umUsuarioMqRequestCompleto() {
        return UsuarioMqRequest.builder()
            .id(123)
            .cpf("123456789")
            .nome("Fulano")
            .email("fulano@tgmail.com.br")
            .telefone("43988887777")
            .rg("123456")
            .loginNetSales("741")
            .orgaoExpedidor("963")
            .situacao(A)
            .nascimento(LocalDateTime.of(1990, 10, 19, 14, 52))
            .nivel(AGENTE_AUTORIZADO)
            .departamento(CodigoDepartamento.AGENTE_AUTORIZADO)
            .cargo(GERENTE_OPERACAO)
            .unidadesNegocio(List.of(CodigoUnidadeNegocio.CLARO_RESIDENCIAL))
            .empresa(List.of(CodigoEmpresa.CLARO_RESIDENCIAL))
            .usuarioCadastroId(69)
            .usuarioCadastroNome("Alguem")
            .colaboradorId(89)
            .agenteAutorizadoId(77)
            .agenteAutorizadoFeeder(ETipoFeeder.RESIDENCIAL)
            .isCadastroSocioPrincipal(true)
            .equipeTecnica(true)
            .build();
    }

    public static UsuarioDto umUsuarioDtoParse() {
        return UsuarioDto.builder()
            .id(123)
            .cpf("123456789")
            .nome("Fulano")
            .email("fulano@tgmail.com.br")
            .telefone("43988887777")
            .rg("123456")
            .loginNetSales("741")
            .orgaoExpedidor("963")
            .situacao(A)
            .nascimento(LocalDateTime.of(1990, 10, 19, 14, 52))
            .nivelCodigo(AGENTE_AUTORIZADO)
            .departamentoId(40)
            .cargoCodigo(GERENTE_OPERACAO)
            .cargoId(47)
            .usuarioCadastroId(69)
            .usuarioCadastroNome("Alguem")
            .agenteAutorizadoId(77)
            .build();
    }

    public static Usuario umUsuarioConvertFrom() {
        return Usuario.builder()
            .id(123)
            .cpf("123456789")
            .nome("Fulano")
            .email("fulano@tgmail.com.br")
            .telefone("43988887777")
            .rg("123456")
            .loginNetSales("741")
            .orgaoExpedidor("963")
            .situacao(A)
            .nascimento(LocalDateTime.of(1990, 10, 19, 14, 52))
            .agenteAutorizadoId(77)
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticadoAa() {
        return umUsuarioAutenticadoAtivoProprioComCargo(1, GERENTE_OPERACAO,
            CodigoDepartamento.AGENTE_AUTORIZADO);
    }

    public static Usuario umUsuarioOperadorTelevendas() {
        return Usuario.builder()
            .id(101)
            .nome("OPERADOR TELEVENDAS")
            .email("SITECOORDENADOR@XBRAIN.COM.BR")
            .cargo(Cargo.builder().codigo(OPERACAO_TELEVENDAS).build())
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .build();
    }

    public static Usuario umUsuarioSocialHub(String email, Integer mercadoDesenvolvimentoId, CodigoNivel codigoNivel) {
        return Usuario.builder()
            .id(1)
            .cpf("097.238.645-92")
            .nome("Seiya")
            .email(email)
            .situacao(ESituacao.A)
            .usuariosHierarquia(new HashSet<>())
            .usuarioCadastro(new Usuario(1))
            .hierarquiasId(List.of(2))
            .territorioMercadoDesenvolvimentoId(mercadoDesenvolvimentoId)
            .canais(Set.of(ECanal.INTERNET))
            .departamento(Departamento.builder().id(1).nome("teste").build())
            .cargo(Cargo.builder()
                .id(1)
                .codigo(OPERACAO_TELEVENDAS)
                .nivel(Nivel.builder()
                    .id(1)
                    .codigo(codigoNivel)
                    .build())
                .build())
            .build();
    }

    public static Usuario umUsuarioOperadorBko(Integer id, String nome, String email) {
        return Usuario.builder()
            .id(id)
            .nome(nome)
            .email(email)
            .organizacaoEmpresa(organizacaoEmpresa())
            .build();
    }

    public static List<Usuario> umaListaDeUsuariosOperadorBko() {
        return List.of(
            umUsuarioOperadorBko(4444, "Khada Jhin", "khadajhin4@teste.com"),
            umUsuarioOperadorBko(2000, "Veigar", "puromalencarnado@teste.com")
        );
    }

    public static EquipeVendaUsuarioRequest umaEquipeVendaUsuarioRequest() {
        return EquipeVendaUsuarioRequest.builder()
            .usuarioId(1)
            .usuarioNome("NAKANO")
            .trocaDeSubCanal(false)
            .trocaDeNome(true)
            .build();
    }

    public static EquipeVendaUsuarioRequest umaEquipeVendaUsuarioRequestComTrocaDeSubcanal() {
        return EquipeVendaUsuarioRequest.builder()
            .usuarioId(1)
            .usuarioNome("NAKANO")
            .trocaDeSubCanal(true)
            .trocaDeNome(false)
            .build();
    }

    public static Usuario umUsuarioPapIndireto() {
        return Usuario.builder()
            .id(1)
            .nome("UM USUARIO PAP INDIRETO")
            .email("umusuariopapindireto@net.com.br")
            .cpf("111.111.111-11")
            .dataCadastro(LocalDateTime.of(2018, 01, 01, 15, 00, 00))
            .situacao(A)
            .build();
    }

    public static Usuario umOutroUsuarioPapIndireto() {
        return Usuario.builder()
            .id(2)
            .nome("UM OUTRO USUARIO PAP INDIRETO")
            .email("umoutrousuariopapindireto@net.com.br")
            .cpf("222.222.222-22")
            .dataCadastro(LocalDateTime.of(2018, 01, 01, 15, 00, 00))
            .situacao(A)
            .build();
    }

    public static Usuario umUsuarioPapIndiretoRemanejado() {
        return Usuario.builder()
            .id(3)
            .nome("UM USUARIO PAP INDIRETO REMANEJADO")
            .email("umusuariopapindiretoremanejado@net.com.br")
            .cpf("111.111.111-11")
            .dataCadastro(LocalDateTime.of(2017, 01, 01, 15, 00, 00))
            .situacao(R)
            .build();
    }

    public static Usuario umUsuarioPapIndiretoRemanejadoParaAntigoAA() {
        return Usuario.builder()
            .id(4)
            .nome("UM USUARIO PAP INDIRETO REMANEJADO PARA ANTIGO AA")
            .email("umusuariopapindireto@net.com.br")
            .cpf("111.111.111-11")
            .dataCadastro(LocalDateTime.of(2020, 01, 01, 15, 00, 00))
            .situacao(A)
            .build();
    }

    public static UsuarioDtoVendas umUsuarioDtoVendasPapIndireto() {
        return UsuarioDtoVendas.builder()
            .id(1)
            .nome("UM USUARIO DTO VENDAS PAP INDIRETO")
            .email("umusuariopapindireto@net.com.br")
            .agenteAutorizadoCnpj("64.262.572/0001-21")
            .agenteAutorizadoRazaoSocial("Razao Social Teste")
            .agenteAutorizadoId(100)
            .situacao(A)
            .build();
    }

    public static UsuarioDtoVendas umOutroUsuarioDtoVendasPapIndireto() {
        return UsuarioDtoVendas.builder()
            .id(2)
            .nome("UM OUTRO USUARIO DTO VENDAS PAP INDIRETO")
            .email("umoutrousuariopapindireto@net.com.br")
            .agenteAutorizadoCnpj("64.262.572/0001-22")
            .agenteAutorizadoRazaoSocial("Razao Social Teste 2")
            .agenteAutorizadoId(101)
            .situacao(A)
            .build();
    }

    public static UsuarioDtoVendas umUsuarioDtoVendasPapIndiretoRemanejado() {
        return UsuarioDtoVendas.builder()
            .id(3)
            .nome("UM USUARIO DTO VENDAS PAP INDIRETO REMANEJADO")
            .email("umusuariopapindiretoremanejado@net.com.br")
            .agenteAutorizadoCnpj("64.262.572/0001-23")
            .agenteAutorizadoRazaoSocial("Razao Social Teste 3")
            .agenteAutorizadoId(103)
            .situacao(R)
            .build();
    }

    public static UsuarioDtoVendas umUsuarioDtoVendasPapIndiretoRemanejadoParaAntigoAA() {
        return UsuarioDtoVendas.builder()
            .id(4)
            .nome("UM USUARIO DTO VENDAS PAP INDIRETO REMANEJADO PARA ANTIGO AA")
            .email("umusuariopapindireto@net.com.br")
            .agenteAutorizadoCnpj("64.262.572/0001-23")
            .agenteAutorizadoRazaoSocial("Razao Social Teste 3")
            .agenteAutorizadoId(103)
            .situacao(A)
            .build();
    }

    public static UsuarioResponse umUsuarioResponse() {
        return UsuarioResponse
            .builder()
            .id(1)
            .aaId(100)
            .nome("UM USUARIO RESPONSE")
            .dataCadastro(LocalDateTime.of(2018, 12, 1, 0, 0))
            .cpf("111.111.111-11")
            .situacao(A)
            .build();
    }

    public static SubNivel umSubNivel(Integer id, String codigo, String nome,
                                      Set<CargoFuncionalidadeSubNivel> cargoFuncionalidadeSubNivels) {
        return SubNivel.builder()
            .id(id)
            .codigo(codigo)
            .nome(nome)
            .cargoFuncionalidadeSubNiveis(cargoFuncionalidadeSubNivels)
            .build();
    }

    public static Set<SubNivel> umSetDeSubNiveisComUmSubNivel() {
        return Set.of(umSubNivel(1, "BACKOFFICE", "BACKOFFICE",
            Set.of(umCargoFuncionalidadeSubNivel(1, null, umaFuncionalidadeBko(1, "Teste 1"))))
        );
    }

    public static Set<SubNivel> umSetDeSubNiveis() {
        return  Set.of(
            umSubNivel(2, "BACKOFFICE_CENTRALIZADO","BACKOFFICE CENTRALIZADO",
                Set.of(umCargoFuncionalidadeSubNivel(2, null, umaFuncionalidadeBko(2, "Teste 2")))),
            umSubNivel(3, "BACKOFFICE_SUPORTE_VENDAS", "BACKOFFICE SUPORTE DE VENDAS",
                Set.of(umCargoFuncionalidadeSubNivel(3, null, umaFuncionalidadeBko(3, "Teste 3"))))
        );
    }

    public static List<SubNivel> umaListaDeSubNiveis() {
        return  List.of(
            umSubNivel(1, "BACKOFFICE", "BACKOFFICE",
                Set.of(umCargoFuncionalidadeSubNivel(1, umCargoMsoConsultor(), umaFuncionalidadeBko(1, "Teste 1")))),
            umSubNivel(2, "BACKOFFICE_CENTRALIZADO", "BACKOFFICE CENTRALIZADO",
                Set.of(umCargoFuncionalidadeSubNivel(2, null, umaFuncionalidadeBko(2, "Teste 2")))),
            umSubNivel(3, "BACKOFFICE_QUALIDADE", "BACKOFFICE DE QUALIDADE",
                Set.of(umCargoFuncionalidadeSubNivel(3, umCargoMsoAnalista(), umaFuncionalidadeBko(3, "Teste 3")))),
            umSubNivel(4, "BACKOFFICE_SUPORTE_VENDAS", "BACKOFFICE SUPORTE DE VENDAS",
                Set.of(umCargoFuncionalidadeSubNivel(4, null, umaFuncionalidadeBko(4, "Teste 4"))))
        );
    }

    public static Set<SubNivel> umSetDeSubNiveisComCargo() {
        return  Set.of(
            umSubNivel(1, "BACKOFFICE", "BACKOFFICE",
                Set.of(umCargoFuncionalidadeSubNivel(1, umCargoMsoConsultor(), umaFuncionalidadeBko(1, "Teste 1")))),
            umSubNivel(2, "BACKOFFICE_CENTRALIZADO", "BACKOFFICE CENTRALIZADO",
                Set.of(umCargoFuncionalidadeSubNivel(2, umCargoMsoAnalista(), umaFuncionalidadeBko(2, "Teste 2")))),
            umSubNivel(3, "BACKOFFICE_QUALIDADE", "BACKOFFICE DE QUALIDADE",
                Set.of(umCargoFuncionalidadeSubNivel(3, umCargoMsoConsultor(), umaFuncionalidadeBko(3, "Teste 3")))),
            umSubNivel(4, "BACKOFFICE_SUPORTE_VENDAS", "BACKOFFICE SUPORTE DE VENDAS",
                Set.of(umCargoFuncionalidadeSubNivel(4, umCargoMsoAnalista(), umaFuncionalidadeBko(4, "Teste 4"))))
        );
    }

    public static CargoFuncionalidadeSubNivel umCargoFuncionalidadeSubNivel(Integer id, Cargo cargo,
                                                                            Funcionalidade funcionalidade) {
        return CargoFuncionalidadeSubNivel.builder()
            .id(id)
            .cargo(cargo)
            .funcionalidade(funcionalidade)
            .build();
    }

    public static Usuario umUsuarioComCargoEOrganizacao(Integer cargoId, Integer organizacaoId) {
        return Usuario.builder()
            .id(100)
            .cargo(Cargo.builder().id(cargoId).codigo(OPERADOR_SUPORTE_VENDAS).build())
            .organizacaoEmpresa(OrganizacaoEmpresa.builder()
                .id(organizacaoId)
                .nivel(Nivel.builder().codigo(BACKOFFICE_SUPORTE_VENDAS).build())
                .build())
            .email("email@google.com")
            .build();
    }
}
