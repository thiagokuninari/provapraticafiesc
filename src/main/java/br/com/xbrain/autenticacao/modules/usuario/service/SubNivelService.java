package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.SubNivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubNivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubNivelService {

    private final SubNivelRepository repository;

    public List<SelectResponse> getSubNiveisSelect(Integer nivelId) {
        return repository.findByNivelIdAndSituacao(nivelId, ESituacao.A).stream()
            .map(subNivel -> SelectResponse.of(subNivel.getId(), subNivel.getNome()))
            .collect(Collectors.toList());
    }

    public List<Integer> getFuncionalidadesIdsByNivel(Integer nivelId) {
        return repository.findFuncionalidadesIdsByNivelId(nivelId);
    }

    public Set<SubNivel> findByIdIn(Set<Integer> subNiveisIds) {
        return repository.findByIdIn(subNiveisIds);
    }

    public List<Integer> getSubNivelFuncionalidadesIdsByCargo(Set<SubNivel> subNiveis, Integer cargoId) {
        return subNiveis.stream()
            .flatMap(subNivel -> subNivel.getFuncionalidadesIdsByCargoId(cargoId).stream())
            .collect(Collectors.toList());
    }
}
