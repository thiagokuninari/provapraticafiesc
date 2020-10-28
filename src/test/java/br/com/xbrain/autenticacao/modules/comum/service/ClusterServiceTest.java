package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

    @Test
    public void findById_deveRetornarUmCluster_seExistir() {
        assertThat(clusterService.findById(1))
            .isEqualTo(umClusterDto());
    }

    @Test
    public void findById_deveLancarException_seClusterNaoExistir() {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Cluster não encontrado.");
        clusterService.findById(100556);
    }

    ClusterDto umClusterDto() {
        ClusterDto clusterDto = new ClusterDto();
        clusterDto.setId(1);
        clusterDto.setNome("PORTO VELHO");
        clusterDto.setGrupo(umGrupoDto());
        clusterDto.setSituacao(ESituacao.A);
        return clusterDto;
    }

    GrupoDto umGrupoDto() {
        return GrupoDto.builder()
            .id(1)
            .nome("CENTRO-OESTE")
            .regional(umaRegionalDto())
            .situacao(ESituacao.A)
            .build();
    }

    RegionalDto umaRegionalDto() {
        return RegionalDto.builder()
            .id(1)
            .nome("LESTE")
            .situacao(ESituacao.A)
            .build();
    }
}
