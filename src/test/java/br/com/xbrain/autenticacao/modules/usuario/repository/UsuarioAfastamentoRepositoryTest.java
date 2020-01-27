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
@Sql(scripts = "classpath:/usuario-afastamento-repository-test.sql")
public class UsuarioAfastamentoRepositoryTest {

    @Autowired
    private UsuarioAfastamentoRepository usuarioAfastamentoRepository;

    @Test
    public void getUsuariosInativosComAfastamentoEmAberto_deveRetornarOsUsuarios_quandoAfastametoVencerNaDataInformada() {
        assertEquals("INATIVO_COM_AFASTAMETNO_VENCENDO@XBRAIN.COM.BR",
            usuarioAfastamentoRepository
                .getUsuariosInativosComAfastamentoEmAberto(LocalDate.of(2019, 1, 1))
                .get(0)
                .getEmail());
    }
}
