package br.com.xbrain.autenticacao.modules.feeder.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeeder;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.feeder.dto.AgenteAutorizadoPermissaoFeederDto;
import br.com.xbrain.autenticacao.modules.feeder.dto.SituacaoAlteracaoUsuarioFeederDto;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.EMPRESARIAL;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.RESIDENCIAL;
import static br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil.*;

@Service
public class FeederService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Usuario não encontrado.");
    private static final ValidacaoException EX_USUARIO_NAO_FEEDER =
        new ValidacaoException("Usuário não Feeder.");
    private static final String EMAIL_INATIVO = "INATIVO_";

    @Autowired
    private PermissaoEspecialRepository permissaoEspecialRepository;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DataHoraAtual dataHoraAtual;
    @Autowired
    private UsuarioService usuarioService;

    @Transactional
    public void atualizarPermissaoFeeder(AgenteAutorizadoPermissaoFeederDto agenteAutorizadoPermissaoFeederDto) {
        if (agenteAutorizadoPermissaoFeederDto.hasPermissaoFeeder()) {
            var permissoes = getNovasPermissoesEspeciais(agenteAutorizadoPermissaoFeederDto);
            usuarioService.salvarPermissoesEspeciais(permissoes);
            gerarUsuarioHistorico(getUsuariosIds(permissoes), true);

            if (!agenteAutorizadoPermissaoFeederDto.hasPermissaoFeederResidencial()) {
                var usuariosIds = agenteAutorizadoPermissaoFeederDto.getColaboradoresVendasIds().stream()
                    .filter(usuarioId -> usuarioRepository.exists(usuarioId))
                    .collect(Collectors.toList());

                removerPermissoesEspeciais(usuariosIds, FUNCIONALIDADES_FEEDER_PARA_COLABORADORES_AA_RESIDENCIAL);
                gerarUsuarioHistorico(usuariosIds, true);
            }
        } else if (!agenteAutorizadoPermissaoFeederDto.hasPermissaoFeeder()) {
            var usuariosIds = agenteAutorizadoPermissaoFeederDto.getColaboradoresVendasIds().stream()
                .filter(usuarioId -> usuarioRepository.exists(usuarioId))
                .collect(Collectors.toList());
            if (!agenteAutorizadoPermissaoFeederDto.isSocioDeOutroAaComPermissaoFeeder()) {
                usuariosIds.add(agenteAutorizadoPermissaoFeederDto.getUsuarioProprietarioId());
            }

            var funcionalidades = new ArrayList<>(FUNCIONALIDADES_FEEDER_PARA_AA);
            funcionalidades.addAll(FUNCIONALIDADES_FEEDER_PARA_COLABORADORES_AA_RESIDENCIAL);

            removerPermissoesEspeciais(usuariosIds, funcionalidades);
            gerarUsuarioHistorico(usuariosIds, false);
        }
    }

    @Transactional
    public void alterarSituacaoUsuarioFeeder(SituacaoAlteracaoUsuarioFeederDto dto) {
        var usuario = findUsuarioById(dto.getUsuarioId());
        validarSeUsuarioFeeder(usuario);
        usuario.setSituacao(dto.getSituacaoAlterada());
        gerarHistorico(usuario, dto);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void limparCpfEAlterarEmailUsuarioFeeder(Integer usuarioId) {
        var usuario = findUsuarioById(usuarioId);
        usuario.setCpf(null);
        usuario.setEmail(EMAIL_INATIVO.concat(usuario.getEmail()));
        gerarHistoricoUsuarioExcluidoFeeder(usuarioId);

        usuarioRepository.save(usuario);
    }

    public void adicionarPermissaoFeederParaUsuarioNovo(UsuarioDto usuario, UsuarioMqRequest usuarioMqRequest) {
        if (CodigoCargo.ASSISTENTE_RELACIONAMENTO != usuarioMqRequest.getCargo()
            && (usuarioMqRequest.getAgenteAutorizadoFeeder() == ETipoFeeder.RESIDENCIAL
            || usuarioMqRequest.getAgenteAutorizadoFeeder() == ETipoFeeder.EMPRESARIAL)) {
            var permissoesFeeder = usuarioRepository.findById(usuario.getId())
                .map(usuarioNovo -> getPermissoesEspeciaisDoColobarodaorConformeCargo(usuarioNovo,
                    usuarioMqRequest.getAgenteAutorizadoFeeder(), usuarioMqRequest.getUsuarioCadastroId(),
                    usuarioMqRequest.getCargo()))
                .orElse(List.of());
            usuarioService.salvarPermissoesEspeciais(permissoesFeeder);
        }
    }

    public void adicionarPermissaoFeederParaUsuarioNovoMso(Usuario usuario) {
        var permissoesTiposFeeder = usuarioRepository.findById(usuario.getId())
            .map(usuarioNovo -> usuarioService.getPermissoesEspeciaisDoUsuario(usuario.getId(),
                usuario.getUsuarioCadastro().getId(), getPermissoesTiposFeederMso(usuario)))
            .orElse(List.of());

        usuarioService.salvarPermissoesEspeciais(permissoesTiposFeeder);
    }

    private List<Integer> getPermissoesTiposFeederMso(Usuario usuario) {
        var listaFuncionalidades = new ArrayList<Integer>();

        if (Objects.nonNull(usuario.getTiposFeeder())) {
            if (usuario.getTiposFeeder().contains(RESIDENCIAL)) {
                listaFuncionalidades.addAll(FUNCIONALIDADES_FEEDER_PARA_MSO_RESIDENCIAL);
            }
            if (usuario.getTiposFeeder().contains(EMPRESARIAL)) {
                listaFuncionalidades.addAll(FUNCIONALIDADES_FEEDER_PARA_MSO_EMPRESARIAL);
            }
        }
        return listaFuncionalidades.stream().distinct().collect(Collectors.toList());
    }

    public void removerPermissaoFeederUsuarioAtualizadoMso(Usuario usuario) {
        if (!usuario.isNovoCadastro() && usuario.isIdNivelMso()) {
            removerPermissaoFeederMsoEmpresarial(usuario);
            removerPermissaoFeederMsoResidencial(usuario);
        }
    }

    private void removerPermissaoFeederMsoResidencial(Usuario usuario) {
        permissaoEspecialRepository.deletarPermissaoEspecialBy(FUNCIONALIDADES_FEEDER_PARA_MSO_RESIDENCIAL,
            List.of(usuario.getId()));
    }

    private void removerPermissaoFeederMsoEmpresarial(Usuario usuario) {
        permissaoEspecialRepository.deletarPermissaoEspecialBy(FUNCIONALIDADES_FEEDER_PARA_MSO_EMPRESARIAL,
            List.of(usuario.getId()));
    }

    private void gerarHistorico(Usuario usuario, SituacaoAlteracaoUsuarioFeederDto dto) {
        usuarioHistoricoService.save(
            UsuarioHistorico.builder()
                .usuario(usuario)
                .dataCadastro(dto.getDataAlteracao())
                .situacao(dto.getSituacaoAlterada())
                .usuarioAlteracao(new Usuario(dto.getUsuarioAlteracaoId()))
                .observacao(dto.getObservacao())
                .build());
    }

    private void validarSeUsuarioFeeder(Usuario usuario) {
        if (!usuario.isCargo(CodigoCargo.GERADOR_LEADS)
            && !usuario.isCargo(CodigoCargo.IMPORTADOR_CARGAS)) {
            throw EX_USUARIO_NAO_FEEDER;
        }
    }

    private Usuario findUsuarioById(Integer usuarioId) {
        return usuarioRepository.findComplete(usuarioId)
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    private void gerarUsuarioHistorico(List<Integer> usuariosIds, boolean hasPermissaoFeeder) {
        if (!ObjectUtils.isEmpty(usuariosIds)) {
            usuarioHistoricoService.save(
                UsuarioHistorico.gerarHistorico(usuariosIds, hasPermissaoFeeder
                        ? OBSERVACAO_FEEDER
                        : OBSERVACAO_NAO_FEEDER,
                    ESituacao.A));
        }
    }

    private void gerarHistoricoUsuarioExcluidoFeeder(Integer usuarioId) {
        if (Objects.nonNull(usuarioId)) {
            usuarioHistoricoService.save(
                UsuarioHistorico.gerarHistorico(usuarioId, null, ALTERACAO_CPF_E_EMAIL_FEEDER, ESituacao.I));
        }
    }

    private List<Integer> getUsuariosIds(List<PermissaoEspecial> permissoes) {
        return permissoes.stream()
            .map(PermissaoEspecial::getUsuario)
            .map(Usuario::getId)
            .collect(Collectors.toList());
    }

    public void removerPermissoesEspeciais(List<Integer> usuarios, List<Integer> funcionalidades) {
        permissaoEspecialRepository.deletarPermissaoEspecialBy(funcionalidades, usuarios);
    }

    private List<PermissaoEspecial> getNovasPermissoesEspeciais(AgenteAutorizadoPermissaoFeederDto aaPermissaoFeederDto) {
        var permissoesEspeciais = getPermissoesEspeciaisDosColaboradores(aaPermissaoFeederDto.getColaboradoresVendasIds(),
            aaPermissaoFeederDto.getUsuarioCadastroId(), aaPermissaoFeederDto.getFeeder());

        permissoesEspeciais.addAll(getPermissaoEspecialSocioPrincipal(
            aaPermissaoFeederDto.getUsuarioProprietarioId(),
            aaPermissaoFeederDto.getUsuarioCadastroId(), aaPermissaoFeederDto.getFeeder()));

        return permissoesEspeciais;
    }

    private List<PermissaoEspecial> getPermissoesEspeciaisDosColaboradores(List<Integer> vendedoresIds,
                                                                           Integer usuarioCadastroId, ETipoFeeder tipoFeeder) {
        return vendedoresIds.stream()
            .map(colaboradorId -> usuarioRepository.findComplete(colaboradorId).orElse(null))
            .filter(Objects::nonNull)
            .filter(usuario -> !usuario.getSituacao().equals(ESituacao.R))
            .filter(usuario -> CodigoCargo.ASSISTENTE_RELACIONAMENTO != usuario.getCargoCodigo())
            .flatMap(colaborador -> getPermissoesEspeciaisDoColobarodaorConformeCargo(colaborador, tipoFeeder,
                usuarioCadastroId, colaborador.getCargoCodigo()).stream())
            .collect(Collectors.toList());
    }

    private List<PermissaoEspecial> getPermissoesEspeciaisDoColobarodaorConformeCargo(Usuario colaborador,
                                                                                      ETipoFeeder tipoFeeder,
                                                                                      Integer usuarioCadastroId,
                                                                                      CodigoCargo cargoCodigo) {
        if (isBackOffice(cargoCodigo)) {
            return usuarioService.getPermissoesEspeciaisDoUsuario(colaborador.getId(), usuarioCadastroId,
                FUNCIONALIDADES_FEEDER_PARA_AA);
        }
        if (isColaboradoresAaFeederResidencial(cargoCodigo, tipoFeeder)) {
            return usuarioService.getPermissoesEspeciaisDoUsuario(colaborador.getId(), usuarioCadastroId,
                FUNCIONALIDADES_FEEDER_PARA_COLABORADORES_AA_RESIDENCIAL);
        }
        return usuarioService.getPermissoesEspeciaisDoUsuario(colaborador.getId(), usuarioCadastroId,
            List.of(FUNCIONALIDADE_TRATAR_LEAD_ID));
    }

    private List<PermissaoEspecial> getPermissaoEspecialSocioPrincipal(Integer socioPrincipalId,
                                                                       Integer usuarioCadastroId,
                                                                       ETipoFeeder tipoFeeder) {
        var funcionalidades = new ArrayList<>(FUNCIONALIDADES_FEEDER_PARA_AA);

        if (ETipoFeeder.RESIDENCIAL == tipoFeeder) {
            funcionalidades.addAll(FUNCIONALIDADES_FEEDER_PARA_COLABORADORES_AA_RESIDENCIAL);
        }

        return usuarioService.getPermissoesEspeciaisDoUsuario(socioPrincipalId, usuarioCadastroId,
            funcionalidades);
    }

    private boolean isBackOffice(CodigoCargo codigoCargo) {
        return Objects.nonNull(codigoCargo) && CARGOS_BACKOFFICE.contains(codigoCargo);
    }

    private boolean isColaboradoresAaFeederResidencial(CodigoCargo usuarioCargoCodigo, ETipoFeeder tipoFeeder) {
        return tipoFeeder == ETipoFeeder.RESIDENCIAL && Objects.nonNull(usuarioCargoCodigo)
            && CODIGOS_CARGOS_COLABORADORES_FEEDER_RESIDENCIAL.contains(usuarioCargoCodigo);
    }

    public void salvarPermissoesEspeciaisCoordenadoresGerentes(List<Integer> usuariosIds, int usuarioLogado) {
        var localDateTime = dataHoraAtual.getDataHora();
        usuariosIds.forEach(usuarioId -> {
                var listaFunc = permissaoEspecialRepository.findByUsuario(usuarioId);
                if (usuarioRepository.exists(usuarioId)) {
                    permissaoEspecialRepository.save(
                        FUNCIONALIDADES_FEEDER_PARA_REPROCESSAR_COORD_GER
                            .stream()
                            .filter(func -> !listaFunc.contains(func))
                            .map(id -> PermissaoEspecial
                                .builder()
                                .funcionalidade(Funcionalidade.builder().id(id).build())
                                .usuario(new Usuario(usuarioId))
                                .dataCadastro(localDateTime)
                                .usuarioCadastro(Usuario.builder().id(usuarioLogado).build())
                                .build())
                            .collect(Collectors.toList()));
                }
            }
        );
    }
}
