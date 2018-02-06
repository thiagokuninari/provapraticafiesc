package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static helpers.Empresas.CLARO_TV;
import static helpers.Empresas.NET;
import static helpers.UnidadesNegocio.UNIDADE_PESSOAL;
import static helpers.UnidadesNegocio.UNIDADE_RESIDENCIAL_E_COMBOS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class FuncionalidadeServiceTest {

    @Autowired
    private FuncionalidadeService funcionalidadeService;
    @MockBean
    private CargoDepartamentoFuncionalidadeRepository cargoDepartamentoFuncionalidadeRepository;

    private static Usuario USUARIO_NET_UNIDADE_PESSOAL = usuario(10, NET, UNIDADE_PESSOAL);

    @Test
    public void deveFiltrarAsFuncionalidadesDeAcordoComAEmpresaEUnidadeDeNegocioDoUsuario() {
        when(cargoDepartamentoFuncionalidadeRepository
                .findFuncionalidadesPorCargoEDepartamento(any())).thenReturn(
                Arrays.asList(
                        funcionalidade(1, "todos", null, null),
                        funcionalidade(2, "somente claroTV", CLARO_TV, null),
                        funcionalidade(3, "somente net", NET, null),
                        funcionalidade(4, "somente unidadeResidencialECombos", null, UNIDADE_RESIDENCIAL_E_COMBOS),
                        funcionalidade(5, "somente unidadePessoal", null, UNIDADE_PESSOAL),
                        funcionalidade(6, "somente net e unidadePessoal", NET, UNIDADE_PESSOAL),
                        funcionalidade(7, "somente claroTV e unidadeResidencialECombos", CLARO_TV, UNIDADE_RESIDENCIAL_E_COMBOS),
                        funcionalidade(8, "somente net e unidadeResidencialECombos", NET, UNIDADE_RESIDENCIAL_E_COMBOS)
                ));

        List<Funcionalidade> funcionalidades = funcionalidadeService
                .getFuncionalidadesPermitidasAoUsuario(USUARIO_NET_UNIDADE_PESSOAL);

        assertEquals(4, funcionalidades.size());
        assertEquals("todos", funcionalidades.get(0).getNome());
        assertEquals("somente net", funcionalidades.get(1).getNome());
        assertEquals("somente unidadePessoal", funcionalidades.get(2).getNome());
        assertEquals("somente net e unidadePessoal", funcionalidades.get(3).getNome());
    }

    private CargoDepartamentoFuncionalidade funcionalidade(int id, String nome, Empresa empresa, UnidadeNegocio unidadeNegocio) {
        CargoDepartamentoFuncionalidade perfilDepFuncionalidade = new CargoDepartamentoFuncionalidade();
        perfilDepFuncionalidade.setFuncionalidade(new Funcionalidade(id, nome, nome));
        perfilDepFuncionalidade.setEmpresa(empresa);
        perfilDepFuncionalidade.setUnidadeNegocio(unidadeNegocio);
        return perfilDepFuncionalidade;
    }

    private static Usuario usuario(int id, Empresa empresa, UnidadeNegocio unidadeNegocio) {
        Usuario usuario = new Usuario(id);
        usuario.setEmpresas(new ArrayList<>());
        usuario.getEmpresas().add(empresa);
        usuario.setUnidadesNegocios(Arrays.asList(unidadeNegocio));
        return usuario;
    }

    private static Empresa empresa(int id, String nome) {
        return new Empresa(id, nome);

    }

    private static UnidadeNegocio unidadeNegocio(int id, String nome) {
        return new UnidadeNegocio(id, nome);
    }
}
