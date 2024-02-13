package br.com.xbrain.autenticacao.modules.agenteautorizado.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.transaction.Transactional;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static org.springframework.util.ObjectUtils.isEmpty;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissaoTecnicoIndicadorService {

    private static final Integer PERMISSAO_TECNICO_INDICADOR = 253;
    private static final List<CodigoCargo> LISTA_CARGOS_TECNICO_INDICADOR = List.of(
        AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS, AGENTE_AUTORIZADO_SOCIO_SECUNDARIO, AGENTE_AUTORIZADO_GERENTE_RECEPTIVO,
        AGENTE_AUTORIZADO_GERENTE, AGENTE_AUTORIZADO_VENDEDOR_HIBRIDO, AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS_RECEPTIVO,
        AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS_RECEPTIVO, AGENTE_AUTORIZADO_SOCIO, AGENTE_AUTORIZADO_COORDENADOR,
        AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS_RECEPTIVO, AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS,
        AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS);

    private final PermissaoEspecialService permissaoEspecialService;
    private final UsuarioRepository usuarioRepository;
    private final CargoRepository cargoRepository;

    @Transactional
    public void atualizarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        if (dto.getIsAdicionarPermissao().equals(Eboolean.V)) {
            adicionarPermissaoTecnicoIndicador(dto);
        } else {
            removerPermissaoTecnicoIndicador(dto);
        }
    }

    public void adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(UsuarioDto usuarioDto,
                                                                  UsuarioMqRequest usuarioMqRequest,
                                                                  boolean isRemanejamento) {
        if (usuarioMqRequest.isTecnicoIndicador()
            && LISTA_CARGOS_TECNICO_INDICADOR.contains(usuarioMqRequest.getCargo())
            && (isRemanejamento || usuarioMqRequest.isNovoCadastro()
            || !validarUsuarioComPermissaoTecnicoIndicador(usuarioDto.getId()))) {
            log.info("Adicionando permissão de Técnico Indicador para usuário novo com id {}.", usuarioDto.getId());
            var permissoes = List.of(PermissaoEspecial.of(
                usuarioDto.getId(), PERMISSAO_TECNICO_INDICADOR, usuarioDto.getUsuarioCadastroId()));
            salvarPermissoesEspeciais(permissoes);
            log.info("Permissões adicionadas com sucesso.");
        }
    }

    @Transactional
    public void removerPermissaoTecnicoIndicadorDoUsuario(UsuarioDto usuarioDto) {
        if (usuarioDto.getId() != null
            && validarUsuarioComPermissaoTecnicoIndicador(usuarioDto.getId())
            && (usuarioDto.getSituacao() == ESituacao.R
            || !LISTA_CARGOS_TECNICO_INDICADOR.contains(usuarioDto.getCargoCodigo()))) {
            log.info("Removendo permissão de Técnico Indicador do usuário.");
            removerPermissaoDosUsuarios(List.of(usuarioDto.getId()));
        }
    }

    public void adicionarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        log.info("Adicionando permissão de técnico indicador aos usuários do agente autorizado {}",
            dto.getAgenteAutorizadoId());

        var permissoes = buscarUsuariosTabulacaoTecnicoIndicador(dto.getUsuariosIds())
            .stream()
            .filter(usuario -> !validarUsuarioComPermissaoTecnicoIndicador(usuario.getId()))
            .map(usuario -> PermissaoEspecial.of(
                usuario.getId(), PERMISSAO_TECNICO_INDICADOR, dto.getUsuarioAutenticadoId()))
            .collect(toList());

        salvarPermissoesEspeciais(permissoes);
    }

    public void removerPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        log.info("Removendo permissão de técnico indicador dos usuários do agente autorizado {}",
            dto.getAgenteAutorizadoId());

        var usuarios = buscarUsuariosTabulacaoTecnicoIndicador(dto.getUsuariosIds())
            .stream()
            .filter(usuario -> validarUsuarioComPermissaoTecnicoIndicador(usuario.getId()))
            .collect(toList());

        removerPermissaoDosUsuarios(usuarios.stream().map(Usuario::getId).collect(toList()));
    }

    public List<Usuario> buscarUsuariosTabulacaoTecnicoIndicador(List<Integer> usuarioIds) {
        return usuarioRepository.findByIdInAndCargoInAndSituacaoNot(
            usuarioIds,
            cargoRepository.findByCodigoIn(LISTA_CARGOS_TECNICO_INDICADOR),
            ESituacao.R);
    }

    public boolean validarUsuarioComPermissaoTecnicoIndicador(Integer usuarioId) {
        return permissaoEspecialService.hasPermissaoEspecialAtiva(
            usuarioId, PERMISSAO_TECNICO_INDICADOR);
    }

    private void salvarPermissoesEspeciais(List<PermissaoEspecial> permissoesEspeciais) {
        if (!isEmpty(permissoesEspeciais)) {
            permissaoEspecialService.save(permissoesEspeciais);
        }
    }

    private void removerPermissaoDosUsuarios(List<Integer> usuariosIds) {
        if (!isEmpty(usuariosIds)) {
            permissaoEspecialService.deletarPermissoesEspeciaisBy(
                List.of(PERMISSAO_TECNICO_INDICADOR), usuariosIds);
        }
    }
}
