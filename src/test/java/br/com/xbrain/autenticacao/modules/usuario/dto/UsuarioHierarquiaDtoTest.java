package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioServiceHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioHierarquiaDtoTest {

    @Test
    public void of_deveRetornarUsuarioHierarquiaDto_quandoReceberUsuarioComoParametro() {
        assertThat(UsuarioHierarquiaDto.of(umUsuario(1)))
            .extracting("id", "razaoSocialNome", "cpf", "situacao")
            .containsExactly(1, "Um usuario", "12345678911", ESituacao.A.getDescricao());
    }

    @Test
    public void ofAgenteAutorizadoResponse_deveRetornarUsuarioHierarquiaDto_quandoReceberAaResponseComoParametro() {
        assertThat(UsuarioHierarquiaDto.ofAgenteAutorizadoResponse(umAgenteAutorizadoAtivoResponse()))
            .extracting("id", "razaoSocialNome", "cnpj", "situacao")
            .containsExactly(1, "TESTE AA", "00.000.0000/0001-00", "CONTRATO ATIVO");
    }

    @Test
    public void ofUsuarioSubordinadoDto_deveRetornarUsuarioHierarquiaDto_quandoReceberUsuarioSubordinadoDtoComoParametro() {
        assertThat(UsuarioHierarquiaDto.ofUsuarioSubordinadoDto(usuarioSubordinadoDtoDtoResponse(1)))
            .extracting("id", "razaoSocialNome", "cpf", "situacao")
            .containsExactly(1, "Uma nome", "12345678911", ESituacao.A.getDescricao());
    }

    @Test
    @SuppressWarnings("LineLength")
    public void ofAgenteAutorizadoResponseList_deveRetornarListaUsuarioHierarquiaDto_quandoReceberListaAgenteAutorizadoResponse() {
        assertThat(UsuarioHierarquiaDto.ofAgenteAutorizadoResponseList(umaListaDeAgenteAutorizadoResponse()))
            .extracting("id", "razaoSocialNome", "cnpj", "situacao")
            .containsExactly(tuple(1, "TESTE AA", "00.000.0000/0001-00", "CONTRATO ATIVO"),
                tuple(3, "TESTE AA INATIVO", "00.000.0000/0001-30", "INATIVO"),
                tuple(4, "TESTE AA REJEITADO", "00.000.0000/0001-40", "REJEITADO"),
                tuple(2, "OUTRO TESTE AA", "00.000.0000/0001-20", "CONTRATO ATIVO"));
    }

    @Test
    public void ofUsuarioSubordinadoDtoList_deveRetornarListaUsuarioHierarquiaDto_quandoReceberListaUsuarioSubordinadoDto() {
        assertThat(UsuarioHierarquiaDto.ofUsuarioSubordinadoDtoList(
            List.of(usuarioSubordinadoDtoDtoResponse(1), umOutroUsuarioSubordinadoDtoDtoResponse(2))))
            .extracting("id", "razaoSocialNome", "cpf", "situacao")
            .containsExactly(
                tuple(1, "Uma nome", "12345678911", "Ativo"),
                tuple(2, "Uma outro nome", "98765432111", "Inativo"));
    }

    private Usuario umUsuario(Integer id) {
        return Usuario.builder()
            .id(id)
            .nome("Um usuario")
            .cpf("12345678911")
            .situacao(ESituacao.A)
            .build();
    }

}
