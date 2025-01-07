package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHierarquiaAtivoService;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CanalServiceTest {

    @InjectMocks
    private CanalService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private ApplicationContext context;

    @Test
    public void usuarioHierarquia_deveRetornarServicoCorreto_quandoUsuarioCanalForAtivoProprio() {
        when(autenticacaoService.getUsuarioCanal())
            .thenReturn(ECanal.ATIVO_PROPRIO);

        var usuarioHierarquia = mock(UsuarioHierarquiaAtivoService.class);

        when(context.getBean(UsuarioHierarquiaAtivoService.class)).thenReturn(usuarioHierarquia);

        assertThat(service.usuarioHierarquia())
            .isEqualTo(usuarioHierarquia);

        verify(autenticacaoService).getUsuarioCanal();
    }

    @Test
    public void usuarioHierarquia_deveRetornarNotImplementedException_quandoUsuarioCanalNaoForAtivoProprio() {
        when(autenticacaoService.getUsuarioCanal())
            .thenReturn(ECanal.INTERNET);

        assertThatThrownBy(() -> service.usuarioHierarquia())
            .isInstanceOf(NotImplementedException.class)
            .hasMessage("Funcionalidade não disponível para canal selecionado");

        verify(autenticacaoService).getUsuarioCanal();
    }
}
