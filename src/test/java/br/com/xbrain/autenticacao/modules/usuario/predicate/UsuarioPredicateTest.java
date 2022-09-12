package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.predicate.OrganizacaoEmpresaPredicate;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.model.QOrganizacaoEmpresa.organizacaoEmpresa;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioPredicateTest {

    @Test
    public void comIds_deveIgnorarTodosOsRegistros_quandoIdsForListaVazia() {
        var predicate = new UsuarioPredicate()
            .comIds(List.of())
            .build();
        var expected = new BooleanBuilder(usuario.id.isNull());
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIds_deveIgnorarTodosOsRegistros_quandoIdsForNull() {
        var predicate = new UsuarioPredicate()
            .comIds(null)
            .build();
        var expected = new BooleanBuilder(usuario.id.isNull());
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIds_deveFiltrarPorIds_quandoIdsListaComElementos() {
        var predicate = new UsuarioPredicate()
            .comIds(List.of(100, 22, 456))
            .build();
        var expected = new BooleanBuilder(usuario.id.in(100, 22, 456));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIds_deveParticionarOsIns_quandoTamanhoDaListaDeIdsForMaiorQueMaximoOracle() {
        var ids = generateRandomIntList(new Random(789), 2700);

        var predicate = new UsuarioPredicate()
            .comIds(ids)
            .build();

        var expected = new BooleanBuilder(usuario.id.in(ids.subList(0, 1000)))
            .or(usuario.id.in(ids.subList(1000, 2000)))
            .or(usuario.id.in(ids.subList(2000, 2700)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void filtrarPermitidosRelatorioLoginLogout_deveRetornarPredicateComCargosCorretos_quandoCanalForD2d() {
        var predicate = new UsuarioPredicate()
            .filtrarPermitidosRelatorioLoginLogout(ECanal.D2D_PROPRIO)
            .build();
        var expected = new BooleanBuilder(usuario.cargo.codigo.in(
            CodigoCargo.VENDEDOR_OPERACAO,
            CodigoCargo.ASSISTENTE_OPERACAO,
            CodigoCargo.OPERACAO_EXECUTIVO_VENDAS));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void filtrarPermitidosRelatorioLoginLogout_deveRetornarPredicateComCargosCorretos_quandoCanalForAa() {
        var predicate = new UsuarioPredicate()
            .filtrarPermitidosRelatorioLoginLogout(ECanal.AGENTE_AUTORIZADO)
            .build();
        var expected = new BooleanBuilder(usuario.cargo.codigo.in(
            CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_D2D,
            CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS,
            CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_D2D,
            CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS,
            CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D,
            CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void filtrarPermitidosRelatorioLoginLogout_deveRetornarPredicateSemCargosDefinidos_quandoCanalNaoForAaOuD2d() {
        var predicate = new UsuarioPredicate()
            .filtrarPermitidosRelatorioLoginLogout(ECanal.ATIVO)
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    private List<Integer> generateRandomIntList(Random random, int size) {
        return IntStream.range(0, size)
            .map(i -> random.nextInt())
            .boxed()
            .collect(Collectors.toList());
    }

    @Test
    public void comOrganizacaoEmpresaId_deveIgnorarTodosOsRegistros_quandoIdForNull() {
        var predicate = new UsuarioPredicate()
            .comOrganizacaoEmpresaId(null)
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comOrganizacaoEmpresaId_usuarioPredicate_quandoIdNaoNull() {
        var predicate = new UsuarioPredicate()
            .comOrganizacaoEmpresaId(1)
            .build();
        var expected = new BooleanBuilder(usuario.organizacaoEmpresa.id.eq(1));
        assertThat(predicate).isEqualTo(expected);
    }
}
