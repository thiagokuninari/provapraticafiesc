package br.com.xbrain.autenticacao.modules.organizacaoempresa.predicate;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.model.QOrganizacaoEmpresa.organizacaoEmpresa;
import static org.assertj.core.api.Assertions.assertThat;

public class OrganizacaoEmpresaPredicateTest {

    @Test
    public void comIds_deveIgnorarTodosOsRegistros_quandoIdForNull() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comId(null)
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIds_organizacaoEmpresaPredicate_quandoIdNaoNull() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comId(1)
            .build();
        var expected = new BooleanBuilder(organizacaoEmpresa.id.eq(1));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNome_organizacaoEmpresaPredicate_quandoNomeNull() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comNome(null)
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNome_organizacaoEmpresaPredicate_quandoNomeVazio() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comNome("")
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNome_organizacaoEmpresaPredicate_quandoNomeNaoNullENaoVazio() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comNome("Thiago teste")
            .build();
        var expected = new BooleanBuilder(organizacaoEmpresa.nome.containsIgnoreCase("Thiago teste"));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNivel_organizacaoEmpresaPredicate_quandoNivelNull() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comNivel(null)
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNivel_organizacaoEmpresaPredicate_quandoNiveNaoNull() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comNivel(1)
            .build();
        var expected = new BooleanBuilder(organizacaoEmpresa.nivel.id.eq(1));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comSituacao_organizacaoEmpresaPredicate_quandoSituacaoNull() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comSituacao(null)
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comSituacao_organizacaoEmpresaPredicate_quandoSituacaoNaoNull() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comSituacao(ESituacaoOrganizacaoEmpresa.I)
            .build();
        var expected = new BooleanBuilder(organizacaoEmpresa.situacao.eq(ESituacaoOrganizacaoEmpresa.I));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCodigo_organizacaoEmpresaPredicate_quandoCodigoNull() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comCodigo(null)
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCodigo_organizacaoEmpresaPredicate_quandoCodigoVazio() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comCodigo("")
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCodigo_organizacaoEmpresaPredicate_quandoCodigoNaoNullENaoVazio() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comCodigo("codigo")
            .build();
        var expected = new BooleanBuilder(organizacaoEmpresa.codigo.containsIgnoreCase("codigo"));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comECanal_deveMontarPredicate_quandoCanalNaoNullENaoVazio() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comECanal(ECanal.INTERNET)
            .build();
        var expected = new BooleanBuilder(organizacaoEmpresa.canal.eq(ECanal.INTERNET));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comECanal_naoDeveMontarPredicate_quandoCanalNull() {
        var predicate = new OrganizacaoEmpresaPredicate()
            .comECanal(null)
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }
}
