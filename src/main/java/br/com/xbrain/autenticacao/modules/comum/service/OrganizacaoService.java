package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.filtros.OrganizacaoFiltros;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.repository.OrganizacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class OrganizacaoService {

    @Autowired
    private OrganizacaoRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<Organizacao> getAllSelect(Integer nivelId, OrganizacaoFiltros filtros) {
        return repository.findByPredicate(getFiltros(nivelId, filtros).toPredicate());
    }

    private OrganizacaoFiltros getFiltros(Integer nivelId, OrganizacaoFiltros filtros) {
        filtros = Objects.isNull(filtros) ? new OrganizacaoFiltros() : filtros;
        filtros.setNivelId(nivelId);
        var usuario = autenticacaoService.getUsuarioAutenticado();
        if (usuario.isBackoffice()) {
            filtros.setOrganizacaoId(usuario.getOrganizacaoId());
        }
        return filtros;
    }
}
