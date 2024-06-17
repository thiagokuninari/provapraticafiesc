package br.com.xbrain.autenticacao.modules.site.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.COORDENADOR_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_USUARIO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.XBRAIN;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;
import static org.assertj.core.api.Assertions.assertThat;

public class SitePredicateTest {

    @Test
    public void comId_naoDeveMontarSitePredicate_seIdNula() {
        assertThat(new SitePredicate().comId(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comId_deveMontarSitePredicate_seIdInformado() {
        assertThat(new SitePredicate().comId(1).build())
            .isEqualTo(new BooleanBuilder(site.id.eq(1)));
    }

    @Test
    public void excetoId_naoDeveMontarSitePredicate_seIdNula() {
        assertThat(new SitePredicate().excetoId(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void excetoId_deveMontarSitePredicate_seIdInformado() {
        assertThat(new SitePredicate().excetoId(1).build())
            .isEqualTo(new BooleanBuilder(site.id.ne(1)));
    }

    @Test
    public void ignorarSite_naoDeveMontarSitePredicate_seIdNula() {
        assertThat(new SitePredicate().ignorarSite(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void ignorarSite_deveMontarSitePredicate_seIdInformado() {
        assertThat(new SitePredicate().ignorarSite(1).build())
            .isEqualTo(new BooleanBuilder(site.id.ne(1)));
    }

    @Test
    public void comNome_naoDeveMontarSitePredicate_seNomeNull() {
        assertThat(new SitePredicate().comNome(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comNome_naoDeveMontarSitePredicate_seNomeVazio() {
        assertThat(new SitePredicate().comNome("").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comNome_deveMontarSitePredicate_seNomeInformado() {
        assertThat(new SitePredicate().comNome("nome").build())
            .isEqualTo(new BooleanBuilder(site.nome.containsIgnoreCase("nome")));
    }

    @Test
    public void comCidades_naoDeveMontarSitePredicate_seCidadesIdsNull() {
        assertThat(new SitePredicate().comCidades(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comNome_deveMontarSitePredicate_seCidadesIdsInformado() {
        assertThat(new SitePredicate().comCidades(List.of(1, 2)).build())
            .isEqualTo(new BooleanBuilder(site.cidades.any().id.in(1, 2)));
    }

    @Test
    public void comEstados_naoDeveMontarSitePredicate_seEstadosIdsNull() {
        assertThat(new SitePredicate().comEstados(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comEstados_deveMontarSitePredicate_seEstadosIdsInformado() {
        assertThat(new SitePredicate().comEstados(List.of(1, 2)).build())
            .isEqualTo(new BooleanBuilder(site.estados.any().id.in(1, 2)));
    }

    @Test
    public void comCoordenadores_naoDeveMontarSitePredicate_seCoordenadoresIdsNull() {
        assertThat(new SitePredicate().comCoordenadores(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCoordenadores_deveMontarSitePredicate_seCoordenadoresIdsInformado() {
        assertThat(new SitePredicate().comCoordenadores(List.of(1, 2)).build())
            .isEqualTo(new BooleanBuilder(site.coordenadores.any().id.in(1, 2)));
    }

    @Test
    public void comCoordenadoresOuSupervisor_naoDeveMontarSitePredicate_seUsuarioIdNull() {
        assertThat(new SitePredicate().comCoordenadoresOuSupervisor(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCoordenadoresOuSupervisor_deveMontarSitePredicate_seUsuarioIdInformado() {
        assertThat(new SitePredicate().comCoordenadoresOuSupervisor(1).build())
            .isEqualTo(new BooleanBuilder(site.coordenadores.any().id.eq(1))
                .or(site.supervisores.any().id.eq(1)));
    }

    @Test
    public void comCoordenadoresOuSupervisores_deveMontarSitePredicate_seCoordenadoresOuSupervidoresIdsInformado() {
        assertThat(new SitePredicate().comCoordenadoresOuSupervisores(List.of(1)).build())
            .isEqualTo(new BooleanBuilder(site.coordenadores.any().id.in(1)
                .or(site.supervisores.any().id.in(1))));
    }

    @Test
    public void comSupervisores_naoDeveMontarSitePredicate_seSupervisoresIdsNull() {
        assertThat(new SitePredicate().comSupervisores(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comSupervisores_deveMontarSitePredicate_seSupervisoresIdsInformado() {
        assertThat(new SitePredicate().comSupervisores(List.of(1)).build())
            .isEqualTo(new BooleanBuilder(site.supervisores.any().id.in(1)));
    }

    @Test
    public void comSituacao_naoDeveMontarSitePredicate_seSituacaoNull() {
        assertThat(new SitePredicate().comSituacao(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comSituacao_deveMontarSitePredicate_seSituacaoInformado() {
        assertThat(new SitePredicate().comSituacao(ESituacao.A).build())
            .isEqualTo(new BooleanBuilder(site.situacao.eq(ESituacao.A)));
    }

    @Test
    public void comTimeZone_naoDeveMontarSitePredicate_seTimeZoneNull() {
        assertThat(new SitePredicate().comTimeZone(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comTimeZone_deveMontarSitePredicate_seTimeZoneInformado() {
        assertThat(new SitePredicate().comTimeZone(ETimeZone.BRT).build())
            .isEqualTo(new BooleanBuilder(site.timeZone.eq(ETimeZone.BRT)));
    }

    @Test
    public void ignorarTodos_deveMontarSitePredicate() {
        assertThat(new SitePredicate().ignorarTodos().build())
            .isEqualTo(new BooleanBuilder(site.id.isNull()));
    }

    @Test
    public void todosSitesAtivos_deveMontarSitePredicate() {
        assertThat(new SitePredicate().todosSitesAtivos().build())
            .isEqualTo(new BooleanBuilder(site.situacao.eq(A)));
    }

    @Test
    public void naoPossuiDiscadora_deveMontarSitePredicate_seNaoPossuiDiscadoraForTrue() {
        assertThat(new SitePredicate().naoPossuiDiscadora(true).build())
            .isEqualTo(new BooleanBuilder(site.discadoraId.isNull()));
    }

    @Test
    public void naoPossuiDiscadora_naoDeveMontarSitePredicate_seNaoPossuiDiscadoraForFalse() {
        assertThat(new SitePredicate().naoPossuiDiscadora(false).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void naoPossuiDiscadora_naoDeveMontarSitePredicate_seNaoPossuiDiscadoraForNull() {
        assertThat(new SitePredicate().naoPossuiDiscadora(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comDiscadoraId_deveMontarSitePredicate_seDiscadoraIdForInformado() {
        assertThat(new SitePredicate().comDiscadoraId(1).build())
            .isEqualTo(new BooleanBuilder(site.discadoraId.eq(1)));
    }

    @Test
    public void comDiscadoraId_naoDeveMontarSitePredicate_seDiscadoraIdForNull() {
        assertThat(new SitePredicate().comDiscadoraId(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comSupervisoresDisponiveisDosCoordenadores_deveMontarSitePredicate_seCoordenadoresIdsForInformado() {
        assertThat(new SitePredicate().comSupervisoresDisponiveisDosCoordenadores(List.of(1, 2)).build())
            .isEqualTo(new BooleanBuilder(usuario.usuariosHierarquia.any().usuarioSuperior.id.in(1, 2)
                .and(usuario.id.notIn(JPAExpressions.select(usuario.id)
                    .from(site)
                    .join(site.supervisores, usuario)
                    .where(site.situacao.eq(A)
                    )))));
    }

    @Test
    public void comSupervisoresDisponiveisDosCoordenadores_naoDeveMontarSitePredicate_seCoordenadoresIdsForNull() {
        assertThat(new SitePredicate().comSupervisoresDisponiveisDosCoordenadores(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comSupervisoresDisponiveisDosCoordenadoresEsite_SitePredicate_seCoordenadoresIdsESiteIdForInformado() {
        assertThat(new SitePredicate().comSupervisoresDisponiveisDosCoordenadoresEsite(List.of(1, 2), 3).build())
            .isEqualTo(new BooleanBuilder(usuario.usuariosHierarquia.any().usuarioSuperior.id.in(1, 2)
                .and(usuario.id.notIn(JPAExpressions.select(usuario.id)
                    .from(site)
                    .join(site.supervisores, usuario)
                    .where(site.situacao.eq(A)
                        .and(site.id.ne(3))
                    )))));
    }

    @Test
    public void comSupervisoresDisponiveisDosCoordenadoresEsite_naoDeveMontarSitePredicate_seCoordenadoresIdsForNull() {
        assertThat(new SitePredicate().comSupervisoresDisponiveisDosCoordenadoresEsite(null, 2).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCoordenadoresDisponiveis_deveMontarSitePredicate_seCoordenadoresIdsESiteIdForInformado() {
        assertThat(new SitePredicate().comCoordenadoresDisponiveis().build())
            .isEqualTo(new BooleanBuilder(usuario.id.notIn(JPAExpressions.select(usuario.id)
                .from(site)
                .join(site.coordenadores, usuario)
                .where(site.situacao.eq(A))))
                .and(usuario.cargo.codigo.eq(COORDENADOR_OPERACAO)
                ));
    }

    @Test
    public void comCoordenadoresComCidade_deveMontarSitePredicate_seCidadesIdsForInformado() {
        assertThat(new SitePredicate().comCoordenadoresComCidade(List.of(1, 2)).build())
            .isEqualTo(new BooleanBuilder(usuarioCidade.cidade.id.in(1, 2)
                .and(usuario.cargo.codigo.eq(COORDENADOR_OPERACAO))));
    }

    @Test
    public void comCoordenadoresComCidade_naoDeveMontarSitePredicate_seCidadesIdsForVazia() {
        assertThat(new SitePredicate().comCoordenadoresComCidade(List.of()).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comSupervisoresDisponiveis_deveMontarSitePredicate() {
        assertThat(new SitePredicate().comSupervisoresDisponiveis().build())
            .isEqualTo(new BooleanBuilder(usuario.id.notIn(JPAExpressions
                    .select(usuario.id)
                    .from(site)
                    .join(site.supervisores, usuario)
                    .where(site.situacao.eq(A)))
                .and(usuario.cargo.codigo.eq(CodigoCargo.SUPERVISOR_OPERACAO))));
    }

    @Test
    public void comFiltroVisualizar_deveMontarSitePredicate_seUsuarioNaoTiverPermissaoParaVisualizarGeral() {
        var usuarioAutenticado = UsuarioAutenticado
            .builder()
            .id(4545)
            .nivelCodigo(XBRAIN.name())
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_USUARIO.getRole())))
            .build();

        assertThat(new SitePredicate().comFiltroVisualizar(usuarioAutenticado).build())
            .isEqualTo(new BooleanBuilder(usuario.id.eq(4545)));
    }

    @Test
    public void comFiltroVisualizar_naoDeveMontarSitePredicate_seUsuarioTiverPermissaoParaVisualizarGeral() {
        var usuarioAutenticado = UsuarioAutenticado
            .builder()
            .id(4545)
            .nivelCodigo(XBRAIN.name())
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
            .build();

        assertThat(new SitePredicate().comFiltroVisualizar(usuarioAutenticado).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comUsuarioSuperior_deveMontarSitePredicate_seUsuarioIdForInformado() {
        assertThat(new SitePredicate().comUsuarioSuperior(2).build())
            .isEqualTo(new BooleanBuilder(usuarioHierarquia.usuarioSuperior.id.eq(2)));
    }

    @Test
    public void comUsuarioSuperior_naoDeveMontarSitePredicate_seUsuarioIdForNull() {
        assertThat(new SitePredicate().comUsuarioSuperior(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCidade_sitePredicate_seCidadeNula() {
        assertThat(new SitePredicate().comCidade(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCidade_sitePredicate_seCidadeVazia() {
        assertThat(new SitePredicate().comCidade("").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCidade_sitePredicate_seCidadeNaoNulaENaoVazia() {
        assertThat(new SitePredicate().comCidade("CIDADE").build())
            .isEqualTo(new BooleanBuilder(site.cidades.any().nome.eq("CIDADE")));
    }

    @Test
    public void comUf_sitePredicate_seUfNula() {
        assertThat(new SitePredicate().comUf(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comUf_sitePredicate_seUfVazia() {
        assertThat(new SitePredicate().comUf("").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comUf_sitePredicate_seUfNaoNulaENaoVazia() {
        assertThat(new SitePredicate().comUf("UF").build())
            .isEqualTo(new BooleanBuilder(site.cidades.any().uf.uf.eq("UF")));
    }

    @Test
    public void comCodigoCidadeDbm_sitePredicate_seCodigoCidadeDbmNula() {
        assertThat(new SitePredicate().comCodigoCidadeDbm(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCodigoCidadeDbm_sitePredicate_seCodigoCidadeDbmNaoNula() {
        assertThat(new SitePredicate().comCodigoCidadeDbm(1).build())
            .isEqualTo(new BooleanBuilder(site.cidades.any().cidadesDbm.any().codigoCidadeDbm.eq(1)));
    }

    @Test
    public void comIds_sitePredicate_seNaoHouverIds() {
        assertThat(new SitePredicate().comIds(null).build())
            .isEqualTo(new BooleanBuilder());

        assertThat(new SitePredicate().comIds(List.of()).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comIds_sitePredicate_seHouverIds() {
        assertThat(new SitePredicate().comIds(umaListaIds(1, 2001)).build())
            .isEqualTo(
                new BooleanBuilder(
                    site.id.in(umaListaIds(1, 1000))
                        .or(site.id.in(umaListaIds(1001, 2000)))
                        .or(site.id.in(2001))
                )
            );
    }

    private List<Integer> umaListaIds(Integer inicio, Integer fim) {
        return IntStream.rangeClosed(inicio, fim)
            .boxed()
            .collect(Collectors.toList());
    }
}
