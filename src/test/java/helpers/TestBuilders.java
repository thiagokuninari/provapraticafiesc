package helpers;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioConfiguracaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDadosAcessoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone.*;
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
                .build(),
            Usuario.builder()
                .id(2)
                .nome("MARIA")
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
        return new Uf(id, nome, uf);
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

    public static Usuario umUsuario(Integer id, CodigoCargo codigoCargo) {
        return Usuario.builder()
                .id(id)
                .cargo(umCargoOperacao(codigoCargo))
                .nome("UM USUARIO " + codigoCargo.name())
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
}
