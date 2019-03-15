package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.AGENTE_AUTORIZADO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.ATIVO;
import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = { "classpath:/tests_database.sql"})
public class CargoDepartamentoFuncionalidadeRepositoryTest {

    private static Integer USUARIO_SOCIO_ID = 226;
    private static Integer CARGO_SOCIO_ID = 41;
    private static Integer DEPARTAMENTO_SOCIO_ID = 40;

    @Autowired
    private CargoDepartamentoFuncionalidadeRepository repository;

    @Test
    public void findFuncionalidadesDoCargoDepartamentoComCanal_funcionalidades_aoFiltrarPorDepartamentoECanal() {
        List<Funcionalidade> funcionalidades = repository
                .findFuncionalidadesDoCargoDepartamentoComCanal(CARGO_SOCIO_ID, DEPARTAMENTO_SOCIO_ID);

        assertEquals(4, funcionalidades.size());
        assertEquals("Relatório - Resumo de Mailing", funcionalidades.get(0).getNome());
        assertEquals(2, funcionalidades.get(0).getCanais().size());
        assertEquals(AGENTE_AUTORIZADO, funcionalidades.get(0).getCanais().get(0).getCanal());
        assertEquals(ATIVO, funcionalidades.get(0).getCanais().get(1).getCanal());
    }

    @Test
    public void findPermissoesEspeciaisDoUsuarioComCanal_funcionalidades_aoFiltrarPorUsuario() {
        List<Funcionalidade> funcionalidades = repository.findPermissoesEspeciaisDoUsuarioComCanal(USUARIO_SOCIO_ID);

        assertEquals(4, funcionalidades.size());
        assertEquals("Relatório - Resumo de Mailing", funcionalidades.get(0).getNome());
        assertEquals(2, funcionalidades.get(0).getCanais().size());
        assertEquals(AGENTE_AUTORIZADO, funcionalidades.get(0).getCanais().get(0).getCanal());
        assertEquals(ATIVO, funcionalidades.get(0).getCanais().get(1).getCanal());
    }
}
