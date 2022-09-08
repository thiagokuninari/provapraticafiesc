package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.permissao.service.CargoDepartamentoFuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.predicate.DepartamentoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private CargoDepartamentoFuncionalidadeService cargoDepartamentoFuncionalidadeService;

    public List<Departamento> getPermitidosPorNivel(Integer nivelId) {
        return repository.findAll(
                new DepartamentoPredicate()
                        .doNivel(nivelId)
                        .filtrarPermitidos(autenticacaoService.getUsuarioAutenticado())
                        .build());
    }

    public List<Departamento> getPermitidosPorCargo(Integer cargoId) {
        return cargoDepartamentoFuncionalidadeService.getDepartamentoByCargo(cargoId);
    }
}
