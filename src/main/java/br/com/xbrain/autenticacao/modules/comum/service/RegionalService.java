package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.predicate.RegionalPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.RegionalRepository;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto.of;

@Service
public class RegionalService {

    @Autowired
    private RegionalRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;

    public List<Regional> getAll() {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        RegionalPredicate predicate = new RegionalPredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return repository.getAll(predicate.build());
    }

    public List<SelectResponse> getAllByUsuarioId(Integer usuarioId) {
        return repository.getAllByUsuarioId(usuarioId)
            .stream()
            .map(s -> SelectResponse.of(s.getId(), s.getNome()))
            .collect(Collectors.toList());
    }

    public RegionalDto findById(Integer regionalId) {
        return of(repository.findById(regionalId)
            .orElseThrow(() -> new ValidacaoException("Regional não encontrada.")));
    }

    public List<RegionalDto> getAtivosParaComunicados() {
        return Stream.concat(
            getAll().stream().map(RegionalDto::of),
            agenteAutorizadoService.getRegionais().stream())
            .distinct()
            .collect(Collectors.toList());
    }
}
