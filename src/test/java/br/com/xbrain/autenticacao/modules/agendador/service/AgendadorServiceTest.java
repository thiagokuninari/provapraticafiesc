package br.com.xbrain.autenticacao.modules.agendador.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AgendadorServiceTest {

    @InjectMocks
    private AgendadorService agendadorService;
    @Mock
    private AgenteAutorizadoService aaService;
    @Mock
    private FeriadoService feriadoService;

    @Test
    public void flushCacheEstruturasAas_deveChamarAaService_quandoSolicitado() {
        agendadorService.flushCacheEstruturasAas();
        verify(aaService).flushCacheEstruturasAas();
    }

    @Test
    public void clearCacheFeriados_deveChamarFeriadoService_quandoSolicitado() {
        agendadorService.clearCacheFeriados();
        verify(feriadoService).flushCacheFeriados();
    }
}

