package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioSiteService {

    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    @Transactional(readOnly = true)
    public List<UsuarioNomeResponse> buscarUsuariosSitePorCargo(CodigoCargo cargo) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        if (usuarioAutenticado.isXbrainOuMso()) {
            return repository.buscarUsuariosPorCanalECargo(ECanal.ATIVO_PROPRIO, cargo);
        }
        return repository.findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(usuarioAutenticado.getId(), cargo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioNomeResponse> getUsuariosParaVincularAoSitePorSiteIdECargo(Integer siteId, CodigoCargo codigoCargo) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var sitePredicate = siteIdToPredicate(siteId);

        if (usuarioAutenticado.isXbrainOuMso()) {
            return repository.findAllSubordinadosDisponiveisParaSitePorCargo(sitePredicate, codigoCargo);
        }
        var usuariosDaHierarquia = getSubordinadosPorIdECargo(usuarioAutenticado.getId(), codigoCargo);
        return repository.findAllSubordinadosDisponiveisParaSitePorCargo(sitePredicate, codigoCargo)
            .stream()
            .filter(usuarioNomeResponse -> usuariosDaHierarquia.contains(usuarioNomeResponse.getId()))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioNomeResponse> getUsuariosDisponiveisPorCargo(CodigoCargo codigoCargo) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var sitePredicate = new SitePredicate();

        if (usuarioAutenticado.isXbrainOuMso()) {
            return repository.findAllSubordinadosDisponiveisParaSitePorCargo(sitePredicate.build(), codigoCargo);
        }
        var usuariosDaHierarquia = getSubordinadosPorIdECargo(usuarioAutenticado.getId(), codigoCargo);
        return repository.findAllSubordinadosDisponiveisParaSitePorCargo(sitePredicate.build(), codigoCargo)
            .stream()
            .filter(usuarioNomeResponse -> usuariosDaHierarquia.contains(usuarioNomeResponse.getId()))
            .collect(Collectors.toList());
    }

    public List<UsuarioNomeResponse> getVendedoresOperacaoAtivoProprioPorSiteId(Integer siteId) {
        return repository.findVendedoresPorSiteId(siteId);
    }

    @Transactional(readOnly = true)
    private List<Integer> getSubordinadosPorIdECargo(Integer usuarioId, CodigoCargo codigoCargo) {
        return repository.findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(usuarioId, codigoCargo)
            .stream()
            .map(UsuarioNomeResponse::getId)
            .collect(Collectors.toList());
    }

    private Predicate siteIdToPredicate(Integer siteId) {
        return new SitePredicate()
            .ignorarSite(siteId)
            .build();
    }
}
