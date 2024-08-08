package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.usuario.model.SubNivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubNivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubNivelService {

    private final SubNivelRepository subNivelRepository;

    public List<SubNivel> findByIdIn(List<Integer> subNiveisIds) {
        return subNivelRepository.findByIdIn(subNiveisIds);
    }
}
