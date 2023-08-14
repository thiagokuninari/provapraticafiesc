package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository.ImportacaoAutomaticaFeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoAutomacaoClient;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.feriado.helper.FeriadoHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImportacaoAutomaticaFeriadoServiceTest {

    @InjectMocks
    private ImportacaoAutomaticaFeriadoService service;
    @Mock
    private FeriadoService feriadoService;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private FeriadoAutomacaoClient feriadoAutomacaoClient;
    @Mock
    private CidadeRepository cidadeRepositoy;
    @Mock
    private ImportacaoAutomaticaFeriadoRepository importacaoAutomaticaFeriadoRepository;
    @Mock
    private UfRepository ufRepository;
    @Mock
    private FeriadoRepository feriadoRepository;

    private static Page<ImportacaoFeriado> umaPageImportacaoHistorico() {
        return new PageImpl<>(
            List.of(umFeriadoImportacaoHistorico(1),
                umFeriadoImportacaoHistorico(2)
            ));
    }

    private static ImportacaoFeriado umFeriadoImportacaoHistorico(Integer id) {
        return ImportacaoFeriado.builder()
            .id(id)
            .situacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO)
            .usuarioCadastroId(1)
            .build();
    }

    @Test
    public void importarTodosOsFeriadoAnuais_deveImportarTodosOsFeriados_seSolicitado() {
        var listaFeriados = umaListFeriadoAutomacao();
        var ano = LocalDate.now().plusYears(1).getYear();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(ano, "PR", "londrina")).thenReturn(listaFeriados);
        when(cidadeRepositoy.findAllCidades()).thenReturn(List.of(umaCidade(1, "londrina"), umaCidade(2, "maringa")));
        when(ufRepository.findByOrderByNomeAsc()).thenReturn(umaListUf());
        when(feriadoAutomacaoClient.buscarFeriadosNacionais(ano)).thenReturn(listaFeriados);
        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(ano, "PR")).thenReturn(listaFeriados);
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class))).thenReturn(new ImportacaoFeriado());
        when(feriadoService.validarSeFeriadoNaoCadastrado(any())).thenReturn(true);

        service.importarTodosOsFeriadoAnuais();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosMunicipais(ano, "PR", "londrina");
        verify(cidadeRepositoy).findAllCidades();
        verify(ufRepository).findByOrderByNomeAsc();
        verify(feriadoAutomacaoClient).buscarFeriadosNacionais(ano);
        verify(feriadoAutomacaoClient, times(2)).buscarFeriadosEstaduais(ano, "PR");
        verify(importacaoAutomaticaFeriadoRepository, times(2)).save(any(ImportacaoFeriado.class));
        verify(feriadoService, times(8)).validarSeFeriadoNaoCadastrado(any());
    }

    @Test
    public void importarTodosOsFeriadoAnuais_deveRetornarException_seHouverErroNaChamadaDoClientDeBuscaDeFeriadosMunicipais() {
        var ano = LocalDate.now().plusYears(1).getYear();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(ano, "PR", "londrina"))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));
        when(cidadeRepositoy.findAllCidades()).thenReturn(List.of(umaCidade(1, "londrina"), umaCidade(2, "maringa")));
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class))).thenReturn(new ImportacaoFeriado());

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.importarTodosOsFeriadoAnuais())
            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosMunicipais(ano, "PR", "londrina");
        verify(cidadeRepositoy).findAllCidades();
        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarTodosOsFeriadoAnuais_deveRetornarException_seHouverErroNaChamadaDoClientDeBuscaDeFeriadosEstaduais() {
        var ano = LocalDate.now().plusYears(1).getYear();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(ano, "PR"))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));
        when(ufRepository.findByOrderByNomeAsc()).thenReturn(umaListUf());
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class))).thenReturn(new ImportacaoFeriado());

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.importarTodosOsFeriadoAnuais())
            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosEstaduais(ano, "PR");
        verify(ufRepository).findByOrderByNomeAsc();
        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarTodosOsFeriadoAnuais_deveRetornarException_seHouverErroNaChamadaDoClientDeBuscaDeFeriadosNacionais() {
        var ano = LocalDate.now().plusYears(1).getYear();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosNacionais(ano))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class))).thenReturn(new ImportacaoFeriado());

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.importarTodosOsFeriadoAnuais())
            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosNacionais(ano);
        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void getAllImportacaoHistorico_deveRetornarPageDeHistoricos_seSolicitado() {
        var filtros = new FeriadoFiltros();
        filtros.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        var predicate = new FeriadoPredicate().comSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);

        when(importacaoAutomaticaFeriadoRepository.findAll(predicate.build(), new PageRequest()))
            .thenReturn(umaPageImportacaoHistorico());

        assertThat(service.getAllImportacaoHistorico(new PageRequest(), filtros))
            .extracting("id", "usuarioCadastroId", "situacaoFeriadoAutomacao")
            .containsExactlyInAnyOrder(
                tuple(1, 1, ESituacaoFeriadoAutomacao.IMPORTADO),
                tuple(2, 1, ESituacaoFeriadoAutomacao.IMPORTADO));

        verify(importacaoAutomaticaFeriadoRepository).findAll(predicate.build(), new PageRequest());
    }

    @Test
    public void getAllImportacaoHistorico_deveRetornarPageVazia_seNaoHouverHistoricos() {
        var filtros = new FeriadoFiltros();
        filtros.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        var predicate = new FeriadoPredicate().comSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);

        when(importacaoAutomaticaFeriadoRepository.findAll(predicate.build(), new PageRequest()))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        assertThat(service.getAllImportacaoHistorico(new PageRequest(), filtros)).isEmpty();

        verify(importacaoAutomaticaFeriadoRepository).findAll(predicate.build(), new PageRequest());
    }
}
