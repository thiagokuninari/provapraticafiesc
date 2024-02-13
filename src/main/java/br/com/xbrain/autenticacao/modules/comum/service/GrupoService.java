package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.predicate.GrupoPredicate;
import br.com.xbrain.autenticacao.modules.comum.repository.GrupoRepository;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ParceirosOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto.of;
import static br.com.xbrain.autenticacao.modules.comum.util.StreamUtils.distinctByKey;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private ParceirosOnlineService parceirosOnlineService;

    public List<GrupoDto> getAllByRegionalId(Integer regionalId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        GrupoPredicate predicate = new GrupoPredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return repository.findAllByRegionalId(regionalId, predicate.build())
            .stream()
            .map(GrupoDto::of)
            .collect(Collectors.toList());
    }

    public List<GrupoDto> getAllByRegionalIdAndUsuarioId(Integer regionalId, Integer usuarioId) {
        GrupoPredicate predicate = new GrupoPredicate()
                .filtrarPermitidos(usuarioId);
        return repository.findAllByRegionalId(regionalId, predicate.build())
                .stream()
                .map(GrupoDto::of)
                .collect(Collectors.toList());
    }

    public List<GrupoDto> getAllAtiva() {
        return repository.findBySituacao(ESituacao.A, new Sort("nome"))
            .stream()
            .map(GrupoDto::of)
            .collect(Collectors.toList());
    }

    public GrupoDto findById(Integer grupoId) {
        return of(repository.findById(grupoId)
            .orElseThrow(() -> new ValidacaoException("Grupo não encontrado.")));
    }

    public List<GrupoDto> getAtivosParaComunicados(Integer regionalId) {
        return Stream.concat(
            getAllByRegionalId(regionalId).stream(),
            parceirosOnlineService.getGrupos(regionalId).stream())
            .filter(distinctByKey(GrupoDto::getId))
            .collect(Collectors.toList());
    }
}
