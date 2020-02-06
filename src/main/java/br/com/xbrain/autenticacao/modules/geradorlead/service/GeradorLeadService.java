package br.com.xbrain.autenticacao.modules.geradorlead.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.geradorlead.dto.AgenteAutorizadoGeradorLeadDto;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.geradorlead.service.GeradorLeadUtil.*;

@Service
public class GeradorLeadService {

    @Autowired
    private PermissaoEspecialRepository permissaoEspecialRepository;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;

    @Transactional
    public void atualizarPermissaoGeradorLead(AgenteAutorizadoGeradorLeadDto agenteAutorizadoGeradorLeadDto) {
        if (agenteAutorizadoGeradorLeadDto.isGeradorLead()) {
            var permissoes = getNovasPermissoesEspeciais(agenteAutorizadoGeradorLeadDto);
            salvarPermissoesEspeciais(permissoes);
            gerarUsuarioHistorico(getUsuariosIds(permissoes), true);
        } else if (!agenteAutorizadoGeradorLeadDto.isGeradorLead()) {
            var usuariosIds = agenteAutorizadoGeradorLeadDto.getColaboradoresVendasIds();
            usuariosIds.add(agenteAutorizadoGeradorLeadDto.getUsuarioProprietarioId());

            removerPermissoesEspeciais(usuariosIds);
            gerarUsuarioHistorico(usuariosIds, false);
        }
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
        permissaoEspecialRepository.deletarPermissaoEspecialBy(
            List.of(FUNCIONALIDADE_GERENCIAR_LEAD_ID, FUNCIONALIDADE_TRATAR_LEAD_ID),
            usuarios);
    }

    private List<PermissaoEspecial> getNovasPermissoesEspeciais(AgenteAutorizadoGeradorLeadDto agenteAutorizadoGeradorLeadDto) {
        var permissoesEspeciais = getPermissoesEspeciaisDosVendedores(agenteAutorizadoGeradorLeadDto.getColaboradoresVendasIds());
        getPermissaoEspecialSocioPrincipal(agenteAutorizadoGeradorLeadDto.getUsuarioProprietarioId())
            .ifPresent(permissoesEspeciais::add);
        return permissoesEspeciais;
    }

    private List<PermissaoEspecial> getPermissoesEspeciaisDosVendedores(List<Integer> vendedoresIds) {
        return vendedoresIds.stream()
            .filter(vendedorId ->
                permissaoEspecialRepository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(
                    vendedorId, FUNCIONALIDADE_TRATAR_LEAD_ID).isEmpty())
            .map(vendedorId -> criarPermissaoEspecial(vendedorId, FUNCIONALIDADE_TRATAR_LEAD_ID))
            .collect(Collectors.toList());
    }

    private Optional<PermissaoEspecial> getPermissaoEspecialSocioPrincipal(Integer socioPrincipalId) {
        return permissaoEspecialRepository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(
            socioPrincipalId, FUNCIONALIDADE_GERENCIAR_LEAD_ID).isEmpty()
            ? Optional.of(criarPermissaoEspecial(socioPrincipalId, FUNCIONALIDADE_GERENCIAR_LEAD_ID))
            : Optional.empty();
    }

    private PermissaoEspecial criarPermissaoEspecial(Integer usuarioId, Integer funcionalidadeId) {
        return PermissaoEspecial.builder()
            .funcionalidade(new Funcionalidade(funcionalidadeId))
            .usuarioCadastro(new Usuario(usuarioId))
            .usuario(new Usuario(usuarioId))
            .dataCadastro(LocalDateTime.now())
            .build();
    }
}
