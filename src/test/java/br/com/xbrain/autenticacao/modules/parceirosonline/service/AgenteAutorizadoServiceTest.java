package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class AgenteAutorizadoServiceTest {

    @InjectMocks
    private AgenteAutorizadoService service;
    @Mock
    private AgenteAutorizadoClient client;
    @Mock
    private AutenticacaoService autenticacaoService;

    @Test
    public void getIdsUsuariosSubordinados_idsNaoIncluindoOProprio_quandoIncluirProprioForFalse() {
        when(client.getIdUsuariosDoUsuario(eq(Map.of()))).thenReturn(Set.of(100, 41, 2, 30));

        assertThat(service.getIdsUsuariosSubordinados(false))
            .containsExactlyInAnyOrder(100, 41, 2, 30);

        verify(autenticacaoService, never()).getUsuarioId();
    }

    @Test
    public void getIdsUsuariosSubordinados_idsIncluindoOProprio_quandoIncluirProprioForTrue() {
        when(client.getIdUsuariosDoUsuario(eq(Map.of()))).thenReturn(Set.of(100, 41, 2, 30));

        when(autenticacaoService.getUsuarioId()).thenReturn(333);

        assertThat(service.getIdsUsuariosSubordinados(true))
            .containsExactlyInAnyOrder(100, 41, 2, 30, 333);
    }
}
