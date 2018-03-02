package br.com.xbrain.autenticacao.modules.importacao;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.importacao.dto.*;
import br.com.xbrain.autenticacao.modules.importacao.repository.UsuarioImportacaoRepository;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.FuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@Rollback(false)
//@ActiveProfiles("importacao")
public class UsuarioImportacaoTest {

    @Autowired
    private UsuarioImportacaoRepository parceirosRepository;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private CidadeRepository cidadeRepository;

    @Autowired
    private FuncionalidadeRepository funcionalidadeRepository;

    @Autowired
    private CargoDepartamentoFuncionalidadeRepository cargoDepartamentoFuncionalidadeRepository;

    @Autowired
    private PermissaoEspecialRepository permissaoEspecialRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;
    /*
    * PARA RODAR A IMPORTAÇÃO:
    *
    * 1. UTILIZE O PROFILE DE IMPORTAÇÃO (APPLICATION-IMPORTACAO.YML)
    * 2. VERIFIQUE SE AS BASES ESTÃO CONFIGURADAS CORRETAMENTE
    * 3. COMENTE AS ANOTAÇÕES DE SEQUENCE DO USUÁRIO ID
    * */

    //@Test
    public void importarUsuarios() {
        List<UsuarioImportacao> dados = parceirosRepository.getAllUsuariosParceirosOnline();

        for (UsuarioImportacao dado : dados) {
            Usuario usuario = new Usuario();
            BeanUtils.copyProperties(dado, usuario);

            usuario.removerCaracteresDoCpf();

            if (dado.getCargoId() != null && dado.getCargoId() != 0) {
                usuario.setCargo(new Cargo(dado.getCargoId()));
            }

            Departamento departamento = null;
            if (dado.getDepartamentoId() != null && dado.getDepartamentoId() != 0) {
                departamento = departamentoRepository.findOne(dado.getDepartamentoId());
                departamento.forceLoad();
                usuario.setDepartamento(new Departamento(dado.getDepartamentoId()));
            }

            if (dado.getUnidadeNegocioId() != null && dado.getUnidadeNegocioId() != 0) {
                usuario.setUnidadesNegocios(
                        Collections.singletonList(new UnidadeNegocio(dado.getUnidadeNegocioId())));
            }

            if (!CollectionUtils.isEmpty(dado.getEmpresasId())) {
                usuario.setEmpresas(dado.getEmpresasId().stream().map(Empresa::new)
                        .collect(Collectors.toList()));
            } else {
                if (departamento != null) {
                    if (departamento.getNivel().getCodigo() == CodigoNivel.AGENTE_AUTORIZADO) {
                        List<Empresa> empresasAa = parceirosRepository.getEmpresasAa(usuario.getId());
                        if (!CollectionUtils.isEmpty(empresasAa)) {
                            usuario.setEmpresas(empresasAa);
                        }

                    }
                }

            }

            if (usuario.getAlterarSenha() == null) {
                usuario.setAlterarSenha(Eboolean.F);
            }

            usuario = repository.save(usuario);

            System.out.println(">>> Registro importado com sucesso: \nID: "
                    + usuario.getId() + "\nNOME: "
                    + usuario.getNome());
        }
    }

    //@Test
    public void importarCidadesUsuario() {
        List<Usuario> usuarios = (List<Usuario>) repository.findAll();
        for (Usuario usuario : usuarios) {
            List<UsuarioCidadeImportacao> cidades =
                    parceirosRepository.getAllCidadesUsuariosParceirosOnline(usuario.getId());
            if (!CollectionUtils.isEmpty(cidades)) {
                usuario.setCidades(convertCidades(cidades, usuario));
                repository.save(usuario);
            } else {
                System.out.println("CIDADE DO USUÁRIO NÃO IMPORTADO: \nusuarioId: "
                        + usuario.getId()
                        + "\ncidadeId: "
                        + usuario.getCidades().stream()
                                .map(UsuarioCidade::getCidadeIdAsString).collect(Collectors.joining(",")));
            }
        }
    }

    //@Test
    public void importarHierarquiasUsuario() {
        List<Usuario> usuarios = (List<Usuario>) repository.findAll();
        for (Usuario usuario : usuarios) {
            List<UsuarioHierarquiaImportacao> hierarquias =
                    parceirosRepository.getAllHierarquiasUsuariosParceirosOnline(usuario.getId());
            if (!CollectionUtils.isEmpty(hierarquias)) {
                convertHierarquias(hierarquias, usuario);
                repository.save(usuario);
            } else {
                System.out.println("HIERARQUIA DO USUÁRIO NÃO IMPORTADO: \nusuarioId: "
                        + usuario.getId()
                        + "\nusuarioSuperiorId: "
                        + usuario.getUsuariosHierarquia().stream()
                                .map(UsuarioHierarquia::getUsuarioSuperiorIdAsString).collect(Collectors.joining(",")));
            }
        }
    }

    //@Test
    public void importarCargoDepartamentoFuncionalidade() {
        List<CargoDepartamentoFuncionalidadeImportacao> dtos
                = parceirosRepository.getAllPermissoesPorCargoDepartamento();
        for (CargoDepartamentoFuncionalidadeImportacao dto : dtos) {
            Funcionalidade funcionalidade = funcionalidadeRepository.findByRole(dto.getRole()).orElse(null);

            if (funcionalidade != null) {
                CargoDepartamentoFuncionalidade cargoDepartamentoFuncionalidade = new CargoDepartamentoFuncionalidade();

                if (dto.getEmpresaId() != null && dto.getEmpresaId() != 0) {
                    cargoDepartamentoFuncionalidade.setEmpresa(new Empresa(dto.getEmpresaId()));
                }

                cargoDepartamentoFuncionalidade.setFuncionalidade(funcionalidade);

                if (dto.getUnidadeNegocioId() != null && dto.getUnidadeNegocioId() != 0) {
                    cargoDepartamentoFuncionalidade.setUnidadeNegocio(new UnidadeNegocio(dto.getUnidadeNegocioId()));
                }

                cargoDepartamentoFuncionalidade.setCargo(new Cargo(dto.getCargoId()));
                cargoDepartamentoFuncionalidade.setDepartamento(new Departamento(dto.getDepartamentoId()));

                if (dto.getUsuarioId() != null && dto.getUsuarioId() != 0) {
                    cargoDepartamentoFuncionalidade.setUsuario(new Usuario(dto.getUsuarioId()));
                } else {
                    cargoDepartamentoFuncionalidade.setUsuario(null);
                }

                cargoDepartamentoFuncionalidade.setDataCadastro(dto.getDataCadastro());
                cargoDepartamentoFuncionalidadeRepository.save(cargoDepartamentoFuncionalidade);
            } else {
                System.out.println("FUNCIONALIDADE (CARG_DEPART) NÃO IMPORTADA: funcionalidadeRole:"
                        + dto.getRole());
            }
        }
    }

    //@Test
    public void importarPermissoesEspeciais() {
        List<PermissaoEspecialImportacao> permissoes = parceirosRepository.getAllPermissoesEspeciais();

        for (PermissaoEspecialImportacao permissao : permissoes) {
            Funcionalidade funcionalidade = funcionalidadeRepository.findByRole(permissao.getRole()).orElse(null);

            if (funcionalidade != null) {

                PermissaoEspecial permissaoEspecial = new PermissaoEspecial();
                permissaoEspecial.setDataCadastro(permissao.getDataCadastro());
                permissaoEspecial.setDataBaixa(permissao.getDataBaixa());
                permissaoEspecial.setFuncionalidade(funcionalidade);

                if (permissao.getUsuarioId() != null && permissao.getUsuarioId() != 0) {
                    permissaoEspecial.setUsuario(new Usuario(permissao.getUsuarioId()));
                } else {
                    permissaoEspecial.setUsuario(null);
                }

                if (permissao.getUsuarioBaixaId() != null && permissao.getUsuarioBaixaId() != 0) {
                    permissaoEspecial.setUsuarioBaixa(new Usuario(permissao.getUsuarioBaixaId()));
                } else {
                    permissaoEspecial.setUsuarioBaixa(null);
                }

                if (permissao.getUsuarioCadastroId() != null && permissao.getUsuarioCadastroId() != 0) {
                    permissaoEspecial.setUsuarioCadastro(new Usuario(permissao.getUsuarioCadastroId()));
                } else {
                    permissaoEspecial.setUsuarioCadastro(null);
                }
                permissaoEspecialRepository.save(permissaoEspecial);
            } else {
                System.out.println("PERMISSÃO ESPECIAL NÃO IMPORTADA: funcionalidadeRole:"
                        + permissao.getRole());
            }
        }

    }

    private void convertHierarquias(List<UsuarioHierarquiaImportacao> dados, Usuario usuario) {
        for (UsuarioHierarquiaImportacao dado : dados) {

            UsuarioHierarquia usuarioHierarquia = new UsuarioHierarquia();
            BeanUtils.copyProperties(dado, usuarioHierarquia);

            UsuarioHierarquiaPk pk = new UsuarioHierarquiaPk(dado.getUsuarioId(), dado.getUsuarioSuperiorId());
            usuarioHierarquia.setUsuarioHierarquiaPk(pk);

            usuarioHierarquia.setUsuarioSuperior(new Usuario(usuarioHierarquia.getUsuarioSuperiorId()));

            if (dado.getUsuarioCadastroId() != null && dado.getUsuarioCadastroId() != 0) {
                usuarioHierarquia.setUsuarioCadastro(new Usuario(dado.getUsuarioCadastroId()));
            }

            usuarioHierarquia.setUsuario(usuario);
            usuario.adicionarHierarquia(usuarioHierarquia);
        }
    }

    private Set<UsuarioCidade> convertCidades(List<UsuarioCidadeImportacao> cidades, Usuario usuario) {
        Set<UsuarioCidade> usuariosCidades = new HashSet<>();

        for (UsuarioCidadeImportacao dado : cidades) {

            UsuarioCidade usuarioCidade = new UsuarioCidade();
            BeanUtils.copyProperties(dado, usuarioCidade);

            UsuarioCidadePk pk = new UsuarioCidadePk(dado.getUsuarioId(), dado.getCidadeId());
            usuarioCidade.setUsuarioCidadePk(pk);

            if (dado.getUsuarioBaixaId() != null && dado.getUsuarioBaixaId() != 0) {
                usuarioCidade.setUsuarioBaixa(repository.findOne(dado.getUsuarioBaixaId()));
            } else {
                usuarioCidade.setUsuarioBaixa(null);
            }

            if (dado.getUsuarioCadastroId() != null) {
                usuarioCidade.setUsuarioCadastro(repository.findOne(dado.getUsuarioCadastroId()));
            }

            usuarioCidade.setUsuario(usuario);
            usuarioCidade.setCidade(cidadeRepository.findOne(dado.getCidadeId()));
            usuariosCidades.add(usuarioCidade);
        }
        return usuariosCidades;
    }
}
