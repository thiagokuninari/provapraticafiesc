package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.repository.OAuthAccessTokenRepository;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeRequest;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CargoDepartamentoFuncionalidadeService {

    @Autowired
    private CargoDepartamentoFuncionalidadeRepository repository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private OAuthAccessTokenRepository tokenRepository;

    public Page<CargoDepartamentoFuncionalidade> getAll(PageRequest pageRequest,
                                                        CargoDepartamentoFuncionalidadeFiltros filtros) {
        return repository.findAll(filtros.toPredicate(), pageRequest);
    }

    public List<CargoDepartamentoFuncionalidade> getCargoDepartamentoFuncionalidadeByFiltro(
            CargoDepartamentoFuncionalidadeFiltros filtros) {
        return repository.findFuncionalidadesPorCargoEDepartamento(filtros.toPredicate());
    }

    public void save(CargoDepartamentoFuncionalidadeRequest funcionalidadeSaveRequest) {
        Usuario usuarioAutenticado = autenticacaoService.getUsuarioAutenticado().getUsuario();
        List<CargoDepartamentoFuncionalidade> itens = funcionalidadeSaveRequest.getFuncionalidadesIds()
                .stream()
                .map(item -> criarCargoDepartamentoFuncionalidade(funcionalidadeSaveRequest, usuarioAutenticado, item))
                .collect(Collectors.toList());
        repository.save(itens);
    }

    private CargoDepartamentoFuncionalidade criarCargoDepartamentoFuncionalidade(
            CargoDepartamentoFuncionalidadeRequest funcionalidadeSaveRequest,
            Usuario usuarioAutenticado,
            Integer item) {
        return CargoDepartamentoFuncionalidade.builder()
                .id(null)
                .cargo(new Cargo(funcionalidadeSaveRequest.getCargoId()))
                .departamento(new Departamento(funcionalidadeSaveRequest.getDepartamentoId()))
                .funcionalidade(new Funcionalidade(item))
                .dataCadastro(LocalDateTime.now())
                .usuario(usuarioAutenticado)
                .build();
    }

    public void remover(int id) {
        repository.delete(id);
    }

    @Transactional
    public void deslogar(Integer cargoId, Integer departamentoId) {
        List<Usuario> usuarios = usuarioRepository.findAllByCargoAndDepartamento(
                new Cargo(cargoId), new Departamento(departamentoId));
        usuarios.forEach(x -> tokenRepository.deleteTokenByUsername(x.getLogin()));
    }
}
