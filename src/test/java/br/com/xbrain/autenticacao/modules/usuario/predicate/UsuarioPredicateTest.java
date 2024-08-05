package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.xbrain.autenticacao.modules.comum.model.QUf.uf1;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioPredicateTest {


    @Test
    public void excluiIds_deveRetornarPredicate_quandoIdsForFornecido() {
        var predicate = new UsuarioPredicate()
            .excluiIds(List.of(1, 2))
            .build();
        var expected = new BooleanBuilder(usuario.id.notIn(List.of(1, 2)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void excluiIds_deveIgnorarTodosOsRegistros_quandoIdsForVazio() {
        var predicate = new UsuarioPredicate()
            .excluiIds(List.of())
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

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
    public void comIds_deveParticionarOsIds_quandoTamanhoDaListaDeIdsForMaiorQueMaximoOracle() {
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

    @Test
    public void comSubCanalId_deveMontarBooleanBuilder_quandoIdExistir() {
        var predicate = new UsuarioPredicate()
            .comSubCanal(1)
            .build();
        var expected = new BooleanBuilder(usuario.subCanais.any().id.eq(1));
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
    public void comOrganizacaoEmpresaId_deveRetornarUsuarioPredicate_quandoIdNaoNull() {
        var predicate = new UsuarioPredicate()
            .comOrganizacaoEmpresaId(1)
            .build();
        var expected = new BooleanBuilder(usuario.organizacaoEmpresa.id.eq(1));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void semCargoCodigo_deveMontarPredicate_quandoSolicitado() {
        var predicate = new UsuarioPredicate()
            .semCargoCodigo(CodigoCargo.COORDENADOR_OPERACAO)
            .build();

        var expected = new BooleanBuilder(usuario.cargo.codigo.ne(CodigoCargo.COORDENADOR_OPERACAO));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comId_deveFiltrarPorId_quandoIdNaoNull() {
        var predicate = new UsuarioPredicate()
            .comId(1)
            .build();
        var expected = new BooleanBuilder(usuario.id.eq(1));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comId_naoDeveFiltrarPorId_quandoIdNull() {
        var predicate = new UsuarioPredicate()
            .comId(null)
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comUsuariosEmail_deveRetornarPredicate_quandoEmailsForFornecido() {
        var predicate = new UsuarioPredicate()
            .comUsuariosEmail(List.of("email1", "email2"))
            .build();
        var expected = new BooleanBuilder(usuario.email.in(List.of("email1", "email2")));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comUsuariosEmail_deveIgnorarTodosOsRegistros_quandoEmailsForVazio() {
        var predicate = new UsuarioPredicate()
            .comUsuariosEmail(List.of())
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comUsuariosCpfs_deveRetornarPredicate_quandoCpfsForFornecido() {
        var predicate = new UsuarioPredicate()
            .comUsuariosCpfs(List.of("111", "222"))
            .build();
        var expected = new BooleanBuilder(usuario.cpf.in(List.of("111", "222")));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comUsuariosCpfs_deveIgnorarTodosOsRegistros_quandoCpfsForVazio() {
        var predicate = new UsuarioPredicate()
            .comUsuariosCpfs(List.of())
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comEmpresas_deveRetornarPredicate_quandoEmpresasForFornecido() {
        var predicate = new UsuarioPredicate()
            .comEmpresas(List.of(111, 222))
            .build();
        var expected = new BooleanBuilder(usuario.empresas.any().id.in(List.of(111, 222)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comEmpresas_deveIgnorarTodosOsRegistros_quandoEmpresasForVazio() {
        var predicate = new UsuarioPredicate()
            .comEmpresas(List.of())
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comUnidadesNegocio_deveRetornarPredicate_quandoUnidadesNegocioIdsForFornecido() {
        var predicate = new UsuarioPredicate()
            .comUnidadesNegocio(List.of(111, 222))
            .build();
        var expected = new BooleanBuilder(usuario.unidadesNegocios.any().id.in(List.of(111, 222)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comUnidadesNegocio_deveIgnorarTodosOsRegistros_quandoUnidadesNegocioIdsForVazio() {
        var predicate = new UsuarioPredicate()
            .comUnidadesNegocio(List.of())
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void isAtivo_deveRetornarPredicate_quandoForAtivo() {
        var predicate = new UsuarioPredicate()
            .isAtivo(Eboolean.V)
            .build();
        var expected = new BooleanBuilder(usuario.situacao.eq(ESituacao.A));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void isAtivo_deveIgnorarTodosOsRegistros_quandoNaoForAtivo() {
        var predicate = new UsuarioPredicate()
            .isAtivo(Eboolean.F)
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void semCargoCodigoo_deveRetornarPredicate_quandoCodigoCargoForFornecido() {
        var predicate = new UsuarioPredicate()
            .semCargoCodigo(CodigoCargo.VENDEDOR_OPERACAO)
            .build();
        var expected = new BooleanBuilder(usuario.cargo.codigo.ne(CodigoCargo.VENDEDOR_OPERACAO));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void semCargoCodigo_deveIgnorarTodosOsRegistros_quandoCodigoCargoForNull() {
        var predicate = new UsuarioPredicate()
            .semCargoCodigo(null)
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCargo_deveRetornarPredicate_quandoCargoIdsForFornecido() {
        var predicate = new UsuarioPredicate()
            .comCargo(List.of(111, 222))
            .build();
        var expected = new BooleanBuilder(usuario.cargo.id.in(List.of(111, 222)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCargo_deveIgnorarTodosOsRegistros_quandoCargoIdsForVazio() {
        var predicate = new UsuarioPredicate()
            .comCargo(List.of())
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCargos_deveRetornarPredicate_quandoCargoIdsForFornecido() {
        var predicate = new UsuarioPredicate()
            .comCargos(Set.of(111, 222))
            .build();
        var expected = new BooleanBuilder(usuario.cargo.id.in(Set.of(111, 222)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCargos_deveIgnorarTodosOsRegistros_quandoCargoIdsForVazio() {
        var predicate = new UsuarioPredicate()
            .comCargos(Set.of())
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comUnidadeNegocio_deveRetornarPredicate_quandoUnidadeNegocioIdsForFornecido() {
        var predicate = new UsuarioPredicate()
            .comUnidadeNegocio(List.of(111, 222))
            .build();
        var expected = new BooleanBuilder(usuario.unidadesNegocios.any().id.in(List.of(111, 222)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comUnidadeNegocio_deveIgnorarTodosOsRegistros_quandoUnidadeNegocioIdsForVazio() {
        var predicate = new UsuarioPredicate()
            .comUnidadeNegocio(List.of())
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comUf_deveRetornarPredicate_quandoUfIdForFornecido() {
        var predicate = new UsuarioPredicate()
            .comUf(5)
            .build();
        var expected = new BooleanBuilder(usuario.cidades.any().cidade.id.in(
            JPAExpressions.select(cidade.id)
                .from(cidade)
                .join(cidade.uf, uf1)
                .where(uf1.id.eq(5))));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comUf_deveIgnorarTodosOsRegistros_quandoUfIdForNull() {
        var predicate = new UsuarioPredicate()
            .comUf(null)
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCanais_deveRetornarPredicate_quandoCanaisForFornecido() {
        var predicate = new UsuarioPredicate()
            .comCanais(Set.of(ECanal.AGENTE_AUTORIZADO, ECanal.VAREJO))
            .build();
        var expected = new BooleanBuilder(usuario.canais.any().in(
            Set.of(ECanal.AGENTE_AUTORIZADO, ECanal.VAREJO)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCanais_deveIgnorarTodosOsRegistros_quandoCanaisForVazio() {
        var predicate = new UsuarioPredicate()
            .comCanais(Set.of())
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comSubCanais_deveRetornarPredicate_quandoSubCanaisForFornecido() {
        var predicate = new UsuarioPredicate()
            .comSubCanais(Set.of(111, 222))
            .build();
        var expected = new BooleanBuilder(usuario.subCanais.any().id.in(
            Set.of(111, 222)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comSubCanais_deveIgnorarTodosOsRegistros_quandoSubCanaisForVazio() {
        var predicate = new UsuarioPredicate()
            .comSubCanais(Set.of())
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void semUsuarioId_deveRetornarPredicate_quandoUsuarioIdForFornecido() {
        var predicate = new UsuarioPredicate()
            .semUsuarioId(2)
            .build();
        var expected = new BooleanBuilder(usuario.id.ne(2));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void semUsuarioId_deveIgnorarTodosOsRegistros_quandoUsuarioIdForNull() {
        var predicate = new UsuarioPredicate()
            .semUsuarioId(null)
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void filtraPermitidos_deveIgnorarTodosOsRegistros_quandoUsuarioAutenticadoForNull() {
        var predicate = new UsuarioPredicate()
            .filtraPermitidos(null, null, false)
            .build();
        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

}
