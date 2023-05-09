package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.rabbitmq;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
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
    @Autowired
    private AutenticacaoService autenticacaoService;

    @RabbitListener(queues = "${app-config.queue.adicionar-permissao-tecnico-indicador}")
    public void adicionarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        try {
            log.info("Adicionando permissão de técnico indicador para os usuários: {}", dto.getUsuariosIds());

            var usuarioAutenticadoId = autenticacaoService.getUsuarioAutenticado().getUsuario().getId();

            permissaoEspecialService.save(usuarioService.getUsuariosPermissaoTecnicoIndicador(dto.getUsuariosIds()).stream()
                .map(usuario -> PermissaoEspecial.of(usuario.getId(), PERMISSAO_TECNICO_INDICADOR_ID, usuarioAutenticadoId))
                .collect(Collectors.toList()));
        } catch (Exception ex) {
            log.error("Erro ao processar fila para adicionar permissão de técnico indicador", ex);
        }
    }

    @RabbitListener(queues = "${app-config.queue.remover-permissao-tecnico-indicador}")
    public void removerPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        try {
            log.info("Removendo permissão de técnico indicador para os usuários: {}", dto.getUsuariosIds());

            usuarioService.getUsuariosPermissaoTecnicoIndicador(dto.getUsuariosIds()).stream()
                .forEach(usuario -> permissaoEspecialService
                    .remover(usuario.getId(), PERMISSAO_TECNICO_INDICADOR_ID));
        } catch (Exception ex) {
            log.error("Erro ao processar fila para remover permissão de técnico indicador", ex);
        }
    }
}
