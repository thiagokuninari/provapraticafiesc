package br.com.xbrain.autenticacao.modules.geradorlead.service;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.geradorlead.dto.AgenteAutorizadoGeradorLeadDto;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.geradorlead.service.GeradorLeadUtil.FUNCIONALIDADE_GERENCIAR_LEAD_ID;

@Service
public class GeradorLeadService {

    @Autowired
    private PermissaoEspecialRepository permissaoEspecialRepository;

    @Transactional
    public void atualizarPermissaoGeradorLead(AgenteAutorizadoGeradorLeadDto agenteAutorizadoGeradorLeadDto) {
        if (Objects.equals(agenteAutorizadoGeradorLeadDto.getGeradorLead(), Eboolean.V)) {
            var usuariosParaSalvarNovaPermissaoEspecial = getNovasPermissoesEspeciais(agenteAutorizadoGeradorLeadDto);

            if (!ObjectUtils.isEmpty(usuariosParaSalvarNovaPermissaoEspecial)) {
                permissaoEspecialRepository.save(usuariosParaSalvarNovaPermissaoEspecial);
            }
        } else {
            var usuarios = agenteAutorizadoGeradorLeadDto.getColaboradoresVendasIds();
            usuarios.add(agenteAutorizadoGeradorLeadDto.getUsuarioProprietarioId());
            permissaoEspecialRepository.deletarPermissaoEspecialBy(
                List.of(FUNCIONALIDADE_GERENCIAR_LEAD_ID, GeradorLeadUtil.FUNCIONALIDADE_TRATAR_LEAD_ID),
                usuarios);
        }
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
                    vendedorId, GeradorLeadUtil.FUNCIONALIDADE_TRATAR_LEAD_ID).isEmpty())
            .map(vendedorId -> criarPermissaoEspecial(vendedorId, GeradorLeadUtil.FUNCIONALIDADE_TRATAR_LEAD_ID))
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
