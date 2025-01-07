package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.filtros.EmpresaPredicate;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository repository;
    private final AutenticacaoService autenticacaoService;

    @Transactional(readOnly = true)
    public Iterable<Empresa> getAll(Integer unidadeNegocioId) {
        return repository.findAll(
            new EmpresaPredicate()
                .daUnidadeDeNegocio(unidadeNegocioId)
                .exibeXbrainSomenteParaXbrain(autenticacaoService.getUsuarioAutenticado().isXbrain())
                .build(),
            new Sort(ASC, "nome"));
    }

    public List<SelectResponse> findWithoutXbrain() {
        return repository.findAll()
            .stream()
            .filter(empresa -> empresa.getCodigo() != CodigoEmpresa.XBRAIN)
            .map(empresa -> new SelectResponse(empresa.getId(), empresa.getNome()))
            .collect(Collectors.toList());
    }
}
