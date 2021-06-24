package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UfService {

    @Autowired
    private UfRepository ufRepository;

    public Iterable<Uf> findAll(Sort sort) {
        return ufRepository.findAll(sort);
    }

    public List<SelectResponse> findAll() {
        return ufRepository.findByOrderByNomeAsc()
                .stream()
                .map(uf -> SelectResponse.of(uf.getId(), uf.getNome()))
                .collect(Collectors.toList());
    }
}
