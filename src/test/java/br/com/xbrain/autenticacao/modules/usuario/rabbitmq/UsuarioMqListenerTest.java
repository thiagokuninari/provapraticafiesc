package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioMqListenerTest {

    @InjectMocks
    private UsuarioMqListener usuarioMqListener;
    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AutenticacaoService autenticacaoService;

    @Test
    public void save_naoDeveSalvarUsuarioEEnviarParaFilaDeFalha_quandoDadosFaltantes() {
        doThrow(new ValidacaoException("Error")).when(usuarioService).saveFromQueue(any());

        usuarioMqListener.save(new UsuarioMqRequest());

        var requestWithException = new UsuarioMqRequest();
        requestWithException.setException("Error");

        verify(usuarioService).enviarParaFilaDeErroCadastroUsuarios(requestWithException);
    }

    @Test
    public void save_deveSalvarUsuario_quandoDadosFornecidos() {
        var request = new UsuarioMqRequest();

        usuarioMqListener.save(request);
        verify(usuarioService).saveFromQueue(request);
    }

    @Test
    public void atualizar_naoDeveSalvarUsuarioEEnviarParaFilaDeFalha_quandoDadosFaltantes() {
        doThrow(new ValidacaoException("Error")).when(usuarioService).updateFromQueue(any());

        usuarioMqListener.atualizar(new UsuarioMqRequest());

        var requestWithException = new UsuarioMqRequest();
        requestWithException.setException("Error");

        verify(usuarioService).enviarParaFilaDeErroAtualizacaoUsuarios(requestWithException);
    }

    @Test
    public void atualizar_deveAtualizarUsuario_quandoDadosFornecidos() {
        var request = new UsuarioMqRequest();

        usuarioMqListener.atualizar(request);
        verify(usuarioService).updateFromQueue(request);
    }

    @Test
    public void atualizar_deveAtualizarUsuarioLojaFuturo_quandoDadosFornecidos() {
        var request = new UsuarioLojaFuturoMqRequest();

        usuarioMqListener.atualizar(request);
        verify(usuarioService).updateUsuarioLojaFuturoFromQueue(request);
    }

    @Test
    public void atualizar_deveAtualizarUsuarioAgenteAutorizado_quandoDadosFornecidos() {
        var request = new UsuarioMqAtualizacaoRequest();

        usuarioMqListener.atualizarUsuariosAa(request);
        verify(usuarioService).atualizarUsuariosAgentesAutorizados(request);
    }

    @Test
    public void recuperar_deveRecuperarUsuarioAgenteAutorizado_quandoDadosFornecidos() {
        var request = new UsuarioMqRequest();

        usuarioMqListener.recuperar(request);
        verify(usuarioService).recuperarUsuariosAgentesAutorizados(request);
    }

    @Test
    public void alterarEmail_deveAterarEmail_quandoDadosFornecidos() {
        var request = new UsuarioAlteracaoRequest();

        usuarioMqListener.alterarEmail(request);
        verify(usuarioService).alterarEmailUsuario(request);
    }

    @Test
    public void alterarCargo_deveAterarCargo_quandoDadosFornecidos() {
        var request = new UsuarioAlteracaoRequest();

        usuarioMqListener.alterarCargo( request);
        verify(usuarioService).alterarCargoUsuario(request);
    }

    @Test
    public void ativar_deveAtivar_quandoDadosFornecidos() {
        var request = new UsuarioAtivacaoDto();

        usuarioMqListener.ativar(request);
        verify(usuarioService).ativar(request);
    }

    @Test
    public void inativar_deveInativar_quandoDadosFornecidos() {
        var request = new UsuarioInativacaoDto();

        usuarioMqListener.inativar(request);
        verify(usuarioService).inativar(request);
    }

    @Test
    public void alterarSenha_deveAlterarSenha_quandoDadosFornecidos() {
        var request = new UsuarioAlterarSenhaDto();

        usuarioMqListener.alterarSenha(request);
        verify(usuarioService).alterarSenhaAa(request);
    }

    @Test
    public void alterarSituacao_deveAlterarSituacao_quandoDadosFornecidos() {
        var request = new UsuarioMqRequest();

        usuarioMqListener.alterarSituacao(request);
        verify(usuarioService).alterarSituacao(request);
    }

    @Test
    public void logoutUsuarios_develogoutUsuarios_quandoDadosFornecidos() {
        var usuario = new UsuarioLogoutDto();
        usuario.setUsuariosIds(List.of(1, 2, 3));

        usuarioMqListener.logoutUsuarios(usuario);

        verify(autenticacaoService).logout(usuario.getUsuariosIds());
    }

    @Test
    public void usuarioRemanejar_deveAtivar_quandoDadosFornecidos() {
        var request = new UsuarioMqRequest();

        usuarioMqListener.usuarioRemanejar(request);
        verify(usuarioService).remanejarUsuario(request);
    }

    @Test
    public void usuarioRemanejar_naoDeveRemanejarUsuariosEEnviarParaFilaDeFalha_quandoDadosFaltantes() {
        doThrow(new ValidacaoException("Error")).when(usuarioService).remanejarUsuario(any());
        var requestWithException = new UsuarioMqRequest();

        usuarioMqListener.usuarioRemanejar(requestWithException);

        requestWithException.setException("Error");

        verify(usuarioService).enviarParaFilaDeErroRemanejarUsuarios(requestWithException);
    }

    @Test
    public void inativarPorAgenteAutorizado_deveInativarPorAgenteAutorizado_quandoDadosFornecidos() {
        var request = new UsuarioDto();

        usuarioMqListener.inativarPorAgenteAutorizado(request);
        verify(usuarioService).inativarPorAgenteAutorizado(request);
    }

    @Test
    public void atualizarPermissaoEquipeTecnica_deveAtualizarPermissaoEquipeTecnica_quandoDadosFornecidos() {
        var request = new PermissaoEquipeTecnicaDto();

        usuarioMqListener.atualizarPermissaoEquipeTecnica(request);
        verify(usuarioService).atualizarPermissaoEquipeTecnica(request);
    }

}
