package br.com.xbrain.autenticacao.modules.comum.service;

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

    public List<Organizacao> getAllSelect(Integer nivelId) {
        if (Objects.nonNull(nivelId)) {
            return repository.findAllByNiveisIdIn(nivelId);
        }
        return repository.findAll();
    }
}
