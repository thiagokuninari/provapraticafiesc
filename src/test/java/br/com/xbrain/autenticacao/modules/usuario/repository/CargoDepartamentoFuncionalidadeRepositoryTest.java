package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.Application;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.predicate.FuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@Sql(scripts = "classpath:/tests_database.sql")
@Transactional
public class CargoDepartamentoFuncionalidadeRepositoryTest {

    @Autowired
    private CargoDepartamentoFuncionalidadeRepository repository;

    @Test
    public void deveRetornarOsCargosPorDepartamentoEFuncionalidade() {
        List<CargoDepartamentoFuncionalidade> funcionalidades = repository
                .findFuncionalidadesPorCargoEDepartamento(new FuncionalidadePredicate()
                        .comCargo(50)
                        .comDepartamento(50));

        Assert.assertFalse(funcionalidades.isEmpty());
    }
}