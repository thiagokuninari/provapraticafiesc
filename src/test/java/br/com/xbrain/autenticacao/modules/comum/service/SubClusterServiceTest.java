package br.com.xbrain.autenticacao.modules.comum.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_area_atuacao.sql"})
public class SubClusterServiceTest {

    private static final int CLUSTER_NORTE_DO_PARANA_ID = 45;
    private static final int CLUSTER_MARILIA_ID = 39;
    private static final int CLUSTER_ALAGOAS_ID = 16;

    @Autowired
    private SubClusterService subClusterServiceterService;

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarSubCluster_quandoUsuarioPossuirClusterMarilia() {
        assertThat(subClusterServiceterService.getAllByClusterIdAndUsuarioId(CLUSTER_MARILIA_ID, 1))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(166, "MARÍLIA"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarSubCluster_quandoUsuarioPossuirClusterNorteDoParana() {
        assertThat(subClusterServiceterService.getAllByClusterIdAndUsuarioId(CLUSTER_NORTE_DO_PARANA_ID, 1))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(185, "BRI - PARANAVAÍ - PR"), tuple(189, "LONDRINA"), tuple(191, "MARINGÁ"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarSubCluster_quandoUsuarioPossuirClusterAlagoas() {
        assertThat(subClusterServiceterService.getAllByClusterIdAndUsuarioId(CLUSTER_ALAGOAS_ID, 2))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(68, "BRI - ARAPIRACA - AL"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_naoDeveRetornarSubCluster_quandoUsuarioNaoPossuirOCluster() {
        assertThat(subClusterServiceterService.getAllByClusterIdAndUsuarioId(CLUSTER_NORTE_DO_PARANA_ID, 2))
                .isEmpty();
    }
}
