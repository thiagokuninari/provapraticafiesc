package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeRequest;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CargoDepartamentoFuncionalidadeService {

    private final CargoDepartamentoFuncionalidadeRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final AutenticacaoService autenticacaoService;

    public Page<CargoDepartamentoFuncionalidade> getAll(PageRequest pageRequest,
                                                        CargoDepartamentoFuncionalidadeFiltros filtros) {
        return repository.findAll(filtros.toPredicate(), pageRequest);
    }

    public List<CargoDepartamentoFuncionalidade> getCargoDepartamentoFuncionalidadeByFiltro(
            CargoDepartamentoFuncionalidadeFiltros filtros) {
        return repository.findFuncionalidadesPorCargoEDepartamento(filtros.toPredicate());
    }

    public List<Departamento> getDepartamentoByCargo(Integer cargoId) {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();
        filtros.setCargoId(cargoId);
        return repository.findAllDepartamentos(filtros.toPredicate());
    }

    @Transactional
    public void save(CargoDepartamentoFuncionalidadeRequest request) {
        Usuario usuarioAutenticado = autenticacaoService.getUsuarioAutenticado().getUsuario();
        List<Integer> funcionalidadesIds = tratarPermissoesExistentes(request);
        List<CargoDepartamentoFuncionalidade> itens = funcionalidadesIds
                .stream()
                .map(item -> criarCargoDepartamentoFuncionalidade(request, usuarioAutenticado, item))
                .collect(Collectors.toList());
        repository.save(itens);
    }

    private CargoDepartamentoFuncionalidade criarCargoDepartamentoFuncionalidade(
            CargoDepartamentoFuncionalidadeRequest request,
            Usuario usuarioAutenticado,
            Integer funcionalidadeId) {
        return CargoDepartamentoFuncionalidade.builder()
                .cargo(new Cargo(request.getCargoId()))
                .departamento(new Departamento(request.getDepartamentoId()))
                .funcionalidade(
                        Funcionalidade
                                .builder()
                                .id(funcionalidadeId)
                                .build())
                .dataCadastro(LocalDateTime.now())
                .usuario(usuarioAutenticado)
                .build();
    }

    private List<Integer> tratarPermissoesExistentes(CargoDepartamentoFuncionalidadeRequest request) {
        CargoDepartamentoFuncionalidadePredicate predicate = new CargoDepartamentoFuncionalidadePredicate();
        predicate.comCargo(request.getCargoId());
        predicate.comDepartamento(request.getDepartamentoId());
        List<CargoDepartamentoFuncionalidade> lista = repository
                .findFuncionalidadesPorCargoEDepartamento(predicate.build());

        return request.getFuncionalidadesIds().stream().filter(x ->
                !lista.stream().map(y -> y.getFuncionalidade().getId()).collect(Collectors.toList()).contains(x)
        ).collect(Collectors.toList());
    }

    @Transactional
    public void remover(int id) {
        repository.delete(id);
    }

    @Transactional
    public void deslogar(Integer cargoId, Integer departamentoId) {
        usuarioRepository
                .findAllByCargoAndDepartamento(new Cargo(cargoId), new Departamento(departamentoId))
                .forEach(u -> autenticacaoService.logout(u.getLogin()));
    }
}
