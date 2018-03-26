package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.filtros.FuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.FuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class FuncionalidadeService {

    @Autowired
    private FuncionalidadeRepository repository;

    @Autowired
    private CargoDepartamentoFuncionalidadeRepository cargoDepartamentoFuncionalidadeRepository;

    @Autowired
    private PermissaoEspecialRepository permissaoEspecialRepository;

    public List<Funcionalidade> getFuncionalidadesPermitidasAoUsuario(Usuario usuario) {
        FuncionalidadePredicate predicate = new FuncionalidadePredicate()
                .comCargo(usuario.getCargoId())
                .comDepartamento(usuario.getDepartamentoId());
        List<CargoDepartamentoFuncionalidade> funcionalidades = cargoDepartamentoFuncionalidadeRepository
                .findFuncionalidadesPorCargoEDepartamento(predicate.build());
        return Stream.concat(
                funcionalidades
                        .stream()
                        .map(CargoDepartamentoFuncionalidade::getFuncionalidade),
                permissaoEspecialRepository
                        .findPorUsuario(usuario.getId()).stream())
                .distinct()
                .collect(toList());
    }

    public List<SimpleGrantedAuthority> getPermissoes(Usuario usuario) {
        return getFuncionalidadesPermitidasAoUsuario(usuario)
                .stream()
                .map(f -> "ROLE_" + f.getRole())
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
    }

    public List<FuncionalidadeResponse> getAll() {
        return FuncionalidadeResponse.convertFrom(repository.findAllByOrderByNome());
    }

}
