package br.com.xbrain.autenticacao.modules.agendador.enums;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import br.com.xbrain.autenticacao.modules.agendador.rabbit.AgendadorSender;
import br.com.xbrain.autenticacao.modules.agendador.service.AgendadorService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;

public class EAgendadorTest {

    @Mock
    private AgendadorService service;
    @Mock
    private AgendadorSender agendadorSender;

    @Before()
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @SuppressWarnings({"MethodName", "AbbreviationAsWordInName"})
    public void AUT_22789_01_deveExecutarMetodo_quandoSolicitado() {
        var mqDto = new AgendadorMqDto();
        mqDto.setJobName("AUT_22789_01");
        EAgendador.AUT_22789_01.executar(service, mqDto);

        verify(service).flushCacheEstruturasAas(mqDto);
    }

    @Test
    @SuppressWarnings({"MethodName", "AbbreviationAsWordInName"})
    public void AUT_22789_02_deveExecutarMetodo_quandoSolicitado() {
        var mqDto = new AgendadorMqDto();
        mqDto.setJobName("AUT_22789_02");
        EAgendador.AUT_22789_02.executar(service, mqDto);

        verify(service).clearCacheFeriados(mqDto);
    }

    @Test
    public void convertFrom_deveRetornarValorEnum_quandoSolicitado() {
        var response = EAgendador.convertFrom("AUT_22789_01");

        assertThat(response)
            .isEqualTo(EAgendador.AUT_22789_01);
    }

    @Test
    public void convertFrom_deveLancarException_quandoNaoEncontrarValorNoEnum() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> EAgendador.convertFrom("AGD_0261"))
            .withMessage("Agendador não encontrado.");
    }
}

