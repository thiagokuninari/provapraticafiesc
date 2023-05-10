package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.rabbitmq;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class AgenteAutorizadoMqListener {

    private static final Integer PERMISSAO_TECNICO_INDICADOR_ID = 253;

    @Autowired
    private PermissaoEspecialService permissaoEspecialService;
    @Autowired
    private UsuarioService usuarioService;

    @RabbitListener(queues = "${app-config.queue.adicionar-permissao-tecnico-indicador}")
    public void adicionarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        log.info("Adicionando permissão de técnico indicador para os usuários do Agente Autorizado: {}",
            dto.getAgenteAutorizadoId());

        try {
            var permissoes = usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(dto.getUsuariosIds())
                .stream()
                .filter(usuario -> !validarUsuarioComPermissaoTecnicoIndicador(usuario.getId()))
                .map(usuario -> PermissaoEspecial.of(
                    usuario.getId(), PERMISSAO_TECNICO_INDICADOR_ID, dto.getUsuarioAutenticadoId()))
                .collect(Collectors.toList());

            permissaoEspecialService.save(permissoes);
        } catch (Exception ex) {
            log.error("Erro ao processar fila para adicionar permissão de técnico indicador", ex);
        }
    }

    @RabbitListener(queues = "${app-config.queue.remover-permissao-tecnico-indicador}")
    public void removerPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        log.info("Removendo permissão de técnico indicador dos usuários do Agente Autorizado: {}",
            dto.getAgenteAutorizadoId());

        try {
            usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(dto.getUsuariosIds())
                .stream()
                .filter(usuario -> validarUsuarioComPermissaoTecnicoIndicador(usuario.getId()))
                .forEach(usuario -> permissaoEspecialService.remover(
                    usuario.getId(), PERMISSAO_TECNICO_INDICADOR_ID, dto.getUsuarioAutenticadoId()));
        } catch (Exception ex) {
            log.error("Erro ao processar fila para remover permissão de técnico indicador", ex);
        }
    }

    private boolean validarUsuarioComPermissaoTecnicoIndicador(Integer usuarioId) {
        return permissaoEspecialService.hasPermissaoEspecialAtiva(
            usuarioId, PERMISSAO_TECNICO_INDICADOR_ID);
    }
}
