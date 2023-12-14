package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioMqListenerTest {

    @InjectMocks
    private UsuarioMqListener usuarioMqListener;
    @Mock
    private UsuarioService usuarioService;

    @Test
    public void save_naoDeveSalvarUsuarioEEnviarParaFilaDeFalha_quandoDadosFaltantes() {
        doThrow(new ValidacaoException("Error")).when(usuarioService).saveFromQueue(any());

        usuarioMqListener.save(new UsuarioMqRequest());

        var requestWithException = new UsuarioMqRequest();
        requestWithException.setException("Error");

        verify(usuarioService).enviarParaFilaDeErroCadastroUsuarios(requestWithException);
    }

    @Test
    public void atualizar_naoDeveSalvarUsuarioEEnviarParaFilaDeFalha_quandoDadosFaltantes() {
        doThrow(new ValidacaoException("Error")).when(usuarioService).updateFromQueue(any());

        usuarioMqListener.atualizar(new UsuarioMqRequest());

        var requestWithException = new UsuarioMqRequest();
        requestWithException.setException("Error");

        verify(usuarioService).enviarParaFilaDeErroAtualizacaoUsuarios(requestWithException);
    }
}
