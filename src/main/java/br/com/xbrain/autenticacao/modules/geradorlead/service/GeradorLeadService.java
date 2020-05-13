package br.com.xbrain.autenticacao.modules.geradorlead.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.geradorlead.dto.AgenteAutorizadoGeradorLeadDto;
import br.com.xbrain.autenticacao.modules.geradorlead.dto.SituacaoAlteracaoGeradorLeadsDto;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
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
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.geradorlead.service.GeradorLeadUtil.*;

@Service
public class GeradorLeadService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Usuario não encontrado.");
    private static final ValidacaoException EX_USUARIO_NAO_GERADOR_LEADS =
        new ValidacaoException("Usuário não Gerador de Leads.");

    @Autowired
    private PermissaoEspecialRepository permissaoEspecialRepository;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public void atualizarPermissaoGeradorLead(AgenteAutorizadoGeradorLeadDto agenteAutorizadoGeradorLeadDto) {
        if (agenteAutorizadoGeradorLeadDto.isGeradorLead()) {
            var permissoes = getNovasPermissoesEspeciais(agenteAutorizadoGeradorLeadDto);
            salvarPermissoesEspeciais(permissoes);
            gerarUsuarioHistorico(getUsuariosIds(permissoes), true);
        } else if (!agenteAutorizadoGeradorLeadDto.isGeradorLead()) {
            var usuariosIds = agenteAutorizadoGeradorLeadDto.getColaboradoresVendasIds().stream()
                .filter(usuarioId -> usuarioRepository.exists(usuarioId))
                .collect(Collectors.toList());
            usuariosIds.add(agenteAutorizadoGeradorLeadDto.getUsuarioProprietarioId());

            removerPermissoesEspeciais(usuariosIds);
            gerarUsuarioHistorico(usuariosIds, false);
        }
    }

    @Transactional
    public void alterarSituacaoGeradorLeads(SituacaoAlteracaoGeradorLeadsDto dto) {
        var usuario = findUsuarioById(dto.getUsuarioId());
        validarSeUsuarioGeradorLeads(usuario);
        usuario.setSituacao(dto.getSituacaoAlterada());
        gerarHistorico(usuario, dto);
        usuarioRepository.save(usuario);
    }

    private void gerarHistorico(Usuario usuario, SituacaoAlteracaoGeradorLeadsDto dto) {
        usuarioHistoricoService.save(
            UsuarioHistorico.builder()
                .usuario(usuario)
                .dataCadastro(dto.getDataAlteracao())
                .situacao(dto.getSituacaoAlterada())
                .usuarioAlteracao(new Usuario(dto.getUsuarioAlteracaoId()))
                .observacao(dto.getObservacao())
                .build());
    }

    private void validarSeUsuarioGeradorLeads(Usuario usuario) {
        if (!usuario.isCargo(CodigoCargo.GERADOR_LEADS)) {
            throw EX_USUARIO_NAO_GERADOR_LEADS;
        }
    }

    private Usuario findUsuarioById(Integer usuarioId) {
        return usuarioRepository.findComplete(usuarioId)
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    private void gerarUsuarioHistorico(List<Integer> usuariosIds, boolean geradorLeads) {
        if (!ObjectUtils.isEmpty(usuariosIds)) {
            usuarioHistoricoService.save(
                UsuarioHistorico.gerarHistorico(usuariosIds, geradorLeads
                    ? OBSERVACAO_GERADOR_LEADS
                    : OBSERVACAO_NAO_GERADOR_LEADS,
                    ESituacao.A));
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

    private void removerPermissoesEspeciais(List<Integer> usuarios) {
        permissaoEspecialRepository.deletarPermissaoEspecialBy(FUNCIONALIDADES_GERADOR_LEADS_PARA_AA, usuarios);
    }

    private List<PermissaoEspecial> getNovasPermissoesEspeciais(AgenteAutorizadoGeradorLeadDto agenteAutorizadoGeradorLeadDto) {
        var permissoesEspeciais = getPermissoesEspeciaisDosVendedores(agenteAutorizadoGeradorLeadDto.getColaboradoresVendasIds(),
                                                                    agenteAutorizadoGeradorLeadDto.getUsuarioCadastroId());
        permissoesEspeciais.addAll(getPermissaoEspecialSocioPrincipal(
            agenteAutorizadoGeradorLeadDto.getUsuarioProprietarioId(),
            agenteAutorizadoGeradorLeadDto.getUsuarioCadastroId()));
        return permissoesEspeciais;
    }

    private List<PermissaoEspecial> getPermissoesEspeciaisDosVendedores(List<Integer> vendedoresIds,
                                                                        Integer usuarioCadastroId) {
        return vendedoresIds.stream()
            .filter(vendedorId -> usuarioRepository.findById(vendedorId)
                .map(usuario -> !usuario.getSituacao().equals(ESituacao.R))
                .orElse(false))
            .filter(vendedorId ->
                permissaoEspecialRepository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(
                    vendedorId, FUNCIONALIDADE_TRATAR_LEAD_ID).isEmpty())
            .map(vendedorId -> criarPermissaoEspecial(vendedorId, FUNCIONALIDADE_TRATAR_LEAD_ID, usuarioCadastroId))
            .collect(Collectors.toList());
    }

    private List<PermissaoEspecial> getPermissaoEspecialSocioPrincipal(Integer socioPrincipalId, Integer usuarioCadastroId) {
        return FUNCIONALIDADES_GERADOR_LEADS_PARA_AA.stream()
            .filter(funcionalidadeId -> permissaoEspecialRepository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(
                socioPrincipalId, FUNCIONALIDADE_GERENCIAR_LEAD_ID).isEmpty())
            .map(funcionalidadeId -> criarPermissaoEspecial(socioPrincipalId, funcionalidadeId, usuarioCadastroId))
            .collect(Collectors.toList());
    }

    private PermissaoEspecial criarPermissaoEspecial(Integer usuarioId, Integer funcionalidadeId, Integer usuarioCadastroId) {
        return PermissaoEspecial.builder()
            .funcionalidade(new Funcionalidade(funcionalidadeId))
            .usuarioCadastro(new Usuario(usuarioCadastroId))
            .usuario(new Usuario(usuarioId))
            .dataCadastro(LocalDateTime.now())
            .build();
    }
}
