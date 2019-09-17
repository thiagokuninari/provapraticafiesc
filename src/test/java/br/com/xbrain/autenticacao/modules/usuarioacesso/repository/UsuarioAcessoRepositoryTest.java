package br.com.xbrain.autenticacao.modules.usuarioacesso.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = {"classpath:/tests_usuario_acesso.sql"})
@Transactional
public class UsuarioAcessoRepositoryTest {

    @Autowired
    private UsuarioAcessoRepository usuarioAcessoRepository;

    @Test
    public void findAllUltimoAcessoUsuarios_deveRetornarUsuarios_queNaoEfetuaramAcessoAoSistemaDuranteTrintaEDoisDias() {
        assertThat(usuarioAcessoRepository.findAllUltimoAcessoUsuarios())
            .extracting("usuario.id", "usuario.email")
            .containsExactly(
                tuple(301, "JOAO@XBRAIN.COM.BR"),
                tuple(302, "CAIO@XBRAIN.COM.BR"),
                tuple(303, "ALBERTO@XBRAIN.COM.BR"),
                tuple(304, "MARIA@XBRAIN.COM.BR"),
                tuple(305, "EDUARDA@XBRAIN.COM.BR"),
                tuple(306, "ERICA@XBRAIN.COM.BR"));
    }

    @Test
    public void deletarHistoricoUsuarioAcesso_deveDeletarQuatroHistorico_quandoDataCadastroDoRegistroUltrapassarDoisMeses() {
        assertThat(usuarioAcessoRepository.countUsuarioAcesso()).isEqualTo(19);
        usuarioAcessoRepository.deletarHistoricoUsuarioAcesso();
        assertThat(usuarioAcessoRepository.countUsuarioAcesso()).isEqualTo(15);
    }
}
