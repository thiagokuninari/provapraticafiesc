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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/*@Transactional
@SpringBootTest
@Rollback(false)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ActiveProfiles("importacao")*/
public class UsuarioImportacaoTest {

    private final Logger log = LoggerFactory.getLogger(UsuarioImportacaoTest.class);

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
    * 4. QUANDO FINALIZAR, ALTERAR SEQUENCE DE USUARIO: SEQ_USUARIO
    * */

    //@Test
    public void importarUsuarios() {
        List<UsuarioImportacao> dados = parceirosRepository.getAllUsuariosParceirosOnline();

        for (UsuarioImportacao dado : dados) {
            dado.trimProperties();
            dado.toUpperCaseProperties();

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
            } else {
                if (!ObjectUtils.isEmpty(departamento)) {
                    if (departamento.getNivel().getCodigo() == CodigoNivel.AGENTE_AUTORIZADO) {
                        List<UnidadeNegocio> unidades = parceirosRepository.getUnidadesNegociosAa(usuario.getId());
                        if (!CollectionUtils.isEmpty(unidades)) {
                            usuario.setUnidadesNegocios(unidades);
                        }
                    }
                }
            }

            if (!CollectionUtils.isEmpty(dado.getEmpresasId())) {
                usuario.setEmpresas(dado.getEmpresasId().stream().map(Empresa::new)
                        .collect(Collectors.toList()));
            } else {
                if (!ObjectUtils.isEmpty(departamento)) {
                    if (departamento.getNivel().getCodigo() == CodigoNivel.AGENTE_AUTORIZADO) {
                        List<Empresa> empresasAa = parceirosRepository.getEmpresasAa(usuario.getId());
                        if (!CollectionUtils.isEmpty(empresasAa)) {
                            usuario.setEmpresas(empresasAa);
                        }
                    }
                }
            }

            if (dado.getDepartamentoId() == 50 && dado.getCargoId() == 50) {
                if (CollectionUtils.isEmpty(usuario.getEmpresas())) {
                    usuario.setEmpresas(Collections.singletonList(new Empresa(4)));
                } else {
                    usuario.getEmpresas().add(new Empresa(4));
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
    //@Transactional(propagation = Propagation.NEVER)
    public void importarCpfsUsuarios() {
        List<Usuario> usuarios = repository.findAllByCpfIsNull();
        //List<Usuario> usuariosUpdate = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            List<String> cpfs = parceirosRepository.getCpfUsuarioColaboradorVendas(usuario.getId());

            if (!CollectionUtils.isEmpty(cpfs)) {
                usuario.setCpf(cpfs.get(0));
                usuario.removerCaracteresDoCpf();

                Optional<Usuario> usuarioOptional = repository.findTop1UsuarioByCpf(usuario.getCpf());
                if (!usuarioOptional.isPresent()) {
                    repository.save(usuarios);
                } else {
                    System.out.println("CPF DO USUÁRIO JÁ IMPORTADO: \nusuarioId: " + usuario.getId());
                }
            } else {
                System.out.println("CPF DO USUÁRIO NÃO IMPORTADO: \nusuarioId: "
                        + usuario.getId());
            }
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

            if (!ObjectUtils.isEmpty(funcionalidade)) {
                //validarRole(funcionalidade);

                CargoDepartamentoFuncionalidade cargoDepartamentoFuncionalidade = new CargoDepartamentoFuncionalidade();

                cargoDepartamentoFuncionalidade.setFuncionalidade(funcionalidade);

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

            if (!ObjectUtils.isEmpty(funcionalidade)) {
                //validarRole(funcionalidade);

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

    private void validarRole(Funcionalidade funcionalidade) {
        String role = funcionalidade.getRole()
                .replaceAll("EXTRACAO", "EXT")
                .replaceAll("VISUALIZAR", "VIS")
                .replaceAll("GERENCIAR", "GER")
                .replaceAll("APROVACAO", "APROV")
                .replaceAll("AGENTE_AUTORIZADO", "AA")
                .replaceAll("DESCREDENCIAMENTO", "DESCRED");
        funcionalidade.setRole(role);
    }
}
