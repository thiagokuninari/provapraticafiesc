package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalResponse;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubCanalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubCanalService {

    private static final ValidacaoException SUBCANAL_NAO_ENCONTRADO =
            new ValidacaoException("Erro, subcanal não encontrado.");

    @Autowired
    private SubCanalRepository repository;

    public List<SubCanalResponse> getAll() {
        return SubCanalResponse.of(repository.findAll());
    }

    public SubCanalResponse getSubCanalById(Integer id) {
        return SubCanalResponse.of(repository.findById(id)
            .orElseThrow(() -> SUBCANAL_NAO_ENCONTRADO));
    }
}
