package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.dto.DiasUteisRequest;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiasUteisServiceTest {

    @InjectMocks
    private DiasUteisService diasUteisService;
    @Mock
    private FeriadoRepository feriadoRepository;

    @Test
    public void getDataComDiasUteisAdicionado_deveRetornarDataCorreto_quandoNaoTiverFeriadoEFinalSemanaNoPeriodo() {
        when(feriadoRepository.findAllDataFeriadoByCidadeId(any())).thenReturn(List.of());

        assertThat(diasUteisService.getDataComDiasUteisAdicionado(umDiasUteisRequest(2)))
            .isEqualTo(LocalDateTime.of(2020, 8, 7, 10, 0, 0));
    }

    @Test
    public void getDataComDiasUteisAdicionado_deveRetornarDataCorreto_quandoTiverFinalSemanaNoPeriodo() {
        when(feriadoRepository.findAllDataFeriadoByCidadeId(any())).thenReturn(List.of());

        assertThat(diasUteisService.getDataComDiasUteisAdicionado(umDiasUteisRequest(3)))
            .isEqualTo(LocalDateTime.of(2020, 8, 10, 10, 0, 0));
    }

    @Test
    public void getDataComDiasUteisAdicionado_deveRetornarDataCorreto_quandoTiverFeriadoNoPeriodo() {
        when(feriadoRepository.findAllDataFeriadoByCidadeId(any())).thenReturn(List.of(LocalDate.of(2020, 8, 6)));

        assertThat(diasUteisService.getDataComDiasUteisAdicionado(umDiasUteisRequest(1)))
            .isEqualTo(LocalDateTime.of(2020, 8, 7, 10, 0, 0));
    }

    @Test
    public void getDataComDiasUteisAdicionado_deveRetornarDataCorreto_quandoTiverFeriadoEFinalSemanaNoPeriodo() {
        when(feriadoRepository.findAllDataFeriadoByCidadeId(any())).thenReturn(List.of(LocalDate.of(2020, 8, 6)));

        assertThat(diasUteisService.getDataComDiasUteisAdicionado(umDiasUteisRequest(3)))
            .isEqualTo(LocalDateTime.of(2020, 8, 11, 10, 0, 0));
    }

    private DiasUteisRequest umDiasUteisRequest(Integer qtdDiasUteis) {
        return DiasUteisRequest.builder()
            .cidadeId(5578)
            .dataOriginal(LocalDateTime.of(2020, 8, 5, 10, 0, 0))
            .qtdDiasUteisAdicionar(qtdDiasUteis)
            .build();
    }
}
