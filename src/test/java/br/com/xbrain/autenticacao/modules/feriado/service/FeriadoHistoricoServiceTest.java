package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.feriado.model.FeriadoHistorico;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoHistoricoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import static br.com.xbrain.autenticacao.modules.feriado.helper.FeriadoHelper.umFeriadoNacional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class FeriadoHistoricoServiceTest {
    @Mock
    private FeriadoHistoricoRepository feriadoHistoricoRepository;
    @InjectMocks
    private FeriadoHistoricoService feriadoHistoricoService;
    @Captor
    private ArgumentCaptor<FeriadoHistorico> historicoCaptor;

    @Test
    public void salvarHistorico_deveSalvarHistorico_quandoChamado() {
        feriadoHistoricoService.salvarHistorico(
            umFeriadoNacional(),
            "EDITADO",
            UsuarioAutenticado.builder()
                .id(2222)
                .build());

        verify(feriadoHistoricoRepository, times(1)).save(historicoCaptor.capture());
        assertThat(historicoCaptor.getValue())
            .extracting("feriado.id", "observacao", "usuario.id")
            .containsExactlyInAnyOrder(1234, "EDITADO", 2222);
    }
}
