package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.predicate.RegionalPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.RegionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<SelectResponse> getAllByUsuarioId(Integer usuarioId) {
        return repository.getAllByUsuarioId(usuarioId)
                .stream()
                .map(s -> SelectResponse.convertFrom(s.getId(), s.getNome()))
                .collect(Collectors.toList());
    }

    public RegionalDto findById(Integer grupoId) {
        var regionalDto = new RegionalDto();
        repository.findById(grupoId)
            .forEach(grupo -> {
                regionalDto.setId(grupo.getId());
                regionalDto.setNome(grupo.getNome());
                regionalDto.setSituacao(grupo.getSituacao());
            });
        return regionalDto;
    }
}
