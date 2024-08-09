package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Subnivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubnivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubnivelService {

    private final SubnivelRepository repository;

    public List<SelectResponse> getSubniveisSelect(Integer nivelId) {
        return repository.findByNivelIdAndSituacao(nivelId, ESituacao.A).stream()
            .map(subnivel -> SelectResponse.of(subnivel.getId(), subnivel.getNome()))
            .collect(Collectors.toList());
    }

    public List<Integer> getFuncionalidadesIds() {
        return this.getSubnivelFuncionalidadesIds(repository.findAll());
    }

    public Set<Subnivel> findByIdIn(Set<Integer> subNiveisIds) {
        return repository.findByIdIn(subNiveisIds);
    }

    public List<Integer> getSubnivelFuncionalidadesIds(Collection<Subnivel> subniveis) {
        return subniveis.stream()
            .flatMap(subnivel -> subnivel.getFuncionalidadesIds().stream())
            .collect(Collectors.toList());
    }
}
