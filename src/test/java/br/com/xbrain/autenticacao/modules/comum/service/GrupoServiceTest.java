package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.predicate.GrupoPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.GrupoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static helpers.GrupoHelper.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrupoServiceTest {

    private static final int REGIONAL_LESTE_ID = 1;
    private static final int REGIONAL_SP_ID = 2;
    private static final int REGIONAL_SUL_ID = 3;
    private static final int USUARIO_ID = 1;

    @InjectMocks
    private GrupoService grupoService;
    @Mock
    private GrupoRepository grupoRepository;
    private GrupoPredicate predicate;

    @Before
    public void setUp() throws Exception {
        predicate = new GrupoPredicate().filtrarPermitidos(USUARIO_ID);
    }

    @Test
    public void getAllByRegionalIdAndUsuarioId_deveRetornarGrupo_quandoUsuarioPossuirRegionalSul() {
        when(grupoRepository.findAllByRegionalId(REGIONAL_SUL_ID, predicate.build()))
            .thenReturn(List.of(umGrupoNorteDoParana()));

        assertThat(grupoService.getAllByRegionalIdAndUsuarioId(REGIONAL_SUL_ID, USUARIO_ID))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(20, "NORTE DO PARANÁ"));
    }

    @Test
    public void getAllByRegionalIdAndUsuarioId_deveRetornarGrupo_quandoUsuarioPossuirRegionalSp() {
        when(grupoRepository.findAllByRegionalId(REGIONAL_SP_ID, predicate.build()))
            .thenReturn(List.of(umGrupoMarilia()));

        assertThat(grupoService.getAllByRegionalIdAndUsuarioId(REGIONAL_SP_ID, USUARIO_ID))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(15, "MARILIA"));
    }

    @Test
    public void getAllByRegionalIdAndUsuarioId_deveRetornarVazio_quandoUsuarioNaoPossuirRegionalLeste() {
        assertThat(grupoService.getAllByRegionalIdAndUsuarioId(REGIONAL_LESTE_ID, USUARIO_ID))
                .isEmpty();
    }

    @Test
    public void getAllByRegionalIdAndUsuarioId_deveRetornarGrupo_quandoUsuarioPossuirRegionalLeste() {
        when(grupoRepository.findAllByRegionalId(REGIONAL_LESTE_ID, predicate.build()))
            .thenReturn(List.of(umGrupoNordeste()));

        assertThat(grupoService.getAllByRegionalIdAndUsuarioId(REGIONAL_LESTE_ID, USUARIO_ID))
                .isNotNull()
                .extracting("id", "nome")
                .containsExactly(tuple(4, "NORDESTE"));
    }

    @Test
    public void findById_deveRetornarUmGrupo_seExistir() {
        when(grupoRepository.findById(1)).thenReturn(Optional.of(umGrupoPortoVelho()));

        assertThat(grupoService.findById(1)).isEqualTo(GrupoDto.of(umGrupoPortoVelho()));
    }

    @Test
    public void findById_deveLancarException_seGrupoNaoExistir() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> grupoService.findById(1516516))
            .withMessage("Grupo não encontrado.");
    }
}
