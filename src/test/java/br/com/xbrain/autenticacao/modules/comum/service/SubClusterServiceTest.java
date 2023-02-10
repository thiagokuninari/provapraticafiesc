package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.predicate.SubClusterPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.SubClusterRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubClusterHelper.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SubClusterServiceTest {

    private static final int CLUSTER_NORTE_DO_PARANA_ID = 45;
    private static final int CLUSTER_MARILIA_ID = 39;
    private static final int CLUSTER_ALAGOAS_ID = 16;
    private static final int USUARIO_ID = 1;

    @InjectMocks
    private SubClusterService subClusterServiceterService;
    @Mock
    private SubClusterRepository subClusterRepository;
    private SubClusterPredicate predicate;

    @Before
    public void setUp() throws Exception {
        predicate = new SubClusterPredicate().filtrarPermitidos(USUARIO_ID);
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarSubCluster_quandoUsuarioPossuirClusterMarilia() {
        when(subClusterRepository.findAllByClusterId(CLUSTER_MARILIA_ID, predicate.build()))
            .thenReturn(List.of(umSubClusterMarilia()));

        assertThat(subClusterServiceterService.getAllByClusterIdAndUsuarioId(CLUSTER_MARILIA_ID, USUARIO_ID))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(166, "MARÍLIA"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarSubCluster_quandoUsuarioPossuirClusterNorteDoParana() {
        when(subClusterRepository.findAllByClusterId(CLUSTER_NORTE_DO_PARANA_ID, predicate.build()))
            .thenReturn(List.of(
                umSubClusterParanavai(),
                umSubClusterLondrina(),
                umSubClusterMaringa()));

        assertThat(subClusterServiceterService.getAllByClusterIdAndUsuarioId(CLUSTER_NORTE_DO_PARANA_ID, USUARIO_ID))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(
                    tuple(185, "BRI - PARANAVAÍ - PR"),
                    tuple(189, "LONDRINA"),
                    tuple(191, "MARINGÁ"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarSubCluster_quandoUsuarioPossuirClusterAlagoas() {
        when(subClusterRepository.findAllByClusterId(CLUSTER_ALAGOAS_ID, predicate.build()))
            .thenReturn(List.of(umSubClusterArapiraca()));

        assertThat(subClusterServiceterService.getAllByClusterIdAndUsuarioId(CLUSTER_ALAGOAS_ID, USUARIO_ID))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(68, "BRI - ARAPIRACA - AL"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_naoDeveRetornarSubCluster_quandoUsuarioNaoPossuirOCluster() {
        assertThat(subClusterServiceterService.getAllByClusterIdAndUsuarioId(CLUSTER_NORTE_DO_PARANA_ID, USUARIO_ID))
                .isEmpty();
    }
}
