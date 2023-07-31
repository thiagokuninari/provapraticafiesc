package br.com.xbrain.autenticacao.modules.permissao.repository;

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

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class CargoDepartamentoFuncionalidadeRepositoryTest {

    private static Integer USUARIO_SOCIO_ID = 226;
    private static Integer CARGO_SOCIO_ID = 41;
    private static Integer DEPARTAMENTO_SOCIO_ID = 40;

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
}
