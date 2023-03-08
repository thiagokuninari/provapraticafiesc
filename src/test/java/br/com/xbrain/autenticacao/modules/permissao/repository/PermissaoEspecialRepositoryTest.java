package br.com.xbrain.autenticacao.modules.permissao.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql("classpath:/tests_permissao_especial.sql")
public class PermissaoEspecialRepositoryTest {

    @Autowired
    private PermissaoEspecialRepository repository;

    @Test
    public void deletarPermissaoEspecialBy_deveRemoverPermissaoEspecial_quandoInformarUsuarioEFuncionalidade() {
        repository.deletarPermissaoEspecialBy(List.of(9000, 9001), List.of(300, 303));
        assertEquals(repository.findAll().size(), 2);
    }

    @Test
    public void deletarPermissaoEspecialBy_naoDeveRemoverPermissaoEspecial_quandoNaoEncontrarFuncionalidadeEUsuario() {
        repository.deletarPermissaoEspecialBy(List.of(9009, 9010), List.of(300, 303));
        assertEquals(repository.findAll().size(), 4);
    }

    @Test
    public void findByUsuario_deveRetornarFuncionalidadeId_seSolicitado() {
        assertEquals(repository.findByUsuario(300).size(), 1);
    }

    @Test
    public void existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull_deveRetornarTrue_seExistir() {
        assertTrue(repository.existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(300, 9000));
    }

    @Test
    public void existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull_deveRetornarFalse_seNaoExistir() {
        assertFalse(repository.existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(300, 9001));
    }
}
