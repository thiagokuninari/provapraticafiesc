package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.FuncionalidadeSaveRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CargoDepartamentoFuncionalidadeService {

    @Autowired
    private CargoDepartamentoFuncionalidadeRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Page<CargoDepartamentoFuncionalidade> getAll(PageRequest pageRequest,
                                                        CargoDepartamentoFuncionalidadeFiltros filtros) {
        return repository.findAll(filtros.toPredicate(), pageRequest);
    }

    public List<CargoDepartamentoFuncionalidade> getCargoDepartamentoFuncionalidadeByFiltro(
            CargoDepartamentoFuncionalidadeFiltros filtros) {
        return repository.findFuncionalidadesPorCargoEDepartamento(filtros.toPredicate());
    }

    public void save(FuncionalidadeSaveRequest funcionalidadeSaveRequest) {
        Usuario usuarioAutenticado = usuarioRepository.findComplete(autenticacaoService.getUsuarioId()).get();
        List<CargoDepartamentoFuncionalidade> itens = funcionalidadeSaveRequest.getFuncionalidadesIds()
                .stream()
                .map(item -> criarCargoDepartamentoFuncionalidade(funcionalidadeSaveRequest, usuarioAutenticado, item))
                .collect(Collectors.toList());
        repository.deleteAll();
        repository.save(itens);
    }

    private CargoDepartamentoFuncionalidade criarCargoDepartamentoFuncionalidade(
            FuncionalidadeSaveRequest funcionalidadeSaveRequest,
            Usuario usuarioAutenticado,
            Integer item) {
        return CargoDepartamentoFuncionalidade.builder()
                .id(null)
                .cargo(new Cargo(funcionalidadeSaveRequest.getCargoId()))
                .departamento(new Departamento(funcionalidadeSaveRequest.getDepartamentoId()))
                .funcionalidade(new Funcionalidade(item))
                .dataCadastro(LocalDateTime.now())
                .empresa(new Empresa(funcionalidadeSaveRequest.getEmpresaId()))
                .unidadeNegocio(usuarioAutenticado.getUnidadesNegocios().get(0))
                .usuario(usuarioAutenticado)
                .build();
    }
}
