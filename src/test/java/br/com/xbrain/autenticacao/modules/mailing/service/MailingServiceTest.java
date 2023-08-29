package br.com.xbrain.autenticacao.modules.mailing.service;

import br.com.xbrain.autenticacao.config.feign.FeignBadResponseWrapper;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.mailing.client.MailingClient;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MailingServiceTest {

    @InjectMocks
    private MailingService service;
    @Mock
    private MailingClient client;

    @Test
    public void countQuantidadeAgendamentosProprietariosDoUsuario_integracaoException_seApiIndisponivel() {
        when(client.countQuantidadeAgendamentosProprietariosDoUsuario(eq(1), eq(ECanal.ATIVO_PROPRIO)))
            .thenThrow(new RetryableException("Connection refused (Connection refused) executing "
                + "GET http://localhost:8095/api/tabulacao/count/agendamentos/proprietarios/1", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.countQuantidadeAgendamentosProprietariosDoUsuario(1, ECanal.ATIVO_PROPRIO))
            .withMessage("#038 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void countQuantidadeAgendamentosProprietariosDoUsuario_integracaoException_seFiltrosObrigatoriosNaoInformados() {
        when(client.countQuantidadeAgendamentosProprietariosDoUsuario(eq(null), eq(null)))
            .thenThrow(new FeignBadResponseWrapper(400, null,
                "[{\"message\":\"O campo usuarioId é obrigatório.\",\"field\":usuarioId}]"));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.countQuantidadeAgendamentosProprietariosDoUsuario(null, null))
            .withMessage("#001 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void countQuantidadeAgendamentosProprietariosDoUsuario_usuarioDtoVendas_seSolicitado() {
        when(client.countQuantidadeAgendamentosProprietariosDoUsuario(eq(1), eq(ECanal.ATIVO_PROPRIO)))
            .thenReturn(Long.valueOf("1"));

        assertThat(service.countQuantidadeAgendamentosProprietariosDoUsuario(1, ECanal.ATIVO_PROPRIO))
            .isEqualTo(1);
    }
}
