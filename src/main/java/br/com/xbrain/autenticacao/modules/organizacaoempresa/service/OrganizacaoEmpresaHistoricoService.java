package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaHistoricoResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresaHistorico;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.OrganizacaoEmpresaHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizacaoEmpresaHistoricoService {

    @Autowired
    private OrganizacaoEmpresaHistoricoRepository historicoRepository;

    public OrganizacaoEmpresaHistorico salvarHistorico(OrganizacaoEmpresa organizacaoEmpresa, EHistoricoAcao observacao,
                                                       UsuarioAutenticado usuario) {
        var historico = OrganizacaoEmpresaHistorico.of(organizacaoEmpresa, observacao, usuario);
        return historicoRepository.save(historico);
    }

    public List<OrganizacaoEmpresaHistoricoResponse> obterHistoricoDaOrganizacaoEmpresa(Integer organizacaoEmpresaId) {
        return historicoRepository.findAllByOrganizacaoEmpresaIdOrderByDataAlteracaoDesc(organizacaoEmpresaId).stream()
            .map(OrganizacaoEmpresaHistoricoResponse::of)
            .collect(Collectors.toList());
    }
}
