package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dClient;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepositoryImpl;
import helpers.TestBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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
    @SpyBean
    private UsuarioRepositoryImpl usuarioRepository;
    @MockBean
    private EquipeVendaD2dClient equipeVendasClient;

    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisoresDaCidade_seExistirem() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.buildUsuarioAutenticadoComTodosCanais());
        assertThat(
            service.getSupervisoresPorAreaAtuacao(CIDADE, singletonList(LONDRINA_ID)))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR CURITIBA", SUPERVISOR_OPERACAO));

        assertThat(
            service.getSupervisoresPorAreaAtuacao(CIDADE, singletonList(CHAPECO_ID)))
            .isEmpty();
    }

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisoresDoSubCluster_seExistirem() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.buildUsuarioAutenticadoComTodosCanais());
        assertThat(
            service.getSupervisoresPorAreaAtuacao(SUBCLUSTER, singletonList(SUBCLUSTER_LONDRINA_ID)))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR CURITIBA", SUPERVISOR_OPERACAO));

        assertThat(
            service.getSupervisoresPorAreaAtuacao(SUBCLUSTER, singletonList(SUBCLUSTER_CHAPECO_ID)))
            .isEmpty();
    }

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisoresDoCluster_seExistirem() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.buildUsuarioAutenticadoComTodosCanais());
        assertThat(
            service.getSupervisoresPorAreaAtuacao(CLUSTER, singletonList(CLUSTER_NORTE_PARANA_ID)))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR CURITIBA", SUPERVISOR_OPERACAO));

        assertThat(
            service.getSupervisoresPorAreaAtuacao(CLUSTER, singletonList(CLUSTER_PASSO_FUNDO_ID)))
            .isEmpty();
    }

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisoresDoGrupo_seExistirem() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.buildUsuarioAutenticadoComTodosCanais());
        assertThat(
            service.getSupervisoresPorAreaAtuacao(GRUPO, singletonList(GRUPO_NORTE_PARANA_ID)))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR CURITIBA", SUPERVISOR_OPERACAO));

        assertThat(
            service.getSupervisoresPorAreaAtuacao(GRUPO, singletonList(GRUPO_RS_SERRA)))
            .isEmpty();
    }

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisoresDaRegional_seExistirem() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.buildUsuarioAutenticadoComTodosCanais());
        assertThat(
            service.getSupervisoresPorAreaAtuacao(REGIONAL, singletonList(REGIONAL_SUL_ID)))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR CURITIBA", SUPERVISOR_OPERACAO));

        assertThat(
            service.getSupervisoresPorAreaAtuacao(REGIONAL, singletonList(REGIONAL_LESTE_ID)))
            .isEmpty();
    }

    @Test
    public void getCargosDescendentesEVendedoresD2dDoSupervisor_vendedoresEAssistentesDoSubcluster_quandoExistirem() {

        doReturn(singletonList(umVendedorComId(1, VENDEDOR_OPERACAO.name())))
            .when(usuarioRepository).getSubordinadosPorCargo(anyInt(), anySet(), anyInt());
        when(equipeVendasClient.filtrarUsuariosComEquipeByUsuarioIdInOuNaEquipe(anyList(), any()))
            .thenReturn(List.of(1, 2));

        assertThat(
            service.getCargosDescendentesEVendedoresD2dDoSupervisor(SUPERVISOR_LONDRINA_ID, null, 1))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("VENDEDOR1", VENDEDOR_OPERACAO));

        assertThat(
            service.getCargosDescendentesEVendedoresD2dDoSupervisor(SUPERVISOR_ARAPONGAS_ID, null, 1))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("VENDEDOR1", VENDEDOR_OPERACAO));

        doReturn(emptyList())
            .when(usuarioRepository).getSubordinadosPorCargo(eq(SUPERVISOR_SEM_CIDADE_ID), anySet(), anyInt());

        assertThat(
            service.getCargosDescendentesEVendedoresD2dDoSupervisor(SUPERVISOR_SEM_CIDADE_ID, null, 1))
            .isEmpty();
    }

    @Test
    public void getCargosDescendentesEVendedoresD2dDoSupervisor_deveFiltrarVendedores_quandoExistirem() {

        doReturn(List.of(umVendedorComId(1, VENDEDOR_OPERACAO.name()), umVendedorComId(2, OPERACAO_EXECUTIVO_VENDAS.name()),
            umVendedorComId(3, VENDEDOR_OPERACAO.name())))
            .when(usuarioRepository).getSubordinadosPorCargo(anyInt(), anySet(), anyInt());
        when(equipeVendasClient.filtrarUsuariosComEquipeByUsuarioIdInOuNaEquipe(anyList(), any()))
            .thenReturn(List.of(1, 2));

        assertThat(
            service.getCargosDescendentesEVendedoresD2dDoSupervisor(SUPERVISOR_LONDRINA_ID, null, 1))
            .extracting("id", "nome", "codigoCargo")
            .containsExactly(
                tuple(1, "VENDEDOR1", VENDEDOR_OPERACAO),
                tuple(2, "VENDEDOR2", OPERACAO_EXECUTIVO_VENDAS));
    }

    @Test
    public void getCargosDescendentesEVendedoresD2dDoSupervisor_deveNaoRetornar_senaoForemDoCanalD2D() {
        doReturn(emptyList())
            .when(usuarioRepository).getSubordinadosPorCargo(eq(SUPERVISOR_LINS_ID), anySet(), anyInt());

        assertThat(
            service.getCargosDescendentesEVendedoresD2dDoSupervisor(SUPERVISOR_LINS_ID, null, 1))
            .isEmpty();
    }

    @Test
    public void getCargosDescendentesEVendedoresD2dDoSupervisor_deveNaoRetornar_quandoEstiverInativo() {
        doReturn(emptyList())
            .when(usuarioRepository).getSubordinadosPorCargo(eq(SUPERVISOR_LINS_ID), anySet(), anyInt());

        assertThat(
            service.getCargosDescendentesEVendedoresD2dDoSupervisor(SUPERVISOR_LINS_ID, null, 1))
            .isEmpty();
    }

    @Test
    public void getSupervisoresDoSubcluster_deveRetornarSupevisoresDoSubCluster_doSupervisorPassadoPeloCanal() {
        assertThat(service.getSupervisoresDoSubclusterDoUsuarioPeloCanal(1, ECanal.D2D_PROPRIO))
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "SUPERVISOR LONDRINA"),
                tuple(2, "SUPERVISOR ARAPONGAS"),
                tuple(5, "SUPERVISOR CURITIBA")
            );
    }

    @Test
    public void getSupervisoresDoSubcluster_deveRetornarSupevisoresDoSubCluster_quandoTiverMaisDeUmaSubClusterPeloCanal() {
        assertThat(service.getSupervisoresDoSubclusterDoUsuarioPeloCanal(5, ECanal.D2D_PROPRIO))
            .extracting("id", "nome")
            .containsExactly(tuple(1, "SUPERVISOR LONDRINA"),
                tuple(2, "SUPERVISOR ARAPONGAS"),
                tuple(5, "SUPERVISOR CURITIBA"));
    }

    @Test
    public void getSupervisoresDoSubcluster_deveNaoRetornarAssistentes_doAssistentePassadoPeloCanal() {
        assertThat(service.getSupervisoresDoSubclusterDoUsuarioPeloCanal(8, ECanal.D2D_PROPRIO))
            .extracting("id", "nome")
            .containsExactly(tuple(1, "SUPERVISOR LONDRINA"),
                tuple(2, "SUPERVISOR ARAPONGAS"),
                tuple(5, "SUPERVISOR CURITIBA"));
    }

    @Test
    public void getLideresPorAreaAtuacao_deveRetornarOsSupervisoresDaCidade_seExistirem() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.buildUsuarioAutenticadoComTodosCanais());
        assertThat(
            service.getLideresPorAreaAtuacao(CIDADE, singletonList(LONDRINA_ID)))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR CURITIBA", SUPERVISOR_OPERACAO),
                tuple("COORDENADOR LONDRINA", COORDENADOR_OPERACAO));

        assertThat(
            service.getLideresPorAreaAtuacao(CIDADE, singletonList(CHAPECO_ID)))
            .isEmpty();
    }

    @Test
    public void getLideresPorAreaAtuacao_deveRetornarOsSupervisoresDoSubCluster_seExistirem() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.buildUsuarioAutenticadoComTodosCanais());
        assertThat(
            service.getLideresPorAreaAtuacao(SUBCLUSTER, singletonList(SUBCLUSTER_LONDRINA_ID)))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR CURITIBA", SUPERVISOR_OPERACAO),
                tuple("COORDENADOR LONDRINA", COORDENADOR_OPERACAO),
                tuple("COORDENADOR ARAPONGAS", COORDENADOR_OPERACAO));

        assertThat(
            service.getLideresPorAreaAtuacao(SUBCLUSTER, singletonList(SUBCLUSTER_CHAPECO_ID)))
            .isEmpty();
    }

    @Test
    public void getLideresPorAreaAtuacao_deveRetornarOsSupervisoresDoCluster_seExistirem() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.buildUsuarioAutenticadoComTodosCanais());
        assertThat(
            service.getLideresPorAreaAtuacao(CLUSTER, singletonList(CLUSTER_NORTE_PARANA_ID)))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR CURITIBA", SUPERVISOR_OPERACAO),
                tuple("COORDENADOR LONDRINA", COORDENADOR_OPERACAO),
                tuple("COORDENADOR ARAPONGAS", COORDENADOR_OPERACAO));

        assertThat(
            service.getLideresPorAreaAtuacao(CLUSTER, singletonList(CLUSTER_PASSO_FUNDO_ID)))
            .isEmpty();
    }

    @Test
    public void getLideresPorAreaAtuacao_deveRetornarOsSupervisoresDoGrupo_seExistirem() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.buildUsuarioAutenticadoComTodosCanais());
        assertThat(
            service.getLideresPorAreaAtuacao(GRUPO, singletonList(GRUPO_NORTE_PARANA_ID)))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR CURITIBA", SUPERVISOR_OPERACAO),
                tuple("COORDENADOR LONDRINA", COORDENADOR_OPERACAO),
                tuple("COORDENADOR ARAPONGAS", COORDENADOR_OPERACAO));

        assertThat(
            service.getLideresPorAreaAtuacao(GRUPO, singletonList(GRUPO_RS_SERRA)))
            .isEmpty();
    }

    @Test
    public void getLideresPorAreaAtuacao_deveRetornarOsSupervisoresDaRegional_seExistirem() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(TestBuilders.buildUsuarioAutenticadoComTodosCanais());
        assertThat(
            service.getLideresPorAreaAtuacao(REGIONAL, singletonList(REGIONAL_SUL_ID)))
            .extracting("nome", "codigoCargo")
            .containsExactly(
                tuple("SUPERVISOR LONDRINA", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR ARAPONGAS", SUPERVISOR_OPERACAO),
                tuple("SUPERVISOR CURITIBA", SUPERVISOR_OPERACAO),
                tuple("COORDENADOR LONDRINA", COORDENADOR_OPERACAO),
                tuple("COORDENADOR ARAPONGAS", COORDENADOR_OPERACAO));

        assertThat(
            service.getLideresPorAreaAtuacao(REGIONAL, singletonList(REGIONAL_LESTE_ID)))
            .isEmpty();
    }

    private Object[] umVendedorComId(int id, String cargoCodigo) {
        return new Object[]{new BigDecimal(id), "VENDEDOR" + id, "EMAIL@GMAIL.COM", "VENDEDOR", cargoCodigo};
    }
}
