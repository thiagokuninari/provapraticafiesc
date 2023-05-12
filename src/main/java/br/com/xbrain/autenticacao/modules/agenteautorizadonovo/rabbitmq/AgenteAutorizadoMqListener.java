package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.rabbitmq;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Component
public class AgenteAutorizadoMqListener {

    private static final Integer PERMISSAO_TECNICO_INDICADOR_ID = 253;
    private static final String INFO_ADICIONAR_PERMISSAO =
        "Adicionando permissão de técnico indicador para os usuários do agente autorizado {}";
    private static final String INFO_REMOVER_PERMISSAO =
        "Removendo permissão de técnico indicador dos usuários do agente autorizado {}";
    private static final String ERRO_ADICIONAR_PERMISSAO =
        "Erro ao adicionar permissão de técnico indicador para os usuários do agente autorizado {}";
    private static final String ERRO_REMOVER_PERMISSAO =
        "Erro ao remover permissão de técnico indicador dos usuários do agente autorizado {}";

    @Autowired
    private PermissaoEspecialService permissaoEspecialService;
    @Autowired
    private UsuarioService usuarioService;

    @RabbitListener(queues = "${app-config.queue.atualizar-permissao-tecnico-indicador}")
    public void atualizarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        if (dto.getIsAdicionarPermissao() == Eboolean.V) {
            adicionarPermissaoTecnicoIndicador(dto);
        } else {
            removerPermissaoTecnicoIndicador(dto);
        }
    }

    public void adicionarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        log.info(INFO_ADICIONAR_PERMISSAO, dto.getAgenteAutorizadoId());

        try {
            var permissoes = usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(dto.getUsuariosIds())
                .stream()
                .filter(usuario -> !validarUsuarioComPermissaoTecnicoIndicador(usuario.getId()))
                .map(usuario -> PermissaoEspecial.of(
                    usuario.getId(), PERMISSAO_TECNICO_INDICADOR_ID, dto.getUsuarioAutenticadoId()))
                .collect(Collectors.toList());

            if (!isEmpty(permissoes)) {
                permissaoEspecialService.save(permissoes);
            }
        } catch (Exception ex) {
            log.error(ERRO_ADICIONAR_PERMISSAO, dto.getAgenteAutorizadoId(), ex);
        }
    }

    public void removerPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        log.info(INFO_REMOVER_PERMISSAO, dto.getAgenteAutorizadoId());

        try {
            usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(dto.getUsuariosIds())
                .stream()
                .filter(usuario -> validarUsuarioComPermissaoTecnicoIndicador(usuario.getId()))
                .forEach(usuario -> permissaoEspecialService.remover(
                    usuario.getId(), PERMISSAO_TECNICO_INDICADOR_ID, dto.getUsuarioAutenticadoId()));
        } catch (Exception ex) {
            log.error(ERRO_REMOVER_PERMISSAO, dto.getAgenteAutorizadoId(), ex);
        }
    }

    private boolean validarUsuarioComPermissaoTecnicoIndicador(Integer usuarioId) {
        return permissaoEspecialService.hasPermissaoEspecialAtiva(
            usuarioId, PERMISSAO_TECNICO_INDICADOR_ID);
    }
}
