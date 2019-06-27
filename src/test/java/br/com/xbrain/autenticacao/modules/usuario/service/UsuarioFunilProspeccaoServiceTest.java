package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.FunilProspeccaoUsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_usuario_funil_prospeccao.sql"})
public class UsuarioFunilProspeccaoServiceTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioFunilProspeccaoService usuarioFunilProspeccaoService;

    private static final String CIDADE_BUSCA_LONDRINA = "LONDRINA";
    private static final String CIDADE_BUSCA_SAO_PAULO = "SAO PAULO";
    private static final String CIDADE_BUSCA_RIO_DE_JANEIRO = "RIO DE JANEIRO";
    private static final String CIDADE_BUSCA_CAPITOLIO = "CAPITOLIO";
    private static final ValidacaoException USUARIO_NOT_FOUND_EXCEPTION =
        new ValidacaoException("Usuário não encontrado");

    @Test
    public void findUsuarioDirecionadoByCidade_deveRetornarUmGerente_quandoHouverMaisDeUmExecutivoOuCoordenador() {
        var usuarioRediredionado = usuarioFunilProspeccaoService
            .findUsuarioDirecionadoByCidade(CIDADE_BUSCA_LONDRINA);
        var usuario = usuarioRepository.findById(usuarioRediredionado.getUsuarioId())
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);
        assertThat(usuarioRediredionado).isEqualTo(new FunilProspeccaoUsuarioDto(3));
        assertThat(usuario.getCargo().getCodigo()).isEqualTo(GERENTE_OPERACAO);
    }

    @Test
    public void findUsuarioDirecionadoByCidade_deveRetornarUmCoordenador_quandoHouverMaisDeUmExecutivo() {
        var usuarioRediredionado = usuarioFunilProspeccaoService
            .findUsuarioDirecionadoByCidade(CIDADE_BUSCA_SAO_PAULO);
        var usuario = usuarioRepository.findById(usuarioRediredionado.getUsuarioId())
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);
        assertThat(usuarioRediredionado).isEqualTo(new FunilProspeccaoUsuarioDto(5));
        assertThat(usuario.getCargo().getCodigo()).isEqualTo(COORDENADOR_OPERACAO);
    }

    @Test
    public void findUsuarioDirecionadoByCidade_deveRetornarUmExecutivo_quandoHouverApenasUmNaCidade() {
        var usuarioRediredionado = usuarioFunilProspeccaoService
            .findUsuarioDirecionadoByCidade(CIDADE_BUSCA_RIO_DE_JANEIRO);
        var usuario = usuarioRepository.findById(usuarioRediredionado.getUsuarioId())
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);
        assertThat(usuarioRediredionado).isEqualTo(new FunilProspeccaoUsuarioDto(1));
        assertThat(usuario.getCargo().getCodigo()).isEqualTo(EXECUTIVO);
    }

    @Test
    public void findUsuarioDirecionadoByCidade_deveRetornarUmExecutivoHunter_quandoHouverApenasUmNaCidade() {
        var usuarioRediredionado = usuarioFunilProspeccaoService
            .findUsuarioDirecionadoByCidade(CIDADE_BUSCA_CAPITOLIO);
        var usuario = usuarioRepository.findById(usuarioRediredionado.getUsuarioId())
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);
        assertThat(usuarioRediredionado).isEqualTo(new FunilProspeccaoUsuarioDto(7));
        assertThat(usuario.getCargo().getCodigo()).isEqualTo(EXECUTIVO_HUNTER);
    }

}