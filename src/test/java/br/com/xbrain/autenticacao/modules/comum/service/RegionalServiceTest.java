package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.predicate.RegionalPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.RegionalRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegionalServiceTest {

    private static final List<Integer> NOVAS_REGIONAIS_IDS = List.of(1025, 1027);
    private static final int USUARIO_ID = 1;

    @InjectMocks
    private RegionalService regionalService;
    @Mock
    private RegionalRepository regionalRepository;
    @Mock
    private AutenticacaoService autenticacaoService;

    @Test
    public void getAll_deveRetornarListaRegional_quandoUsuarioPossuirPermissao() {
        var usuario = umUsuarioAutenticadoNivelBackoffice();
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuario);
        var predicateFiltrarPermitidos = new RegionalPredicate().filtrarPermitidos(usuario);

        when(regionalRepository.getAll(predicateFiltrarPermitidos.build()))
            .thenReturn(List.of(Regional.builder().id(1025).nome("RNE").build()));

        assertThat(regionalService.getAll())
            .isNotNull()
            .extracting("id", "nome")
            .containsExactly(tuple(1025, "RNE"));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(regionalRepository).getAll(predicateFiltrarPermitidos.build());
    }

    @Test
    public void findAllAtivos_deveRetornarRegionalDtoAtivo_quandoSolicitado() {
        when(regionalRepository.findAllBySituacaoAndNovaRegional(ESituacao.A, Eboolean.V))
            .thenReturn(List.of(Regional.builder().id(1025).nome("RNE").build()));

        assertThat(regionalService.findAllAtivos())
            .isNotNull()
            .extracting("id", "nome")
            .containsExactly(tuple(1025, "RNE"));

        verify(regionalRepository).findAllBySituacaoAndNovaRegional(ESituacao.A, Eboolean.V);
    }

    @Test
    public void getAtivosParaComunicados_deveRetornarListaRegionalDto_quandoSolicitado() {
        var usuario = umUsuarioAutenticadoNivelBackoffice();

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuario);
        var predicateFiltrarPermitidos = new RegionalPredicate().filtrarPermitidos(usuario);

        when(regionalRepository.getAll(predicateFiltrarPermitidos.build()))
            .thenReturn(List.of(Regional.builder().id(1025).nome("RNE").build()));
        assertThat(regionalService.getAtivosParaComunicados())
            .isNotNull()
            .extracting("id", "nome")
            .containsExactly(tuple(1025, "RNE"));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(regionalRepository).getAll(predicateFiltrarPermitidos.build());
    }

    @Test
    public void getAllByUsuarioId_deveRetornarRegionaisSulESp_doUsuarioInformadoPeloParametro() {
        when(regionalRepository.getAllByUsuarioId(USUARIO_ID))
            .thenReturn(List.of(Regional.builder().id(1027).nome("RPS").build()));

        assertThat(regionalService.getAllByUsuarioId(USUARIO_ID))
            .isNotNull()
            .extracting("value", "label")
            .containsExactly(
                tuple(1027, "RPS")
            );
    }

    @Test
    public void getAllByUsuarioId_deveRetornarRegionalLeste_doUsuarioInformadoPeloParametro() {
        when(regionalRepository.getAllByUsuarioId(USUARIO_ID))
            .thenReturn(List.of(Regional.builder().id(1025).nome("RNE").build()));

        assertThat(regionalService.getAllByUsuarioId(USUARIO_ID))
            .isNotNull()
            .extracting("value", "label")
            .containsExactly(tuple(1025, "RNE"));
    }

    @Test
    public void getRegionalIds_deveRetornarIdsDasRegionais_doUsuarioInformadoPeloParametro() {
        when(regionalRepository.getAllByUsuarioId(USUARIO_ID))
            .thenReturn(List.of(Regional.builder().id(1027).nome("RPS").build()));

        assertThat(regionalService.getRegionalIds(USUARIO_ID))
            .isNotNull()
            .containsExactly(1027);
    }

    @Test
    public void findById_deveRetornarUmaRegional_seExistir() {
        when(regionalRepository.findById(USUARIO_ID))
            .thenReturn(Optional.of(Regional.builder().id(1).nome("LESTE").situacao(A).build()));

        assertThat(regionalService.findById(1))
            .isEqualTo(umRegionalDto());
    }

    @Test
    public void findById_deveLancarException_seRegionalNaoExistir() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> regionalService.findById(16516))
            .withMessage("Regional não encontrada.");
    }

    @Test
    public void getNovasRegionaisIds_deveRetornarIdsDeNovasRegionais_quandoSolicitado() {
        when(regionalRepository.getNovasRegionaisIds()).thenReturn(NOVAS_REGIONAIS_IDS);

        assertThat(regionalService.getNovasRegionaisIds()).isEqualTo(NOVAS_REGIONAIS_IDS);
    }

    RegionalDto umRegionalDto() {
        RegionalDto regionalDto = new RegionalDto();
        regionalDto.setId(1);
        regionalDto.setNome("LESTE");
        regionalDto.setSituacao(A);
        return regionalDto;
    }
}
