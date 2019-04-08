package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.INATIVADO_SEM_ACESSO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.ULTIMO_ACESSO;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_usuario_historico.sql"})
@Transactional
public class UsuarioHistoricoServiceTest {

    private static LocalDateTime DATA_CADASTRO_DEFAULT = LocalDateTime.of(2019, 4, 1, 9, 30, 0);
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private UsuarioHistoricoRepository usuarioHistoricoRepository;
    @Autowired
    private MotivoInativacaoService motivoInativacaoService;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void gerarHistoricoUltimoAcessoDoUsuario_umHistoricoGerado_quandoUsuarioEfetuarLoginSemPossuirHistorico() {
        assertEquals(0, usuarioHistoricoRepository.findByUsuarioId(799).size());
        usuarioHistoricoService.gerarHistoricoUltimoAcessoDoUsuario(799);
        refresh();

        List<UsuarioHistorico> usuarioHistoricos = usuarioHistoricoRepository.findAllCompleteByUsuarioId(799);
        assertEquals(1, usuarioHistoricos.size());

        assertThat(usuarioHistoricos.get(0))
                .extracting("id", "situacao", "motivoInativacao", "observacao", "usuario.id")
                .contains(1, ESituacao.A, findMotivoInativacaoByCodigo(ULTIMO_ACESSO), null, 799);
    }

    @Test
    public void gerarHistoricoUltimoAcessoDoUsuario_umHistoricoAtualizado_quandoUsuarioEfetuarLoginEPossuirHistorico() {
        usuarioHistoricoService.gerarHistoricoUltimoAcessoDoUsuario(800);
        refresh();

        List<UsuarioHistorico> usuarioHistoricos = usuarioHistoricoRepository.findAllCompleteByUsuarioId(800);

        UsuarioHistorico usuarioHistorico = usuarioHistoricos.get(0);

        assertNotEquals(usuarioHistorico.getDataCadastro(), DATA_CADASTRO_DEFAULT);

        assertEquals(1, usuarioHistoricos.size());
        assertThat(usuarioHistorico)
                .extracting("id", "situacao", "motivoInativacao", "observacao", "usuario.id")
                .contains(204, ESituacao.A, findMotivoInativacaoByCodigo(ULTIMO_ACESSO), null, 800);
    }

    @Test
    public void gerarHistoricoUsuarioInativado_umHistoricoInativado_quandoDesejarInativarUmUsuario() {
        usuarioHistoricoService.gerarHistoricoUsuarioInativado(new Usuario(800));
        refresh();

        List<UsuarioHistorico> usuarioHistoricos = usuarioHistoricoRepository.findAllCompleteByUsuarioId(800);
        assertEquals(1, usuarioHistoricos.size());
        assertThat(usuarioHistoricos.get(0))
                .extracting("id", "situacao", "motivoInativacao", "observacao", "usuario.id")
                .contains(204, ESituacao.I, findMotivoInativacaoByCodigo(INATIVADO_SEM_ACESSO),
                        "Inativado por falta de acesso", 800);
    }

    private MotivoInativacao findMotivoInativacaoByCodigo(CodigoMotivoInativacao codigo) {
        return motivoInativacaoService.findByCodigoMotivoInativacao(codigo);
    }

    private void refresh() {
        this.entityManager.flush();
        this.entityManager.clear();
    }

}
