package helpers;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.site.dto.SiteCidadeResponse;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanalDto;
import static java.util.Collections.singleton;

public class TestBuilders {

    public static UsuarioHierarquiaResponse umUsuarioHierarquia() {
        return UsuarioHierarquiaResponse.builder()
            .id(100)
            .nome("XBRAIN")
            .cargoNome("COORDENADOR_OPERACAO")
            .build();
    }

    public static UsuarioConfiguracaoDto umUsuarioConfiguracaoDto() {
        UsuarioConfiguracaoDto dto = new UsuarioConfiguracaoDto();
        dto.setRamal(1000);
        dto.setUsuario(100);
        return dto;
    }

    public static UsuarioDadosAcessoRequest umEsqueciSenha() {
        UsuarioDadosAcessoRequest dto = new UsuarioDadosAcessoRequest();
        dto.setEmailAtual("HELPDESK@XBRAIN.COM.BR");
        return dto;
    }

    public static UsuarioConfiguracaoDto umUsuarioComRamalDuplicado() {
        UsuarioConfiguracaoDto dto = new UsuarioConfiguracaoDto();
        dto.setRamal(1008);
        dto.setUsuario(105);
        return dto;
    }

    public static List<UsuarioConfiguracaoDto> umaListaDeUsuarioComRamal() {
        UsuarioConfiguracaoDto dto1 = new UsuarioConfiguracaoDto();
        dto1.setRamal(1008);
        dto1.setUsuario(105);
        UsuarioConfiguracaoDto dto2 = new UsuarioConfiguracaoDto();
        dto2.setRamal(7006);
        dto2.setUsuario(100);
        return List.of(dto1, dto2);
    }

    public static List<UsuarioConfiguracaoDto> umaListaDeUsuarioComRamalInconsistente() {
        UsuarioConfiguracaoDto dto1 = new UsuarioConfiguracaoDto();
        dto1.setRamal(1008);
        dto1.setUsuario(110);
        return List.of(dto1);
    }

    public static List<UsuarioConfiguracaoDto> umaListDeUsuarioConfiguracaoDto() {
        UsuarioConfiguracaoDto dto = new UsuarioConfiguracaoDto();
        dto.setRamal(1000);
        dto.setUsuario(100);
        return List.of(dto);
    }

    public static UsuarioAutenticado buildUsuarioAutenticadoComTodosCanais() {
        return UsuarioAutenticado.builder()
            .usuario(Usuario.builder()
                .canais(Set.of(ECanal.D2D_PROPRIO, ECanal.ATIVO_PROPRIO))
                .build()).build();
    }

    public static Set<Usuario> umaListaSupervisores() {
        return Set.of(
            Usuario.builder()
                .id(1)
                .nome("RENATO")
                .situacao(ESituacao.A)
                .build(),
            Usuario.builder()
                .id(2)
                .nome("MARIA")
                .situacao(ESituacao.I)
                .build(),
            Usuario.builder()
                .id(3)
                .nome("JOAO")
                .situacao(ESituacao.R)
                .build()
        );
    }

    public static SiteRequest umSiteRequest() {
        return SiteRequest.builder()
            .nome("Site brandon big")
            .timeZone(BRT)
            .supervisoresIds(List.of(1, 2))
            .coordenadoresIds(List.of(3))
            .cidadesIds(List.of(1, 10))
            .estadosIds(List.of(5))
            .build();
    }

    public static List<Cidade> umListaCidades() {
        return List.of(
            Cidade.builder().id(1).nome("CIDADE 1").uf(umaUf(1, "UF 1", "PR")).build(),
            Cidade.builder().id(2).nome("CIDADE 2").uf(umaUf(2, "UF 2", "SP")).build()
        );
    }

    public static Uf umaUf(Integer id, String nome, String uf) {
        return Uf.builder().id(id).nome(nome).uf(uf).build();
    }

    public static List<Uf> umaListaUfs() {
        return List.of(
            umaUf(1, "UF 1", "PR"),
            umaUf(2, "UF 2", "SP"),
            umaUf(3, "UF 3", "AC")
        );
    }

    public static List<Site> umaListaSites() {
        return List.of(
            umSite(1, "Site Brandon Big", BRT),
            umSite(2, "Site Dinossauro do Acre", ACT),
            umSite(3, "Site Amazonia Queimada", AMT)
        );
    }

    public static List<Site> umaListaDeSitesVinculadoAUsuarioComCargo(Integer id, String nome, Usuario usuarioVinculado) {
        return List.of(
                umSiteVinculado(id, nome, usuarioVinculado)
        );
    }

    public static Site umSite(Integer id, String nome, ETimeZone timeZone) {
        return Site.builder()
            .id(id)
            .nome(nome)
            .timeZone(timeZone)
            .build();
    }

    public static Site umSiteVinculado(Integer id, String nome, Usuario usuarioVinculado) {
        return Site.builder()
                .id(id)
                .nome(nome)
                .supervisores(singleton(usuarioVinculado))
                .timeZone(BRT)
                .build();
    }

    public static SiteCidadeResponse umSiteCidade() {
        return SiteCidadeResponse
            .builder()
            .siteId(1)
            .siteNome("SITE 1")
            .codigoCidadeDbm(1)
            .cidadeId(1)
            .cidadeNome("LONDRINA")
            .ufId(1)
            .ufNome("PR")
            .build();
    }

    public static Usuario umUsuario(Integer id, CodigoCargo codigoCargo) {
        return Usuario.builder()
            .id(id)
            .cargo(umCargoOperacao(codigoCargo))
            .nome("UM USUARIO " + codigoCargo.name())
            .cidades(Collections.emptySet())
            .departamento(umDepartamento())
            .subCanais(Collections.emptySet())
            .build();
    }

    public static UsuarioNomeResponse umUsuarioNomeResponse(Integer id, String nome, ESituacao situacao) {
        return UsuarioNomeResponse
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticado(Integer id, CodigoCargo codigoCargo) {
        return UsuarioAutenticado.builder()
            .id(id)
            .nome("FULANO " + id.toString())
            .cargoCodigo(codigoCargo)
            .canais(Set.of(ECanal.D2D_PROPRIO, ECanal.ATIVO_PROPRIO))
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticado(Integer id, CodigoCargo codigoCargo, String nivel) {
        return UsuarioAutenticado.builder()
            .id(id)
            .nome("FULANO " + id.toString())
            .cargoCodigo(codigoCargo)
            .nivel(nivel)
            .nivelCodigo(nivel)
            .canais(Set.of(ECanal.D2D_PROPRIO, ECanal.ATIVO_PROPRIO))
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticado(CodigoNivel nivel) {
        return UsuarioAutenticado.builder()
            .id(100)
            .nivelCodigo(nivel.name())
            .subCanais(Set.of(umSubCanalDto(1, ETipoCanal.PAP, "PAP")))
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticadoAdmin(Integer id) {
        return UsuarioAutenticado.builder()
            .id(id)
            .cargoCodigo(CodigoCargo.ADMINISTRADOR)
            .canais(Collections.singleton(ECanal.ATIVO_PROPRIO))
            .nivel("XBRAIN")
            .nivelCodigo(CodigoNivel.XBRAIN.name())
            .usuario(Usuario.builder()
                .cargo(null)
                .build())
            .departamentoCodigo(CodigoDepartamento.ADMINISTRADOR)
            .build();
    }

    public static Cargo umCargoOperacao(CodigoCargo codigoCargo) {
        return Cargo.builder()
                .id(1)
                .codigo(codigoCargo)
                .nivel(Nivel.builder()
                        .codigo(CodigoNivel.OPERACAO)
                        .build())
                .nome(codigoCargo.name())
                .situacao(ESituacao.A)
                .build();
    }

    private static Departamento umDepartamento() {
        return Departamento
            .builder()
            .id(1)
            .codigo(CodigoDepartamento.COMERCIAL)
            .build();
    }
}
