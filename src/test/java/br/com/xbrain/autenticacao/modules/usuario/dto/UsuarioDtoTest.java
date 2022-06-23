package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.*;
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

    private static UsuarioDto umUsuarioDtoMso() {
        return UsuarioDto.builder()
            .nome("MSO FEEDER")
            .cpf("873.616.099-70")
            .nivelId(2)
            .organizacaoId(1)
            .usuarioCadastroId(1)
            .tiposFeeder(Set.of(EMPRESARIAL, RESIDENCIAL))
            .build();
    }

    private static UsuarioDto umUsuarioDtoOuvidoria() {
        return UsuarioDto.builder()
            .nome("OUVIDORIA NAO FEEDER")
            .cpf("286.250.583-88")
            .nivelId(15)
            .organizacaoId(1)
            .usuarioCadastroId(1)
            .tiposFeeder(Set.of(EMPRESARIAL, RESIDENCIAL))
            .build();
    }
}
