package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.predicate.OrganizacaoPredicate;
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

    public List<Organizacao> getAllSelect(Integer nivelId) {
        var predicate = new OrganizacaoPredicate();
        if (Objects.nonNull(nivelId)) {
            predicate.comNivel(nivelId);
        }
        return repository.findByPredicate(filtrarPorNivel(predicate).build());
    }

    private OrganizacaoPredicate filtrarPorNivel(OrganizacaoPredicate predicate) {
        var usuario = autenticacaoService.getUsuarioAutenticado();
        if (usuario.isBackoffice()) {
            predicate.comId(usuario.getOrganizacaoId());
        }
        return predicate;
    }
}
