package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import org.junit.Test;

import java.time.LocalDateTime;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioDtoVendasPapIndireto;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioVendasPapIndiretoResponseTest {

    @Test
    public void of_deveRetornarUsuarioVendasPapIndiretoResponseCadastrado_quandoDataSaidaNula() {
        assertThat(UsuarioVendasPapIndiretoResponse.of(umUsuarioResponse(), umUsuarioDtoVendasPapIndireto()))
            .extracting(UsuarioVendasPapIndiretoResponse::getId, UsuarioVendasPapIndiretoResponse::getUsuarioId,
                UsuarioVendasPapIndiretoResponse::getNome, UsuarioVendasPapIndiretoResponse::getCpf,
                UsuarioVendasPapIndiretoResponse::getAgenteAutorizadoId, UsuarioVendasPapIndiretoResponse::getCnpjAa,
                UsuarioVendasPapIndiretoResponse::getRazaoSocialAa, UsuarioVendasPapIndiretoResponse::getDataCadastro,
                UsuarioVendasPapIndiretoResponse::getDataSaidaCnpj, UsuarioVendasPapIndiretoResponse::getSituacao)
            .containsExactly(
                1, 1, "UM USUARIO RESPONSE", "111.111.111-11", 100, "64.262.572/0001-21", "Razao Social Teste",
                DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_SEG, LocalDateTime.of(2018, 12, 1, 0, 0)),
                "", "Ativo");
    }

    @Test
    public void of_deveRetornarUsuarioVendasPapIndiretoResponseRemanejado_quandoDataSaidaPresente() {
        var usuarioAutenticacao = umUsuarioResponse();
        usuarioAutenticacao.setDataSaidaCnpj(LocalDateTime.of(2019, 12, 1, 0, 0));

        assertThat(UsuarioVendasPapIndiretoResponse.of(usuarioAutenticacao, umUsuarioDtoVendasPapIndireto()))
            .extracting(UsuarioVendasPapIndiretoResponse::getId, UsuarioVendasPapIndiretoResponse::getUsuarioId,
                UsuarioVendasPapIndiretoResponse::getNome, UsuarioVendasPapIndiretoResponse::getCpf,
                UsuarioVendasPapIndiretoResponse::getAgenteAutorizadoId, UsuarioVendasPapIndiretoResponse::getCnpjAa,
                UsuarioVendasPapIndiretoResponse::getRazaoSocialAa, UsuarioVendasPapIndiretoResponse::getDataCadastro,
                UsuarioVendasPapIndiretoResponse::getDataSaidaCnpj, UsuarioVendasPapIndiretoResponse::getSituacao)
            .containsExactlyInAnyOrder(
                1, 1, "UM USUARIO RESPONSE", "111.111.111-11", 100, "64.262.572/0001-21", "Razao Social Teste",
                DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_SEG, LocalDateTime.of(2018, 12, 1, 0, 0)),
                DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_SEG, LocalDateTime.of(2019, 12, 1, 0, 0)),
                "REMANEJADO");
    }
}

