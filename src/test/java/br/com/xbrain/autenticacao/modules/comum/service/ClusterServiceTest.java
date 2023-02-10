package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.predicate.ClusterPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.ClusterRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static helpers.ClusterHelper.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ClusterServiceTest {

    private static final int GRUPO_NORTE_PARANA_ID = 20;
    private static final int GRUPO_MARILIA_ID = 15;
    private static final int GRUPO_NORDESTE_ID = 4;
    private static final int USUARIO_ID = 1;

    @InjectMocks
    private ClusterService clusterService;
    @Mock
    private ClusterRepository clusterRepository;

    private ClusterPredicate predicate;

    @Before
    public void setUp() throws Exception {
        predicate = new ClusterPredicate().filtrarPermitidos(USUARIO_ID);
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarCluster_quandoUsuarioPossuirGrupoNorteDoParana() {
        when(clusterRepository.findAllByGrupoId(GRUPO_NORTE_PARANA_ID, predicate.build()))
            .thenReturn(List.of(umClusterNorteDoParana()));

        assertThat(clusterService.getAllByGrupoIdAndUsuarioId(GRUPO_NORTE_PARANA_ID, USUARIO_ID))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(45, "NORTE DO PARANÁ"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarCluster_quandoUsuarioPossuirGrupoMarilia() {
        when(clusterRepository.findAllByGrupoId(GRUPO_MARILIA_ID, predicate.build()))
            .thenReturn(List.of(umClusterMarilia()));

        assertThat(clusterService.getAllByGrupoIdAndUsuarioId(GRUPO_MARILIA_ID, USUARIO_ID))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(39, "MARÍLIA"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarCluster_quandoUsuarioPossuirGrupoNordeste() {
        when(clusterRepository.findAllByGrupoId(GRUPO_NORDESTE_ID, predicate.build()))
            .thenReturn(List.of(umClusterAlagoas()));

        assertThat(clusterService.getAllByGrupoIdAndUsuarioId(GRUPO_NORDESTE_ID, USUARIO_ID))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(16, "ALAGOAS"));
    }

    @Test
    public void findById_deveRetornarUmCluster_seExistir() {
        when(clusterRepository.findById(1)).thenReturn(Optional.of(umClusterPortoVelho()));

        assertThat(clusterService.findById(1)).isEqualTo(ClusterDto.of(umClusterPortoVelho()));
    }

    @Test
    public void findById_deveLancarException_seClusterNaoExistir() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> clusterService.findById(100556))
            .withMessage("Cluster não encontrado.");
    }
}
