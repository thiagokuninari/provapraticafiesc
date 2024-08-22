package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class CargoDepartamentoFuncionalidadeRepositoryTest {

    private static final Integer CARGO_SOCIO_ID = 41;
    private static final Integer USUARIO_SOCIO_ID = 226;
    private static final Integer DEPARTAMENTO_SOCIO_ID = 40;

    @Autowired
    private CargoDepartamentoFuncionalidadeRepository repository;

    @Test
    public void findFuncionalidadesDoCargoDepartamentoComCanal_funcionalidades_aoFiltrarPorDepartamentoECanal() {
        List<Funcionalidade> funcionalidades = repository
            .findFuncionalidadesDoCargoDepartamentoComCanal(CARGO_SOCIO_ID, DEPARTAMENTO_SOCIO_ID);

        assertThat(funcionalidades)
                .extracting("nome")
                .containsExactly(
                    "Gerenciar Pausas Agendadas",
                    "Visualizar Tabulação Manual",
                    "Visualizar Agendamento",
                    "Relatório - Resumo de Mailing",
                    "Relatório - Ticket Médio Analítico",
                    "Relatório - Ticket Médio por Vendedor",
                    "Relatório - Gerenciamento Operacional",
                    "Visualizar Relatório Consulta de Endereço",
                    "Visualizar Pré Venda Loja Futuro");

        assertThat(funcionalidades.get(0).getCanais())
            .extracting("canal")
            .containsExactly(ECanal.AGENTE_AUTORIZADO, ECanal.ATIVO_PROPRIO);
    }

    @Test
    public void findPermissoesEspeciaisDoUsuarioComCanal_funcionalidades_aoFiltrarPorUsuario() {
        List<Funcionalidade> funcionalidades = repository.findPermissoesEspeciaisDoUsuarioComCanal(USUARIO_SOCIO_ID);

        assertThat(funcionalidades)
            .extracting("nome")
            .containsExactly(
                "Relatório - Resumo de Mailing",
                "Relatório - Ticket Médio Analítico",
                "Relatório - Gerenciamento Operacional",
                "Cadastrar venda para o vendedor D2D");

        assertThat(funcionalidades.get(0).getCanais())
            .extracting("canal")
            .containsExactly(ECanal.AGENTE_AUTORIZADO, ECanal.ATIVO_PROPRIO);
    }

    @Test
    public void findAll_deveRetornarPageCargoDepartamentoFuncionalidade_quandoSolicitado() {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();
        var cargoDepartamentoFuncionalidade = repository.findAll(filtros.toPredicate(), new PageRequest());
        assertThat(cargoDepartamentoFuncionalidade)
            .extracting("id", "funcionalidade.id", "cargo.id", "departamento.id")
            .containsExactly(tuple(1, 1, 50, 50),
                tuple(2, 5, 50, 50),
                tuple(3, 6, 50, 50),
                tuple(4, 11, 50, 50),
                tuple(5, 12, 50, 50),
                tuple(6, 13, 50, 50),
                tuple(7, 16, 50, 50),
                tuple(8, 17, 50, 50),
                tuple(9, 18, 50, 50),
                tuple(10, 28, 50, 50));
    }
}
