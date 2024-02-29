package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.predicate.RegionalPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.RegionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto.of;
import static br.com.xbrain.autenticacao.modules.comum.util.StreamUtils.distinctByKey;

@Service
public class RegionalService {

    @Autowired
    private RegionalRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<Regional> getAll() {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        RegionalPredicate predicate = new RegionalPredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return repository.getAll(predicate.build());
    }

    public List<RegionalDto> findAllAtivos() {
        return repository.findAllBySituacaoAndNovaRegional(ESituacao.A, Eboolean.V)
            .stream()
            .map(RegionalDto::of)
            .collect(Collectors.toList());
    }

    public List<SelectResponse> getAllByUsuarioId(Integer usuarioId) {
        return repository.getAllByUsuarioId(usuarioId)
            .stream()
            .map(s -> SelectResponse.of(s.getId(), s.getNome()))
            .collect(Collectors.toList());
    }

    public List<Integer> getRegionalIds(Integer usuarioId) {
        return repository.getAllByUsuarioId(usuarioId)
            .stream()
            .map(Regional::getId)
            .collect(Collectors.toList());
    }

    public RegionalDto findById(Integer regionalId) {
        return of(repository.findById(regionalId)
            .orElseThrow(() -> new ValidacaoException("Regional não encontrada.")));
    }

    public List<RegionalDto> getAtivosParaComunicados() {
        return getAll().stream().map(RegionalDto::of)
            .filter(distinctByKey(RegionalDto::getId))
            .collect(Collectors.toList());
    }

    public List<Integer> getNovasRegionaisIds() {
        return repository.getNovasRegionaisIds();
    }
}
