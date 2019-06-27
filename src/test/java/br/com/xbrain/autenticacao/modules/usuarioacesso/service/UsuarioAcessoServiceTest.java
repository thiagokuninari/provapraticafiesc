package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Java6Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Sql(scripts = {"classpath:/tests_usuario_acesso.sql"})
@Transactional
public class UsuarioAcessoServiceTest {

    @Autowired
    private UsuarioAcessoService usuarioAcessoService;
    @Autowired
    private UsuarioAcessoRepository usuarioAcessoRepository;

    @Test
    public void registrarAcesso_deveRegistrarAcesso_quandoUsuarioEfetuarLogin() {
        usuarioAcessoService.registrarAcesso(100);

        assertThat(usuarioAcessoRepository.findAll())
                .hasSize(4);
    }
}