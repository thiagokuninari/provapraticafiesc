package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioDtoTest {

    @Test
    public void convertFrom_deveRetornarFeeders_quandoCadastrarUsuarioNivelMso() {
        assertThat(UsuarioDto.convertFrom(umUsuarioDtoMso()))
            .extracting("nome", "cpf", "tiposFeeder")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL));
    }

    @Test
    public void convertFrom_deveRetornarFeedersVazio_quandoCadastradoUsuarioComOutroNilvel() {
        assertThat(UsuarioDto.convertFrom(umUsuarioDtoOuvidoria()))
            .extracting("nome", "cpf", "tiposFeeder")
            .containsExactly("OUVIDORIA NAO FEEDER", "286.250.583-88", Set.of());
    }

    @Test
    public void convertFrom_deveRetornarNivelId_quandoPassadoNoDto() {
        assertThat(UsuarioDto.convertFrom(umUsuarioDtoMso()))
            .extracting("nome", "cpf", "tiposFeeder", "nivelId")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), 2);
    }

    @Test
    public void convertFrom_naoDeveRetornarNivelId_quandoNaoPassadoNoDto() {
        var usuarioSemNivelId = umUsuarioDtoMso();
        usuarioSemNivelId.setNivelId(null);

        assertThat(UsuarioDto.convertFrom(usuarioSemNivelId))
            .extracting("nome", "cpf", "tiposFeeder", "nivelId")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(), null);
    }

    @Test
    public void convertFrom_deveRetornarOrganizacaoId_quandoPassadoNoDto() {
        assertThat(UsuarioDto.convertFrom(umUsuarioDtoMso()))
            .extracting("nome", "cpf", "tiposFeeder", "organizacao")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), new Organizacao(1));
    }

    @Test
    public void convertFrom_naoDeveRetornarOrganizacaoId_quandoNaoPassadoNoDto() {
        var usuarioSemOrganizacaoId = umUsuarioDtoMso();
        usuarioSemOrganizacaoId.setOrganizacaoId(null);

        assertThat(UsuarioDto.convertFrom(usuarioSemOrganizacaoId))
            .extracting("nome", "cpf", "tiposFeeder", "organizacao")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), null);
    }

    @Test
    public void convertFrom_deveRetornarUsuarioCadastroId_quandoPassadoNoDto() {
        assertThat(UsuarioDto.convertFrom(umUsuarioDtoMso()))
            .extracting("nome", "cpf", "tiposFeeder", "usuarioCadastro")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), new Usuario(1));
    }

    @Test
    public void convertFrom_naoDeveRetornarUsuarioCadastroId_quandoNaoPassadoNoDto() {
        var usuarioSemUsuarioCadastroId = umUsuarioDtoMso();
        usuarioSemUsuarioCadastroId.setUsuarioCadastroId(null);

        assertThat(UsuarioDto.convertFrom(usuarioSemUsuarioCadastroId))
            .extracting("nome", "cpf", "tiposFeeder", "usuarioCadastro")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), null);
    }

    @Test
    public void of_deveRetornarUsuarioCadastroId_quandoPassado() {
        assertThat(UsuarioDto.of(umUsuarioMso()))
            .extracting("nome", "cpf", "tiposFeeder", "usuarioCadastroId")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), 101112);
    }

    @Test
    public void of_naoDeveRetornarUsuarioCadastroId_quandoNulo() {
        var usuarioComUsuarioCadastroNulo = umUsuarioMso();
        usuarioComUsuarioCadastroNulo.setUsuarioCadastro(null);

        assertThat(UsuarioDto.of(usuarioComUsuarioCadastroNulo))
            .extracting("nome", "cpf", "tiposFeeder", "usuarioCadastroId")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), null);
    }
}
