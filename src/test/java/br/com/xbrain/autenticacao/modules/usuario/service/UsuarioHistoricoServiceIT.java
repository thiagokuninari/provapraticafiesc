package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
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
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.INATIVADO_SEM_ACESSO;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_usuario_historico.sql"})
@Transactional
public class UsuarioHistoricoServiceIT {

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
    public void gerarHistoricoInativacao_deveGerarHistorico_quandoUsuarioForInativadoPeloSistema() {
        assertEquals(0, usuarioHistoricoRepository.findByUsuarioId(799).size());
        usuarioHistoricoService.gerarHistoricoInativacao(799, "Teste de histórico");
        refresh();

        List<UsuarioHistorico> usuarioHistoricos = usuarioHistoricoRepository.findAllCompleteByUsuarioId(799);
        assertEquals(1, usuarioHistoricos.size());

        assertThat(usuarioHistoricos.get(0))
            .extracting("situacao", "motivoInativacao", "observacao", "usuario.id")
            .contains(ESituacao.I, findMotivoInativacaoByCodigo(INATIVADO_SEM_ACESSO),
                "Teste de histórico", 799);
    }

    private MotivoInativacao findMotivoInativacaoByCodigo(CodigoMotivoInativacao codigo) {
        return motivoInativacaoService.findByCodigoMotivoInativacao(codigo);
    }

    private void refresh() {
        this.entityManager.flush();
        this.entityManager.clear();
    }

}
