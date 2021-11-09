package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:/tests_inativacao_usuario.sql")
public class UsuarioAcessoServiceIT {

    private static final String ORIGEM_INATIVACAO = "Teste de Integração";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioHistoricoRepository usuarioHistoricoRepository;

    @Autowired
    private UsuarioAcessoService usuarioAcessoService;

    @Value("${app-config.timer-usuario.email-usuario-viabilidade}")
    private String emailUsuarioViabilidade;

    @Test
    public void inativar_deveInativarUsuario_quandoUsuarioEstiverAptoInativar() {
        var usuariosInativados = usuarioAcessoService.inativarUsuariosSemAcesso(ORIGEM_INATIVACAO);
        Assertions.assertThat(usuariosInativados.longValue()).isEqualTo(3L);

        var usuarios = usuarioRepository.findBySituacaoAndIdIn(ESituacao.I, idsUsuariosParaTeste());
        Assertions.assertThat(usuarios.size()).isEqualTo(3);

        var emailsUsuariosViabilidade = emailUsuarioViabilidade.split(",");

        Assert.assertFalse(usuarios.stream().anyMatch(
            u -> Arrays.asList(emailsUsuariosViabilidade).contains(u.getEmail())));

        long usuariosHistoricoInativos = idsUsuariosParaTeste().stream()
            .map(id -> usuarioHistoricoRepository.getUltimoHistoricoPorUsuario(id).orElse(new UsuarioHistorico()))
            .filter(u -> u.getSituacao() != null && u.getSituacao() == ESituacao.I)
            .count();
        Assertions.assertThat(usuariosHistoricoInativos).isEqualTo(3L);
    }

    private List<Integer> idsUsuariosParaTeste() {
        return List.of(801, 802, 803, 804, 805, 806, 807);
    }

}
