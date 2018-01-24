package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Service
public class FuncionalidadeService {

    @Autowired
    private CargoDepartamentoFuncionalidadeRepository cargoDepartamentoFuncionalidadeRepository;
    @Autowired
    private PermissaoEspecialRepository permissaoEspecialRepository;

    private Predicate<CargoDepartamentoFuncionalidade> semEmpresaEUnidadeDeNegocio = f -> f.getEmpresa() == null
            && f.getUnidadeNegocio() == null;

    private Predicate<CargoDepartamentoFuncionalidade> possuiEmpresa(List<Empresa> empresasUsuario) {
        return f -> f.getEmpresa() != null && f.getUnidadeNegocio() == null
                && empresasUsuario.contains(f.getEmpresa());
    }

    private Predicate<CargoDepartamentoFuncionalidade> possuiUnidadeNegocio(List<UnidadeNegocio> unidadesUsuario) {
        return f -> f.getUnidadeNegocio() != null && f.getEmpresa() == null
                && unidadesUsuario.contains(f.getUnidadeNegocio());
    }

    private Predicate<CargoDepartamentoFuncionalidade> possuiEmpresaEUnidadeNegocio(List<UnidadeNegocio> unidadesUsuario,
                                                                                    List<Empresa> empresasUsuario) {
        return f -> f.getUnidadeNegocio() != null && f.getEmpresa() != null
                && unidadesUsuario.contains(f.getUnidadeNegocio()) && empresasUsuario.contains(f.getEmpresa());
    }

    public List<Funcionalidade> getFuncionalidadesPermitidasAoUsuario(Usuario usuario) {
        List<CargoDepartamentoFuncionalidade> funcionalidades = cargoDepartamentoFuncionalidadeRepository
                .findFuncionalidadesPorCargoEDepartamento(usuario.getCargo(), usuario.getDepartamento());

        return Stream.concat(
                funcionalidades
                        .stream()
                        .filter(semEmpresaEUnidadeDeNegocio
                                .or(possuiEmpresa(usuario.getEmpresas()))
                                .or(possuiUnidadeNegocio(singletonList(usuario.getUnidadeNegocio())))
                                .or(possuiEmpresaEUnidadeNegocio(
                                        singletonList(usuario.getUnidadeNegocio()),
                                        usuario.getEmpresas())))
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
}
