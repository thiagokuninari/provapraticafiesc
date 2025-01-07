package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import org.junit.Test;

import java.time.LocalDateTime;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioVendasPapIndiretoResponseTest {

    @Test
    public void of_deveRetornarUsuarioVendasPapIndiretoResponse_quandoSolicitado() {
        assertThat(UsuarioVendasPapIndiretoResponse.of(umUsuarioPapIndireto()))
            .extracting(UsuarioVendasPapIndiretoResponse::getUsuarioId, UsuarioVendasPapIndiretoResponse::getNome,
                UsuarioVendasPapIndiretoResponse::getCpf, UsuarioVendasPapIndiretoResponse::getDataCadastro,
                UsuarioVendasPapIndiretoResponse::getSituacao)
            .containsExactly(
                1, "UM USUARIO RESPONSE", "111.111.111-11", DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_SEG,
                    LocalDateTime.of(2018, 1, 1, 15, 0)), "Ativo");
    }
}

