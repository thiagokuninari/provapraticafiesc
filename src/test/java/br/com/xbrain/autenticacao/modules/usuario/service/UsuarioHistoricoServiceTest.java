package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ColaboradorVendasService;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
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
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_usuario_historico.sql"})
public class UsuarioHistoricoServiceTest {

    @Autowired
    private UsuarioService service;
    @Autowired
    private UsuarioHistoricoRepository usuarioHistoricoRepository;
    @MockBean
    private ColaboradorVendasService colaboradorVendasService;

    @Test
    public void inativarUsuariosSemAcesso_usuarioInativo_quandoNaoTiverEfetuadoLoginDuranteTrintaEDoisDias() {
        service.inativarUsuariosSemAcesso();

        verify(colaboradorVendasService, times(2)).inativarColaborador(anyString());

        assertThat(service.findById(100))
                .extracting("id", "cpf", "situacao", "email")
                .contains(100, "38957979875", ESituacao.I, "ADMIN@XBRAIN.COM.BR");

        assertEquals(5, usuarioHistoricoRepository.findByUsuarioId(100).size());
    }

}
