package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    public void inativarUsuariosSemAcesso_deveInativarUsuario_quandoUsuarioEstiver32DiasSemLogarESemPrazoCarencia() {
        var usuariosInativados = usuarioAcessoService.inativarUsuariosSemAcesso(ORIGEM_INATIVACAO);
        Assertions.assertThat(usuariosInativados.longValue()).isEqualTo(4L);

        var usuarios = usuarioRepository.findBySituacaoAndIdIn(ESituacao.I, idsUsuariosParaTeste());
        Assertions.assertThat(usuarios).hasSize(2);

        var usuariosComDataReativacaoNull = usuarioRepository.findByDataReativacaoNotNull();
        Assertions.assertThat(usuariosComDataReativacaoNull).hasSize(2);

        long usuariosHistoricoInativos = idsUsuariosParaTeste().stream()
            .map(id -> usuarioHistoricoRepository.getUltimoHistoricoPorUsuario(id).orElse(new UsuarioHistorico()))
            .filter(u -> u.getSituacao() != null && u.getSituacao() == ESituacao.I)
            .count();
        Assertions.assertThat(usuariosHistoricoInativos).isEqualTo(2L);
    }

    private List<Integer> idsUsuariosParaTeste() {
        return List.of(801, 802, 803, 804, 805, 806, 807);
    }

}
