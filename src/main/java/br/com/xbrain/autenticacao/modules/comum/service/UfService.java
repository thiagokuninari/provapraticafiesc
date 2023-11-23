package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UfService {

    private final UfRepository ufRepository;

    public Iterable<Uf> findAll(Sort sort) {
        return ufRepository.findAll(sort);
    }

    public List<SelectResponse> findAll() {
        return ufRepository.findByOrderByNomeAsc()
                .stream()
                .map(uf -> SelectResponse.of(uf.getId(), uf.getNome()))
                .collect(Collectors.toList());
    }

    public List<SelectResponse> findAllByRegionalId(Integer regionalId) {
        return ufRepository.buscarEstadosPorRegional(regionalId)
                .stream()
                .map(uf -> SelectResponse.of(uf.getId(), uf.getNome()))
                .collect(Collectors.toList());
    }

    public List<UfResponse> findAllByRegionalIdComUf(Integer regionalId) {
        return ufRepository.buscarEstadosPorRegional(regionalId)
            .stream()
            .map(uf -> new UfResponse(uf.getId(), uf.getNome(), uf.getUf()))
            .collect(Collectors.toList());
    }
}
