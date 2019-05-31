package br.com.xbrain.autenticacao.modules.usuario.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(scripts = "classpath:/usuario-ferias-repository-test.sql")
public class UsuarioFeriasRepositoryTest {

    @Autowired
    private UsuarioFeriasRepository usuarioFeriasRepository;

    @Test
    public void getUsuariosInativosComFeriasEmAberto_deveRetornarOsUsuarios() {
        assertEquals("INATIVO_COMFERIAS_VENCENDO@XBRAIN.COM.BR",
                usuarioFeriasRepository
                .getUsuariosInativosComFeriasEmAberto(LocalDate.of(2019, 1, 1))
                .get(0)
                .getEmail());
    }
}
