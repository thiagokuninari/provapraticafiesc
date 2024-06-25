package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.predicate.ClusterPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.ClusterRepository;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ParceirosOnlineService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice;
import static helpers.ClusterHelper.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.verify;
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
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private ParceirosOnlineService parceirosOnlineService;

    private ClusterPredicate predicate;

    @Before
    public void setUp() throws Exception {
        predicate = new ClusterPredicate().filtrarPermitidos(USUARIO_ID);
    }

    @Test
    public void getAllByGrupoId_deveRetornarListaClusterDto_quandoUsuarioPossuirPermissao() {
        var usuario = umUsuarioAutenticadoNivelBackoffice();
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuario);
        var predicateFiltrarPermitidos = new ClusterPredicate().filtrarPermitidos(usuario);

        when(clusterRepository.findAllByGrupoId(GRUPO_NORTE_PARANA_ID, predicateFiltrarPermitidos.build()))
            .thenReturn(List.of(umClusterNorteDoParana()));

        assertThat(clusterService.getAllByGrupoId(GRUPO_NORTE_PARANA_ID))
            .isNotNull()
            .extracting("id", "nome")
            .containsExactly(tuple(45, "NORTE DO PARANÁ"));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(clusterRepository).findAllByGrupoId(GRUPO_NORTE_PARANA_ID, predicateFiltrarPermitidos.build());
    }

    @Test
    public void getAllAtivo_deveRetornarListaClustersAtivo_quandoSolicitado() {
        when(clusterRepository.findBySituacao(ESituacao.A, new Sort("nome")))
            .thenReturn(List.of(umClusterNorteDoParana(), umClusterMarilia()));

        assertThat(clusterService.getAllAtivo())
            .isNotNull()
            .extracting("id", "nome", "situacao")
            .containsExactly(tuple(45, "NORTE DO PARANÁ", ESituacao.A),
                tuple(39, "MARÍLIA", ESituacao.A));

        verify(clusterRepository).findBySituacao(ESituacao.A, new Sort("nome"));
    }

    @Test
    public void getAtivosParaComunicados_deveRetornarListaClustersAtivosOrdenadosPorNome_quandoSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());

        when(parceirosOnlineService.getClusters(40)).thenReturn(List.of(umClusterDto(1), umClusterDto(2)));
        assertThat(clusterService.getAtivosParaComunicados(40))
            .isNotNull()
            .extracting("id", "nome", "situacao")
            .containsExactly(tuple(1, "CLUSTER", ESituacao.A),
                tuple(2, "CLUSTER", ESituacao.A));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(parceirosOnlineService).getClusters(40);
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

    private ClusterDto umClusterDto(Integer id) {
        return ClusterDto.builder()
            .id(id)
            .nome("CLUSTER")
            .grupo(GrupoDto.builder()
                .id(2)
                .build())
            .situacao(ESituacao.A)
            .build();
    }
}
