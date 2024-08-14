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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.LISTA_CARGOS_SUPERIORES_AGENTE_AUTORIZADO;
import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.PERMISSAO_DESBLOQUEAR_INDICACAO_EXTERNA_ID;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("PMD.TooManyStaticImports")
public class PermissaoTecnicoIndicadorService {

    private static final Integer PERMISSAO_TRABALHAR_INDICACAO = 253;
    private static final List<Integer> PERMISSOES_TECNICO_INDICADOR = List.of(22122, 22257);
    private static final List<CodigoCargo> LISTA_CARGOS_TECNICO_INDICADOR = List.of(
        AGENTE_AUTORIZADO_ACEITE,
        AGENTE_AUTORIZADO_APRENDIZ,
        AGENTE_AUTORIZADO_ASSISTENTE,
        AGENTE_AUTORIZADO_BACKOFFICE_D2D,
        AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS,
        AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS_RECEPTIVO,
        AGENTE_AUTORIZADO_BACKOFFICE_TEMP,
        AGENTE_AUTORIZADO_COORDENADOR,
        AGENTE_AUTORIZADO_EMPRESARIO,
        AGENTE_AUTORIZADO_GERENTE,
        AGENTE_AUTORIZADO_GERENTE_RECEPTIVO,
        AGENTE_AUTORIZADO_GERENTE_TEMP,
        AGENTE_AUTORIZADO_SOCIO,
        AGENTE_AUTORIZADO_SOCIO_SECUNDARIO,
        AGENTE_AUTORIZADO_SUPERVISOR,
        AGENTE_AUTORIZADO_SUPERVISOR_RECEPTIVO,
        AGENTE_AUTORIZADO_SUPERVISOR_TEMP,
        AGENTE_AUTORIZADO_TECNICO_COORDENADOR,
        AGENTE_AUTORIZADO_TECNICO_GERENTE,
        AGENTE_AUTORIZADO_TECNICO_SEGMENTADO,
        AGENTE_AUTORIZADO_TECNICO_SUPERVISOR,
        AGENTE_AUTORIZADO_TECNICO_VENDEDOR,
        AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D,
        AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS,
        AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS_RECEPTIVO,
        AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TEMP,
        AGENTE_AUTORIZADO_VENDEDOR_D2D,
        AGENTE_AUTORIZADO_VENDEDOR_HIBRIDO,
        AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS,
        AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS_RECEPTIVO,
        AGENTE_AUTORIZADO_VENDEDOR_TEMP);

    private static final List<CodigoCargo> LISTA_CARGOS_TRABALHAR_TECNICO_INDICADOR = List.of(
        AGENTE_AUTORIZADO_ACEITE,
        AGENTE_AUTORIZADO_APRENDIZ,
        AGENTE_AUTORIZADO_ASSISTENTE,
        AGENTE_AUTORIZADO_BACKOFFICE_D2D,
        AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS,
        AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS_RECEPTIVO,
        AGENTE_AUTORIZADO_BACKOFFICE_TEMP,
        AGENTE_AUTORIZADO_COORDENADOR,
        AGENTE_AUTORIZADO_EMPRESARIO,
        AGENTE_AUTORIZADO_GERENTE,
        AGENTE_AUTORIZADO_GERENTE_RECEPTIVO,
        AGENTE_AUTORIZADO_GERENTE_TEMP,
        AGENTE_AUTORIZADO_SOCIO,
        AGENTE_AUTORIZADO_SOCIO_SECUNDARIO,
        AGENTE_AUTORIZADO_TECNICO_COORDENADOR,
        AGENTE_AUTORIZADO_TECNICO_GERENTE,
        AGENTE_AUTORIZADO_TECNICO_SEGMENTADO,
        AGENTE_AUTORIZADO_TECNICO_VENDEDOR,
        AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D,
        AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS,
        AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS_RECEPTIVO,
        AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TEMP,
        AGENTE_AUTORIZADO_VENDEDOR_D2D,
        AGENTE_AUTORIZADO_VENDEDOR_HIBRIDO,
        AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS,
        AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS_RECEPTIVO,
        AGENTE_AUTORIZADO_VENDEDOR_TEMP);

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
            var permissoes = getPermissoesTecnicoIndicador(usuarioDto.getId(),
                usuarioDto.getUsuarioCadastroId(), usuarioMqRequest.getCargo());
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

    private void adicionarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        log.info("Adicionando permissão de técnico indicador aos usuários do agente autorizado {}",
            dto.getAgenteAutorizadoId());

        var permissoes = buscarUsuariosTabulacaoTecnicoIndicador(dto.getUsuariosIds())
            .stream()
            .filter(usuario -> !validarUsuarioComPermissaoTecnicoIndicador(usuario.getId()))
            .map(usuario -> getPermissoesTecnicoIndicador(usuario.getId(), usuario.getUsuarioCadastro().getId(),
                usuario.getCargoCodigo()))
            .flatMap(List::stream)
            .collect(toList());

        salvarPermissoesEspeciais(permissoes);
    }

    private void removerPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
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

    private boolean validarUsuarioComPermissaoTecnicoIndicador(Integer usuarioId) {
        return permissaoEspecialService.hasPermissaoEspecialAtiva(usuarioId, getPermissoesEspeciaisTecnicoIndicadorIds());
    }

    private void salvarPermissoesEspeciais(List<PermissaoEspecial> permissoesEspeciais) {
        if (!isEmpty(permissoesEspeciais)) {
            permissaoEspecialService.save(permissoesEspeciais);
        }
    }

    private void removerPermissaoDosUsuarios(List<Integer> usuariosIds) {
        if (!isEmpty(usuariosIds)) {
            permissaoEspecialService.deletarPermissoesEspeciaisBy(getPermissoesEspeciaisTecnicoIndicadorIds(), usuariosIds);
        }
    }

    private List<PermissaoEspecial> getPermissoesTecnicoIndicador(Integer usuarioId,
                                                                  Integer usuarioCadastroId,
                                                                  CodigoCargo cargo) {
        var permissoes = PermissaoEspecial.of(usuarioId, PERMISSOES_TECNICO_INDICADOR, usuarioCadastroId);
        if (LISTA_CARGOS_SUPERIORES_AGENTE_AUTORIZADO.contains(cargo)) {
            permissoes.add(PermissaoEspecial.of(usuarioId, PERMISSAO_DESBLOQUEAR_INDICACAO_EXTERNA_ID, usuarioCadastroId));
        }
        if (LISTA_CARGOS_TRABALHAR_TECNICO_INDICADOR.contains(cargo)) {
            permissoes.add(PermissaoEspecial.of(usuarioId, PERMISSAO_TRABALHAR_INDICACAO, usuarioCadastroId));
        }
        return permissoes;
    }

    private List<Integer> getPermissoesEspeciaisTecnicoIndicadorIds() {
        var permissoes = new ArrayList<>(PERMISSOES_TECNICO_INDICADOR);
        permissoes.add(PERMISSAO_DESBLOQUEAR_INDICACAO_EXTERNA_ID);
        permissoes.add(PERMISSAO_TRABALHAR_INDICACAO);
        return permissoes;
    }
}
