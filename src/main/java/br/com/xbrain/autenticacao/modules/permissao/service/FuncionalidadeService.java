package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.filtros.FuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.FuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class FuncionalidadeService {

    private final FuncionalidadeRepository repository;
    private final CargoDepartamentoFuncionalidadeRepository cargoDepartamentoFuncionalidadeRepository;
    private final PermissaoEspecialRepository permissaoEspecialRepository;

    public List<Funcionalidade> getFuncionalidadesPermitidasAoUsuario(Usuario usuario) {
        return Stream.concat(
            cargoDepartamentoFuncionalidadeRepository
                .findFuncionalidadesPorCargoEDepartamento(getPredicate(usuario))
                .stream()
                .filter(cargoDepartFunc -> isNivelXbrainOuMso(usuario) || deveObterFuncionalidade(cargoDepartFunc, usuario))
                .map(CargoDepartamentoFuncionalidade::getFuncionalidade),
            permissaoEspecialRepository
                .findPorUsuario(usuario.getId()).stream())
            .distinct()
            .collect(toList());
    }

    private BooleanBuilder getPredicate(Usuario usuario) {
        return new FuncionalidadePredicate()
            .comCargo(usuario.getCargoId())
            .comDepartamento(usuario.getDepartamentoId()).build();
    }

    private boolean isNivelXbrainOuMso(Usuario usuario) {
        return usuario.getNivelCodigo().equals(CodigoNivel.XBRAIN)
            || usuario.getNivelCodigo().equals(CodigoNivel.MSO);
    }

    private boolean deveObterFuncionalidade(CargoDepartamentoFuncionalidade cargoDepartamentoFuncionalidade, Usuario usuario) {
        return Objects.isNull(cargoDepartamentoFuncionalidade.getCanal())
            || !ObjectUtils.isEmpty(usuario.getCanais())
            && usuario.getCanais().contains(cargoDepartamentoFuncionalidade.getCanal());
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

    public List<Funcionalidade> getFuncionalidadesPermitidasAoUsuarioComCanal(Usuario usuario) {
        return Stream.of(
                cargoDepartamentoFuncionalidadeRepository
                        .findFuncionalidadesDoCargoDepartamentoComCanal(usuario.getCargoId(), usuario.getDepartamentoId()),
                cargoDepartamentoFuncionalidadeRepository
                        .findPermissoesEspeciaisDoUsuarioComCanal(usuario.getId()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(toList());
    }
}
