package br.com.xbrain.autenticacao.modules.comum.service;

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
public class GrupoServiceTest {

    private static final int REGIONAL_LESTE_ID = 1;
    private static final int REGIONAL_SP_ID = 2;
    private static final int REGIONAL_SUL_ID = 3;
    @Autowired
    private GrupoService grupoService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getAllByRegionalIdAndUsuarioId_deveRetornarGrupo_quandoUsuarioPossuirRegionalSul() {
        assertThat(grupoService.getAllByRegionalIdAndUsuarioId(REGIONAL_SUL_ID, 1))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(20, "NORTE DO PARANÁ"));
    }

    @Test
    public void getAllByRegionalIdAndUsuarioId_deveRetornarGrupo_quandoUsuarioPossuirRegionalSp() {
        assertThat(grupoService.getAllByRegionalIdAndUsuarioId(REGIONAL_SP_ID, 1))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(15, "MARILIA"));
    }

    @Test
    public void getAllByRegionalIdAndUsuarioId_deveRetornarVazio_quandoUsuarioNaoPossuirRegionalLeste() {
        assertThat(grupoService.getAllByRegionalIdAndUsuarioId(REGIONAL_LESTE_ID, 1))
                .isEmpty();
    }

    @Test
    public void getAllByRegionalIdAndUsuarioId_deveRetornarGrupo_quandoUsuarioPossuirRegionalLeste() {
        assertThat(grupoService.getAllByRegionalIdAndUsuarioId(REGIONAL_LESTE_ID, 2))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(4, "NORDESTE"));
    }

    @Test
    public void findById_deveRetornarUmGrupo_seExistir() {
        assertThat(grupoService.findById(1))
            .isEqualTo(umGrupoDto());
    }

    @Test
    public void findById_deveLancarException_seGrupoNaoExistir() {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Grupo não encontrado.");
        grupoService.findById(1516516);
    }

    GrupoDto umGrupoDto() {
        GrupoDto grupoDto = new GrupoDto();
        grupoDto.setId(1);
        grupoDto.setNome("CENTRO-OESTE");
        grupoDto.setRegional(umaRegionalDto());
        grupoDto.setSituacao(ESituacao.A);
        return grupoDto;
    }

    RegionalDto umaRegionalDto() {
        return RegionalDto.builder()
            .id(1)
            .nome("LESTE")
            .situacao(ESituacao.A)
            .build();
    }
}
