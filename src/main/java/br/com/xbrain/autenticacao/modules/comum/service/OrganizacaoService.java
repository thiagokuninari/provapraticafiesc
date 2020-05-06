package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.dto.OrganizacaoResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.repository.OrganizacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class OrganizacaoService {

    private static final ValidacaoException NAO_ENCONTRADO = new ValidacaoException("Organização não encontrada.");

    @Autowired
    private OrganizacaoRepository repository;

    public List<Organizacao> getAllSelect(Integer nivelId) {
        if (Objects.nonNull(nivelId)) {
            return repository.findAllByNiveisIdIn(nivelId);
        }
        return repository.findAll();
    }

    public OrganizacaoResponse getById(Integer id) {
        return OrganizacaoResponse.of(repository.findById(id).orElseThrow(() -> NAO_ENCONTRADO));
    }
}
