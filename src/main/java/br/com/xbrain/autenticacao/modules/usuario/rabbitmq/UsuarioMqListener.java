package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.config.IgnoreRabbitProfile;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioAaTipoFeederDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Slf4j
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

    @RabbitListener(queues = "${app-config.queue.usuario-atualizacao-lojafuturo}")
    public void atualizar(UsuarioLojaFuturoMqRequest usuarioMqRequest) {
        service.updateUsuarioLojaFuturoFromQueue(usuarioMqRequest);
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

    @RabbitListener(queues = "${app-config.queue.usuario-remanejar-pol}")
    public void usuarioRemanejar(UsuarioMqRequest usuarioMqRequest) {
        service.remanejarUsuario(usuarioMqRequest);
    }

    @RabbitListener(queues = "${app-config.queue.usuario-inativacao-por-aa}")
    public void inativarPorAgenteAutorizado(UsuarioDto usuario) {
        service.inativarPorAgenteAutorizado(usuario);
    }

    @RabbitListener(queues = "${app-config.queue.permissao-agente-autorizado-equipe-tecnica}")
    public void atualizarPermissaoEquipeTecnica(PermissaoEquipeTecnicaDto dto) {
        service.atualizarPermissaoEquipeTecnica(dto);
    }

    @RabbitListener(queues = "${app-config.queue.atualizar-permissao-especial-aa-residencial}")
    public void atualizarPermissaoEspecialAaResidencial(UsuarioAaTipoFeederDto usuarioAaTipoFeederDto) {
        service.atualizarPermissaoEspecialAaResidencial(usuarioAaTipoFeederDto);
    }
}

