package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("oracle-test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database_oracle.sql", "classpath:/tests_hierarquia.sql"})
public class UsuarioServiceItTest {


    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private UsuarioService service;
    @MockBean
    private InativarColaboradorMqSender inativarColaboradorMqSender;

    @Test
    public void inativarUsuariosSemAcesso_doisUsuariosInativados_quandoUsuarioNaoEfetuarLoginNosUltimosTrintaEDoisDias() {
        service.inativarUsuariosSemAcesso();

        Usuario usuarioInativo = service.findById(101);
        assertThat(usuarioHistoricoService.getHistoricoDoUsuario(usuarioInativo.getId()))
                .extracting("id", "motivo", "observacao")
                .contains(tuple(104, "INATIVIDADE DE ACESSO", "Inativado por falta de acesso"));

        assertEquals(ESituacao.I, usuarioInativo.getSituacao());
        assertEquals(ESituacao.I, service.findById(104).getSituacao());
        assertEquals(ESituacao.A, service.findById(100).getSituacao());
        assertEquals(ESituacao.A, service.findById(366).getSituacao());
        assertEquals(0, service.getUsuariosSemAcesso().size());
        verify(inativarColaboradorMqSender, times(2)).sendSuccess(anyString());
    }
}
