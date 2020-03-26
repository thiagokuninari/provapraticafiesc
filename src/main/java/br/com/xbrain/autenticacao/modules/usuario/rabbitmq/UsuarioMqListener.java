package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.config.IgnoreRabbitProfile;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(IgnoreRabbitProfile.class)
public class UsuarioMqListener {

    @Autowired
    private UsuarioService service;
    @Autowired
    private AutenticacaoService autenticacaoService;

    @RabbitListener(queues = "${app-config.queue.usuario-cadastro}")
    public void save(UsuarioMqRequest usuarioMqRequest) {
        service.saveFromQueue(usuarioMqRequest);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-atualizacao}")
    public void atualizar(UsuarioMqRequest usuarioMqRequest) {
        service.updateFromQueue(usuarioMqRequest);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-aa-atualizacao}")
    public void atualizarUsuariosAa(UsuarioMqAtualizacaoRequest usuarioAtualizacaoRequest) {
        service.atualizarUsuariosAgentesAutorizados(usuarioAtualizacaoRequest);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-recuperacao}")
    public void recuperar(UsuarioMqRequest usuarioMqRequest) {
        service.recuperarUsuariosAgentesAutorizados(usuarioMqRequest);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-alterar-email}")
    public void alterarEmail(UsuarioAlteracaoRequest usuarioAlteracaoRequest) {
        service.alterarEmailUsuario(usuarioAlteracaoRequest);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-alterar-cargo}")
    public void alterarCargo(UsuarioAlteracaoRequest usuarioAlteracaoRequest) {
        service.alterarCargoUsuario(usuarioAlteracaoRequest);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-ativar}")
    public void ativar(UsuarioAtivacaoDto usuarioAtivacaoDto) {
        service.ativar(usuarioAtivacaoDto);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-inativar}")
    public void inativar(UsuarioInativacaoDto usuarioInativacaoDto) {
        service.inativar(usuarioInativacaoDto);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-alterar-senha}")
    public void alterarSenha(UsuarioAlterarSenhaDto usuarioAlterarSenhaDto) {
        service.alterarSenhaAa(usuarioAlterarSenhaDto);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-alterar-situacao}")
    public void alterarSituacao(UsuarioMqRequest usuario) {
        service.alterarSituacao(usuario);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-logout}")
    public void logoutUsuarios(UsuarioLogoutDto usuarioLogoutDto) {
        autenticacaoService.logout(usuarioLogoutDto.getUsuariosIds());
    }
}

