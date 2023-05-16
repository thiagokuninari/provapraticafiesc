package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissaoTecnicoIndicadorService {

    private static final Integer PERMISSAO_TECNICO_INDICADOR = 253;

    private final PermissaoEspecialService permissaoEspecialService;
    private final UsuarioService usuarioService;

    public void atualizarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        if (dto.getIsAdicionarPermissao().equals(Eboolean.V)) {
            adicionarPermissaoTecnicoIndicador(dto);
        } else {
            removerPermissaoTecnicoIndicador(dto);
        }
    }

    public void adicionarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        log.info("Adicionando permissão de técnico indicador aos usuários do agente autorizado {}",
            dto.getAgenteAutorizadoId());

        var permissoes = usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(dto.getUsuariosIds())
            .stream()
            .filter(usuario -> !validarUsuarioComPermissaoTecnicoIndicador(usuario.getId()))
            .map(usuario -> PermissaoEspecial.of(
                usuario.getId(), PERMISSAO_TECNICO_INDICADOR, dto.getUsuarioAutenticadoId()))
            .collect(toList());

        if (!isEmpty(permissoes)) {
            permissaoEspecialService.save(permissoes);
        }
    }

    public void removerPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        log.info("Removendo permissão de técnico indicador dos usuários do agente autorizado {}",
            dto.getAgenteAutorizadoId());

        var usuarios = usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(dto.getUsuariosIds())
            .stream()
            .filter(usuario -> validarUsuarioComPermissaoTecnicoIndicador(usuario.getId()))
            .collect(toList());
        
        if (!isEmpty(usuarios)) {
            permissaoEspecialService.deletarPermissoesEspeciaisBy(
                List.of(PERMISSAO_TECNICO_INDICADOR),
                usuarios.stream().map(Usuario::getId).collect(toList()));
        }
    }

    public boolean validarUsuarioComPermissaoTecnicoIndicador(Integer usuarioId) {
        return permissaoEspecialService.hasPermissaoEspecialAtiva(
            usuarioId, PERMISSAO_TECNICO_INDICADOR);
    }
}
