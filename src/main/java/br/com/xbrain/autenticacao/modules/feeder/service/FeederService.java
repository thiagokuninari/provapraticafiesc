package br.com.xbrain.autenticacao.modules.feeder.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeeder;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Transactional
    public void atualizarPermissaoFeeder(AgenteAutorizadoPermissaoFeederDto agenteAutorizadoPermissaoFeederDto) {
        if (agenteAutorizadoPermissaoFeederDto.hasPermissaoFeeder()) {
            var permissoes = getNovasPermissoesEspeciais(agenteAutorizadoPermissaoFeederDto);
            salvarPermissoesEspeciais(permissoes);
            gerarUsuarioHistorico(getUsuariosIds(permissoes), true);
        } else if (!agenteAutorizadoPermissaoFeederDto.hasPermissaoFeeder()) {
            var usuariosIds = agenteAutorizadoPermissaoFeederDto.getColaboradoresVendasIds().stream()
                .filter(usuarioId -> usuarioRepository.exists(usuarioId))
                .collect(Collectors.toList());
            if (!agenteAutorizadoPermissaoFeederDto.isSocioDeOutroAaComPermissaoFeeder()) {
                usuariosIds.add(agenteAutorizadoPermissaoFeederDto.getUsuarioProprietarioId());
            }

            removerPermissoesEspeciais(usuariosIds);
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
        if (CodigoCargo.ASSISTENTE_LOJA_FUTURO.equals(usuarioMqRequest.getCargo())) {
            return;
        }
        if (usuarioMqRequest.getAgenteAutorizadoFeeder() == ETipoFeeder.RESIDENCIAL
            || usuarioMqRequest.getAgenteAutorizadoFeeder() == ETipoFeeder.EMPRESARIAL) {
            var permissoesFeeder = usuarioRepository.findById(usuario.getId())
                .map(usuarioNovo -> getPermissoesEspeciaisDoColobarodaorConformeCargo(usuarioNovo,
                    usuarioMqRequest.getUsuarioCadastroId(), usuarioMqRequest.getCargo()))
                .orElse(List.of());
            salvarPermissoesEspeciais(permissoesFeeder);
        }
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

    private void salvarPermissoesEspeciais(List<PermissaoEspecial> permissoesEspeciais) {
        if (!ObjectUtils.isEmpty(permissoesEspeciais)) {
            permissaoEspecialRepository.save(permissoesEspeciais);
        }
    }

    public void removerPermissoesEspeciais(List<Integer> usuarios) {
        permissaoEspecialRepository.deletarPermissaoEspecialBy(FUNCIONALIDADES_FEEDER_PARA_AA, usuarios);
    }

    private List<PermissaoEspecial> getNovasPermissoesEspeciais(AgenteAutorizadoPermissaoFeederDto aaPermissaoFeederDto) {
        var permissoesEspeciais = getPermissoesEspeciaisDosColaboradores(aaPermissaoFeederDto.getColaboradoresVendasIds(),
            aaPermissaoFeederDto.getUsuarioCadastroId());
        permissoesEspeciais.addAll(getPermissaoEspecialSocioPrincipal(
            aaPermissaoFeederDto.getUsuarioProprietarioId(),
            aaPermissaoFeederDto.getUsuarioCadastroId()));
        return permissoesEspeciais;
    }

    private List<PermissaoEspecial> getPermissoesEspeciaisDosColaboradores(List<Integer> vendedoresIds,
                                                                           Integer usuarioCadastroId) {
        return vendedoresIds.stream()
            .map(colaboradorId -> usuarioRepository.findComplete(colaboradorId).orElse(null))
            .filter(Objects::nonNull)
            .filter(usuario -> !usuario.getSituacao().equals(ESituacao.R))
            .filter(usuario -> !CodigoCargo.ASSISTENTE_LOJA_FUTURO.equals(usuario.getCargoCodigo()))
            .flatMap(colaborador -> getPermissoesEspeciaisDoColobarodaorConformeCargo(colaborador,
                usuarioCadastroId, colaborador.getCargoCodigo()).stream())
            .collect(Collectors.toList());
    }

    private List<PermissaoEspecial> getPermissoesEspeciaisDoColobarodaorConformeCargo(Usuario colaborador,
                                                                                      Integer usuarioCadastroId,
                                                                                      CodigoCargo cargoCodigo) {
        if (isBackOffice(cargoCodigo)) {
            return getPermissoesEspeciaisDoUsuario(colaborador.getId(), usuarioCadastroId, FUNCIONALIDADES_FEEDER_PARA_AA);
        }
        return getPermissoesEspeciaisDoUsuario(colaborador.getId(), usuarioCadastroId, List.of(FUNCIONALIDADE_TRATAR_LEAD_ID));
    }

    private List<PermissaoEspecial> getPermissoesEspeciaisDoUsuario(Integer usuarioId, Integer usuarioCadastroId,
                                                                    List<Integer> funcionalidadesIds) {
        return funcionalidadesIds.stream()
            .filter(funcionalidadeId -> permissaoEspecialRepository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(
                usuarioId, funcionalidadeId).isEmpty())
            .map(funcionalidadeId -> criarPermissaoEspecial(usuarioId, funcionalidadeId, usuarioCadastroId))
            .collect(Collectors.toList());
    }

    private List<PermissaoEspecial> getPermissaoEspecialSocioPrincipal(Integer socioPrincipalId, Integer usuarioCadastroId) {
        return getPermissoesEspeciaisDoUsuario(socioPrincipalId, usuarioCadastroId, FUNCIONALIDADES_FEEDER_PARA_AA);
    }

    private PermissaoEspecial criarPermissaoEspecial(Integer usuarioId, Integer funcionalidadeId, Integer usuarioCadastroId) {
        return PermissaoEspecial.builder()
            .funcionalidade(new Funcionalidade(funcionalidadeId))
            .usuarioCadastro(new Usuario(usuarioCadastroId))
            .usuario(new Usuario(usuarioId))
            .dataCadastro(LocalDateTime.now())
            .build();
    }

    private boolean isBackOffice(CodigoCargo codigoCargo) {
        return Objects.nonNull(codigoCargo) && CARGOS_BACKOFFICE.contains(codigoCargo);
    }
}
