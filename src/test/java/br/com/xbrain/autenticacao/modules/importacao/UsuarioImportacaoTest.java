package br.com.xbrain.autenticacao.modules.importacao;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.importacao.dto.UsuarioCidadeImportacao;
import br.com.xbrain.autenticacao.modules.importacao.dto.UsuarioHierarquiaImportacao;
import br.com.xbrain.autenticacao.modules.importacao.dto.UsuarioImportacao;
import br.com.xbrain.autenticacao.modules.importacao.repository.UsuarioImportacaoRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
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
public class UsuarioImportacaoTest {

    @Autowired
    private UsuarioImportacaoRepository parceirosRepository;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private CidadeRepository cidadeRepository;

    /*
    * datasource:
      url: jdbc:oracle:thin:@//192.168.1.6:1521/DEV
      username: AUTENTICACAO
      password: py12nrk7

      datasource-parceiros:
      jdbcUrl: jdbc:oracle:thin:@//192.168.1.6:1521/DEV
      username: homologacao
      password: c1t1gu1zes
    * */

    //@Test
    public void importarUsuarios() {
        List<UsuarioImportacao> dados = parceirosRepository.getAllUsuariosParceirosOnline();

        for (UsuarioImportacao dado : dados) {
            Usuario usuario = new Usuario();
            BeanUtils.copyProperties(dado, usuario);

            //ignorar cargo 50 depart 50
            if (dado.getCargoId() != null && dado.getCargoId() != 0) {
                usuario.setCargo(new Cargo(dado.getCargoId()));
            }

            if (dado.getDepartamentoId() != null && dado.getDepartamentoId() != 0) {
                usuario.setDepartamento(new Departamento(dado.getDepartamentoId()));
            }

            if (dado.getUnidadeNegocioId() != null && dado.getUnidadeNegocioId() != 0) {
                usuario.setUnidadesNegocios(
                        Collections.singletonList(new UnidadeNegocio(dado.getUnidadeNegocioId())));
            }

            if (!CollectionUtils.isEmpty(dado.getEmpresasId())) {
                usuario.setEmpresas(dado.getEmpresasId().stream().map(Empresa::new)
                        .collect(Collectors.toList()));
            }

            if (usuario.getAlterarSenha() == null) {
                usuario.setAlterarSenha(Eboolean.F);
            }

            usuario = repository.save(usuario);

            System.out.println(">>> Registro importado com sucesso: \nID: " + usuario + "\nNOME: " + usuario.getNome());
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
