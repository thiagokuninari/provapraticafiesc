package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioInativacaoDtoTest {

    @Test
    public void isFerias_deveRetonarTrue_seMotivoInativacaoForFeriasEDataInicioNaoNullEDataFimNaoNull() {
        var usuarioInativacao = umUsuarioInativacaoDto(CodigoMotivoInativacao.FERIAS);
        assertThat(usuarioInativacao.isFerias()).isTrue();
    }

    @Test
    public void isFerias_deveRetonarFalse_seMotivoInativacaoNaoForFeriasEDataInicioNaoNullEDataFimNaoNull() {
        var usuarioInativacao = umUsuarioInativacaoDto(CodigoMotivoInativacao.DEMISSAO);
        assertThat(usuarioInativacao.isFerias()).isFalse();
    }

    @Test
    public void isFerias_deveRetonarFalse_seMotivoInativacaoForFeriasEDataInicioNullEDataFimNaoNull() {
        var usuarioInativacao = umUsuarioInativacaoDto(CodigoMotivoInativacao.FERIAS);
        usuarioInativacao.setDataInicio(null);
        assertThat(usuarioInativacao.isFerias()).isFalse();
    }

    @Test
    public void isAfastamento_deveRetonarTrue_seMotivoInativacaoForAfastamentoEDataInicioNaoNull() {
        var usuarioInativacao = umUsuarioInativacaoDto(CodigoMotivoInativacao.AFASTAMENTO);
        assertThat(usuarioInativacao.isAfastamento()).isTrue();
    }

    @Test
    public void isAfastamento_deveRetonarFalse_seMotivoInativacaoNaoForAfastamentoEDataInicioNaoNull() {
        var usuarioInativacao = umUsuarioInativacaoDto(CodigoMotivoInativacao.DEMISSAO);
        assertThat(usuarioInativacao.isAfastamento()).isFalse();
    }

    @Test
    public void isAfastamento_deveRetonarFalse_seMotivoInativacaoForAfastamentoEDataInicioNull() {
        var usuarioInativacao = umUsuarioInativacaoDto(CodigoMotivoInativacao.AFASTAMENTO);
        usuarioInativacao.setDataInicio(null);
        assertThat(usuarioInativacao.isAfastamento()).isFalse();
    }

    @Test
    public void isFerias_deveRetonarFalse_seMotivoInativacaoForFeriasEDataInicioNaoNullEDataFimNull() {
        var usuarioInativacao = umUsuarioInativacaoDto(CodigoMotivoInativacao.FERIAS);
        usuarioInativacao.setDataFim(null);
        assertThat(usuarioInativacao.isFerias()).isFalse();
    }

    private UsuarioInativacaoDto umUsuarioInativacaoDto(CodigoMotivoInativacao codigoMotivoInativacao) {
        return UsuarioInativacaoDto.builder()
            .codigoMotivoInativacao(codigoMotivoInativacao)
            .dataInicio(LocalDate.of(2024, 7, 9))
            .dataFim(LocalDate.of(2024, 8, 9))
            .build();
    }
}
