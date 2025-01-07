package br.com.xbrain.autenticacao.modules.email.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EmailPrioridade;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EmailBuilderTest {

    private EmailBuilder emailBuilder = new EmailBuilder();

    @Test
    public void comDestinatarios_deveRetornarDestinatariosNoEmail_quandoSolicitado() {
        assertThat(emailBuilder.comDestinatarios(List.of("usuario1@xbrain.com.br", "usuario2@xbrain.com.br")).build())
            .extracting(Email::getTo)
            .isEqualTo(List.of("usuario1@xbrain.com.br", "usuario2@xbrain.com.br"));
    }

    @Test
    public void comCorpo_deveRetornarCorpoNoEmail_quandoSolicitado() {
        assertThat(emailBuilder.comCorpo("Corpo do email").build())
            .extracting(Email::getBody)
            .isEqualTo("Corpo do email");
    }

    @Test
    public void comAssunto_deveRetornarAssuntoNoEmail_quandoSolicitado() {
        assertThat(emailBuilder.comAssunto("assunto").build())
            .extracting(Email::getSubject).isEqualTo("assunto");
    }

    @Test
    public void comCopia_deveRetornarCopiaNoEmail_quandoSolicitado() {
        assertThat(emailBuilder.comCopia(List.of("usuario1@xbrain.com.br", "usuario2@xbrain.com.br")).build())
            .extracting(Email::getCc)
            .isEqualTo(List.of("usuario1@xbrain.com.br", "usuario2@xbrain.com.br"));
    }

    @Test
    public void comCopiaOculta_deveRetornarCopiaOcultaNoEmail_quandoSolicitado() {
        assertThat(emailBuilder.comCopiaOculta(List.of("usuario1@xbrain.com.br", "usuario2@xbrain.com.br")).build())
            .extracting(Email::getBcc)
            .isEqualTo(List.of("usuario1@xbrain.com.br", "usuario2@xbrain.com.br"));
    }

    @Test
    public void comResponderPara_deveRetornarResponderParaNoEmail_quandoSolicitado() {
        assertThat(emailBuilder.comResponderPara("usuario@xbrain.com.br").build())
            .extracting(Email::getReplyTo)
            .isEqualTo("usuario@xbrain.com.br");
    }

    @Test
    public void comPriority_deveRetornarPriorityNoEmail_quandoSolicitado() {
        assertThat(emailBuilder.comPriority(EmailPrioridade.ALTA).build())
            .extracting(Email::getPriority)
            .isEqualTo(EmailPrioridade.ALTA);
    }
}
