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
public class ClusterServiceTest {

    private static final int GRUPO_NORTE_PARANA_ID = 20;
    private static final int GRUPO_MARILIA_ID = 15;
    private static final int GRUPO_NORDESTE_ID = 4;

    @Autowired
    private ClusterService clusterService;

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarCluster_quandoUsuarioPossuirGrupoNorteDoParana() {
        assertThat(clusterService.getAllByGrupoIdAndUsuarioId(GRUPO_NORTE_PARANA_ID, 1))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(45, "NORTE DO PARANÁ"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarCluster_quandoUsuarioPossuirGrupoMarilia() {
        assertThat(clusterService.getAllByGrupoIdAndUsuarioId(GRUPO_MARILIA_ID, 1))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(39, "MARÍLIA"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarCluster_quandoUsuarioPossuirGrupoNordeste() {
        assertThat(clusterService.getAllByGrupoIdAndUsuarioId(GRUPO_NORDESTE_ID, 2))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(16, "ALAGOAS"));
    }
}
