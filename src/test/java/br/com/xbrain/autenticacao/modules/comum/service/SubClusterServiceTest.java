package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.dto.SubClusterDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.predicate.SubClusterPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.SubClusterRepository;
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

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubClusterHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SubClusterServiceTest {

    private static final int CLUSTER_NORTE_DO_PARANA_ID = 45;
    private static final int CLUSTER_MARILIA_ID = 39;
    private static final int CLUSTER_ALAGOAS_ID = 16;
    private static final int USUARIO_ID = 1;

    @InjectMocks
    private SubClusterService subClusterService;
    @Mock
    private SubClusterRepository subClusterRepository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private ParceirosOnlineService parceirosOnlineService;
    private SubClusterPredicate predicate;

    @Before
    public void setUp() throws Exception {
        predicate = new SubClusterPredicate().filtrarPermitidos(USUARIO_ID);
    }

    @Test
    public void getAllByClusterId_deveRetornarListaSubClusterDto_quandoUsuarioPossuirPermissao() {
        var usuario = umUsuarioAutenticadoNivelBackoffice();
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuario);
        var predicateFiltrarPermitidos = new SubClusterPredicate().filtrarPermitidos(usuario);

        when(subClusterRepository.findAllByClusterId(CLUSTER_MARILIA_ID, predicateFiltrarPermitidos.build()))
            .thenReturn(List.of(umSubClusterMarilia()));

        assertThat(subClusterService.getAllByClusterId(CLUSTER_MARILIA_ID))
            .isNotNull()
            .extracting("id", "nome")
            .containsExactly(tuple(166, "MARÍLIA"));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(subClusterRepository).findAllByClusterId(CLUSTER_MARILIA_ID, predicateFiltrarPermitidos.build());
    }

    @Test
    public void getAllByClustersId_deveRetornarListaSubClusterDto_quandoUsuarioPossuirPermissao() {
        var usuario = umUsuarioAutenticadoNivelBackoffice();
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuario);
        var predicateFiltrarPermitidos = new SubClusterPredicate().filtrarPermitidos(usuario);

        when(subClusterRepository.findAllByClustersId(
            List.of(CLUSTER_MARILIA_ID, CLUSTER_NORTE_DO_PARANA_ID),
            predicateFiltrarPermitidos.build()))
            .thenReturn(List.of(umSubClusterMarilia(), umSubClusterLondrina()));

        assertThat(subClusterService.getAllByClustersId(List.of(CLUSTER_MARILIA_ID, CLUSTER_NORTE_DO_PARANA_ID)))
            .isNotNull()
            .extracting("id", "nome")
            .containsExactly(tuple(166, "MARÍLIA"),
                tuple(189, "LONDRINA"));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(subClusterRepository).findAllByClustersId(
            List.of(CLUSTER_MARILIA_ID, CLUSTER_NORTE_DO_PARANA_ID),
            predicateFiltrarPermitidos.build());
    }

    @Test
    public void getById_deveRetornarSubClusterDto_quandoSubClusterExistir() {
        when(subClusterRepository.findById(166))
            .thenReturn(Optional.ofNullable(umSubClusterMarilia()));

        assertThat(subClusterService.getById(166))
            .isNotNull()
            .extracting("id", "nome")
            .containsExactly(166, "MARÍLIA");

        verify(subClusterRepository).findById(166);
    }

    @Test
    public void getById_deveRetornarNotFoundException_quandoSubClusterNaoEncontrado() {

        when(subClusterRepository.findById(166))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> subClusterService.getById(166))
            .withMessage("Subcluster não encontrado.");

        verify(subClusterRepository).findById(166);
    }

    @Test
    public void getAllAtivos_deveRetornarListaSubClusterDtoAtivos_quandoSolicitado() {
        when(subClusterRepository.findBySituacao(ESituacao.A, new Sort("nome")))
            .thenReturn(List.of(umSubClusterMarilia(), umSubClusterLondrina()));

        assertThat(subClusterService.getAllAtivos())
            .isNotNull()
            .extracting("id", "nome", "situacao")
            .containsExactly(tuple(166, "MARÍLIA", ESituacao.A),
                tuple(189, "LONDRINA", ESituacao.A));

        verify(subClusterRepository).findBySituacao(ESituacao.A, new Sort("nome"));
    }

    @Test
    public void getAll_deveRetornarListaSubClusterDto_quandoSolicitado() {
        when(subClusterRepository.findAll())
            .thenReturn(List.of(umSubClusterMarilia(), umSubClusterLondrina()));

        assertThat(subClusterService.getAll())
            .isNotNull()
            .extracting("id", "nome", "situacao")
            .containsExactly(tuple(166, "MARÍLIA", ESituacao.A),
                tuple(189, "LONDRINA", ESituacao.A));

        verify(subClusterRepository).findAll();
    }

    @Test
    public void getAllSubclustersByUsuarioAutenticado_deveRetornarListaSubClusterDto_quandoUsuarioPossuirPermissao() {
        var usuario = umUsuarioAutenticadoNivelBackoffice();
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuario);
        var predicateFiltrarPermitidos = new SubClusterPredicate().filtrarPermitidos(usuario);

        when(subClusterRepository.findAllAtivo(predicateFiltrarPermitidos.build()))
            .thenReturn(List.of(umSubClusterMarilia()));

        assertThat(subClusterService.getAllSubclustersByUsuarioAutenticado())
            .isNotNull()
            .extracting("id", "nome")
            .containsExactly(tuple(166, "MARÍLIA"));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(subClusterRepository).findAllAtivo(predicateFiltrarPermitidos.build());
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_deveRetornarSubCluster_quandoUsuarioPossuirClusterMarilia() {
        when(subClusterRepository.findAllByClusterId(CLUSTER_MARILIA_ID, predicate.build()))
            .thenReturn(List.of(umSubClusterMarilia()));

        assertThat(subClusterService.getAllByClusterIdAndUsuarioId(CLUSTER_MARILIA_ID, USUARIO_ID))
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

        assertThat(subClusterService.getAllByClusterIdAndUsuarioId(CLUSTER_NORTE_DO_PARANA_ID, USUARIO_ID))
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

        assertThat(subClusterService.getAllByClusterIdAndUsuarioId(CLUSTER_ALAGOAS_ID, USUARIO_ID))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(68, "BRI - ARAPIRACA - AL"));
    }

    @Test
    public void getAllByGrupoIdAndUsuarioId_naoDeveRetornarSubCluster_quandoUsuarioNaoPossuirOCluster() {
        assertThat(subClusterService.getAllByClusterIdAndUsuarioId(CLUSTER_NORTE_DO_PARANA_ID, USUARIO_ID))
                .isEmpty();
    }

    private SubClusterDto umSubClusterDto(Integer id) {
        return SubClusterDto.builder()
            .id(id)
            .nome("SUBCLUSTER")
            .cluster(ClusterDto.builder()
                .id(2)
                .build())
            .situacao(A)
            .build();
    }
}
