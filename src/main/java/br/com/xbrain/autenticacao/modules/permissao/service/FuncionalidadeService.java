package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.CHM_ADM_CHAMADOS;
import static java.util.stream.Collectors.toList;

@Service
public class FuncionalidadeService {

    private static final Integer CHAMADO_APPLICATION_ID = 15;

    @Autowired
    private FuncionalidadeRepository repository;

    @Autowired
    private CargoDepartamentoFuncionalidadeRepository cargoDepartamentoFuncionalidadeRepository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private PermissaoEspecialRepository permissaoEspecialRepository;

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

    public List<FuncionalidadeResponse> getAll(HttpServletRequest request) {
        var funcionalidades = validarPermissaoAdmSuporte(repository.findAllByOrderByNome(), request);
        return FuncionalidadeResponse.convertFrom(funcionalidades);
    }

    private List<Funcionalidade> validarPermissaoAdmSuporte(List<Funcionalidade> funcionalidades, HttpServletRequest request) {
        var userPermissoes = autenticacaoService.getUsuarioAutenticado().getPermissoes();
        if (AutenticacaoService.getUsuarioEmuladorId(request) != null
            || !userPermissoes.toString().contains(CHM_ADM_CHAMADOS.getRole())) {
            funcionalidades = funcionalidades.stream().filter(func -> func.getAplicacao()
                .getId() != CHAMADO_APPLICATION_ID).collect(toList());
        }
        return funcionalidades;
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
