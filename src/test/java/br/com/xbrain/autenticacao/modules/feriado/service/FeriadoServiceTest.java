package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.feriado.helper.FeriadoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.cidadeResponseBarueri;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.umMapApenasDistritosComCidadePai;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeriadoServiceTest {

    @InjectMocks
    private FeriadoService feriadoService;
    @Mock
    private FeriadoRepository feriadoRepository;
    @Mock
    private CidadeService cidadeService;

    @Test
    public void obterFeriadosByFiltros_deveRetornarPageVazia_quandoNaoExistirFeriados() {
        var pageRequest = new PageRequest();
        var filtros = new FeriadoFiltros();
        var predicate = filtros.toPredicate().build();

        when(feriadoRepository.findAll(predicate, pageRequest)).thenReturn(new PageImpl<>(List.of()));

        assertThat(feriadoService.obterFeriadosByFiltros(pageRequest, filtros))
            .isEmpty();

        verify(feriadoRepository).findAll(predicate, pageRequest);
        verifyZeroInteractions(cidadeService);
    }

    @Test
    public void obterFeriadosByFiltros_deveRetornarPageFeriadoResponse_quandoCidadesEstiveremNaListaDeFeriadoResponse() {
        var pageRequest = new PageRequest();
        var filtros = new FeriadoFiltros();
        var predicate = filtros.toPredicate().build();

        when(feriadoRepository.findAll(predicate, pageRequest)).thenReturn(umaPageFeriadosCompleta());
        when(cidadeService.getCidadesDistritos(Eboolean.V)).thenReturn(umMapApenasDistritosComCidadePai());

        assertThat(feriadoService.obterFeriadosByFiltros(pageRequest, filtros))
            .hasSize(5)
            .extracting("id", "nome", "dataFeriado", "dataCadastro", "feriadoNacional", "cidadeId",
                "cidadeNome", "fkCidade", "cidadePai", "estadoId", "estadoNome", "tipoFeriado", "anoReferencia")
            .containsExactly(
                tuple(34015, "Revolução Constitucionalista", LocalDate.of(2023, 7, 9), LocalDateTime.of(2023, 1, 27, 17, 30),
                    Eboolean.F, 33618, "ALDEIA", 4864, "BARUERI", 2, "SAO PAULO", ETipoFeriado.MUNICIPAL, 2023),
                tuple(33154, "Revolução Constitucionalista", LocalDate.of(2023, 7, 9), LocalDateTime.of(2023, 1, 30, 11, 20),
                    Eboolean.F, 4864, "BARUERI", null, null, 2, "SAO PAULO", ETipoFeriado.MUNICIPAL, 2023),
                tuple(33864, "Revolução Constitucionalista", LocalDate.of(2023, 7, 9), LocalDateTime.of(2023, 1, 30, 11, 20),
                    Eboolean.F, 33252, "JARDIM BELVAL", 4864, "BARUERI", 2, "SAO PAULO", ETipoFeriado.MUNICIPAL, 2023),
                tuple(33867, "Revolução Constitucionalista", LocalDate.of(2023, 7, 9), LocalDateTime.of(2023, 1, 30, 11, 20),
                    Eboolean.F, 33255, "JARDIM SILVEIRA", 4864, "BARUERI", 2, "SAO PAULO", ETipoFeriado.MUNICIPAL, 2023),
                tuple(13853, "Aniversário da cidade", LocalDate.of(2023, 12, 10), LocalDateTime.of(2023, 1, 27, 16, 45),
                    Eboolean.F, 5578, "LONDRINA", null, null, 1, "PARANA", ETipoFeriado.MUNICIPAL, 2023)
            );

        verify(feriadoRepository).findAll(predicate, pageRequest);
        verify(cidadeService).getCidadesDistritos(Eboolean.V);
    }

    @Test
    public void getFeriadoById_deveLancarNotFoundException_quandoNaoEncontrarFeriadoPorId() {
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> feriadoService.getFeriadoById(13853))
            .withMessage("Feriado não encontrado.");

        verify(feriadoRepository).findById(13853);
    }

    @Test
    public void getFeriadoById_deveRetornarFeriadoResponse_quandoExistirFeriadoEmCidade() {
        when(feriadoRepository.findById(13853))
            .thenReturn(Optional.of(umFeriadoMunicipalCidadeLondrina()));

        assertThat(feriadoService.getFeriadoById(13853))
            .extracting("id", "nome", "dataFeriado", "dataCadastro", "feriadoNacional", "cidadeId", "cidadeNome",
                "fkCidade", "cidadePai", "estadoId", "estadoNome", "tipoFeriado", "anoReferencia")
            .containsExactly(13853, "Aniversário da cidade",
                LocalDate.of(2023, 12, 10), LocalDateTime.of(2023, 1, 27, 16, 45),
                Eboolean.F, 5578, "LONDRINA", null, null, 1, "PARANA", ETipoFeriado.MUNICIPAL, 2023);

        verify(feriadoRepository).findById(13853);
        verifyZeroInteractions(cidadeService);
    }

    @Test
    public void getFeriadoById_deveRetornarFeriadoResponseComCidadePai_quandoExistirFeriadoEmDistrito() {
        when(feriadoRepository.findById(34015))
            .thenReturn(Optional.of(umFeriadoMunicipalDistritoAldeia()));
        when(cidadeService.getCidadeById(4864))
            .thenReturn(cidadeResponseBarueri());

        assertThat(feriadoService.getFeriadoById(34015))
            .extracting("id", "nome", "dataFeriado", "dataCadastro", "feriadoNacional", "cidadeId", "cidadeNome",
                "fkCidade", "cidadePai", "estadoId", "estadoNome", "tipoFeriado", "anoReferencia")
            .containsExactly(34015, "Revolução Constitucionalista",
                LocalDate.of(2023, 7, 9), LocalDateTime.of(2023, 1, 27, 17, 30),
                Eboolean.F, 33618, "ALDEIA", 4864, "BARUERI", 2, "SAO PAULO", ETipoFeriado.MUNICIPAL, 2023);

        verify(feriadoRepository).findById(34015);
        verify(cidadeService).getCidadeById(4864);
    }
}
