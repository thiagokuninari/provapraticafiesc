package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
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

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_area_atuacao.sql"})
public class RegionalServiceTest {

    @Autowired
    private RegionalService regionalService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getAllByUsuarioId_deveRetornarRegionaisSulESp_doUsuarioInformadoPeloParametro() {
        assertThat(regionalService.getAllByUsuarioId(1))
                .isNotNull()
                .extracting("value", "label")
                .containsExactly(tuple(3, "SUL"), tuple(2, "SÃO PAULO"));
    }

    @Test
    public void getAllByUsuarioId_deveRetornarRegionalLeste_doUsuarioInformadoPeloParametro() {
        assertThat(regionalService.getAllByUsuarioId(2))
                .isNotNull()
                .extracting("value", "label")
                .containsExactly(tuple(1, "LESTE"));
    }

    @Test
    public void findById_deveRetornarUmaRegional_seExistir() {
        assertThat(regionalService.findById(1))
            .isEqualTo(umClusterDto());
    }

    @Test
    public void findById_deveLancarException_seRegionalNaoExistir() {
        thrown.expect(ValidacaoException.class);
        thrown.expectMessage("Regional não encontrada.");
        regionalService.findById(16516);
    }

    @Test
    public void getNovasRegionaisIds_deveRetornarIdsDeNovasRegionais_quandoSolicitado() {
        assertThat(regionalService.getNovasRegionaisIds()).isEqualTo(List.of(1027));
    }

    RegionalDto umClusterDto() {
        RegionalDto regionalDto = new RegionalDto();
        regionalDto.setId(1);
        regionalDto.setNome("LESTE");
        regionalDto.setSituacao(A);
        return regionalDto;
    }
}
