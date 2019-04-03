package br.com.xbrain.autenticacao.modules.usuario.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_supervisor.sql"})
public class SupervisorServiceTest {

    private static final int LONDRINA_ID = 5578;
    private static final int CHAPECO_ID = 4498;

    private static final int SUBCLUSTER_LONDRINA_ID = 189;
    private static final int SUBCLUSTER_CHAPECO_ID = 26600;

    private static final int CLUSTER_NORTE_PARANA_ID = 45;
    private static final int CLUSTER_PASSO_FUNDO_ID = 7700;

    private static final int GRUPO_NORTE_PARANA_ID = 20;
    private static final int GRUPO_RS_SERRA = 710;

    private static final int REGIONAL_SUL_ID = 3;
    private static final int REGIONAL_LESTE_ID = 1;

    private static final int SUPERVISOR_LONDRINA_ID = 1;
    private static final int SUPERVISOR_ARAPONGAS_ID = 2;
    private static final int SUPERVISOR_LINS_ID = 3;
    private static final int SUPERVISOR_SEM_CIDADE_ID = 4;

    @Autowired
    private SupervisorService service;

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisoresDaCidade_seExistirem() {

        assertThat(
                service.getSupervisoresPorAreaAtuacao(CIDADE, singletonList(LONDRINA_ID)))
                .extracting("nome", "codigoCargo")
                .containsExactly(
                        tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO));

        assertThat(
                service.getSupervisoresPorAreaAtuacao(CIDADE, singletonList(CHAPECO_ID)))
                .isEmpty();
    }

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisoresDoSubCluster_seExistirem() {

        assertThat(
                service.getSupervisoresPorAreaAtuacao(SUBCLUSTER, singletonList(SUBCLUSTER_LONDRINA_ID)))
                .extracting("nome", "codigoCargo")
                .containsExactly(
                        tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                        tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO));

        assertThat(
                service.getSupervisoresPorAreaAtuacao(SUBCLUSTER, singletonList(SUBCLUSTER_CHAPECO_ID)))
                .isEmpty();
    }

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisoresDoCluster_seExistirem() {

        assertThat(
                service.getSupervisoresPorAreaAtuacao(CLUSTER, singletonList(CLUSTER_NORTE_PARANA_ID)))
                .extracting("nome", "codigoCargo")
                .containsExactly(
                        tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                        tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO));

        assertThat(
                service.getSupervisoresPorAreaAtuacao(CLUSTER, singletonList(CLUSTER_PASSO_FUNDO_ID)))
                .isEmpty();
    }

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisoresDoGrupo_seExistirem() {

        assertThat(
                service.getSupervisoresPorAreaAtuacao(GRUPO, singletonList(GRUPO_NORTE_PARANA_ID)))
                .extracting("nome", "codigoCargo")
                .containsExactly(
                        tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                        tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO));

        assertThat(
                service.getSupervisoresPorAreaAtuacao(GRUPO, singletonList(GRUPO_RS_SERRA)))
                .isEmpty();
    }

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisoresDaRegional_seExistirem() {

        assertThat(
                service.getSupervisoresPorAreaAtuacao(REGIONAL, singletonList(REGIONAL_SUL_ID)))
                .extracting("nome", "codigoCargo")
                .containsExactly(
                        tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                        tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO));

        assertThat(
                service.getSupervisoresPorAreaAtuacao(REGIONAL, singletonList(REGIONAL_LESTE_ID)))
                .isEmpty();
    }

    @Test
    public void getAssistentesEVendedoresD2dDaCidadeDoSupervisor_vendedoresEAssistentesDoSubcluster_seExistirem() {
        assertThat(
                service.getAssistentesEVendedoresD2dDaCidadeDoSupervisor(SUPERVISOR_LONDRINA_ID))
                .extracting("nome", "codigoCargo")
                .containsExactly(
                        tuple("VENDEDOR LONDRINA", VENDEDOR_OPERACAO),
                        tuple("ASSISTENTE LONDRINA", ASSISTENTE_OPERACAO));

        assertThat(
                service.getAssistentesEVendedoresD2dDaCidadeDoSupervisor(SUPERVISOR_ARAPONGAS_ID))
                .extracting("nome", "codigoCargo")
                .containsExactly(
                        tuple("VENDEDOR ARAPONGAS", VENDEDOR_OPERACAO),
                        tuple("ASSISTENTE ARAPONGAS", ASSISTENTE_OPERACAO));

        assertThat(
                service.getAssistentesEVendedoresD2dDaCidadeDoSupervisor(SUPERVISOR_SEM_CIDADE_ID))
                .isEmpty();
    }

    @Test
    public void getAssistentesEVendedoresD2dDaCidadeDoSupervisor_deveNaoRetornar_senaoForemDoCanalD2D() {
        assertThat(
                service.getAssistentesEVendedoresD2dDaCidadeDoSupervisor(SUPERVISOR_LINS_ID))
                .isEmpty();
    }
}
