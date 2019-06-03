package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.TabulacaoDistribuicaoMqRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DistribuirTabulacoesMqSender {

    @Value("${app-config.topic.mailing}")
    private String mailingTopic;

    @Value("${app-config.queue.distribuir-tabulacoes: distribuir-tabulacoes.queue}")
    private String distribuirTabulacoesQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void distribuirTabulacoes(TabulacaoDistribuicaoMqRequest tabulacaoDistribuicaoMqRequest) {
        try {
            rabbitTemplate.convertAndSend(mailingTopic, distribuirTabulacoesQueue, tabulacaoDistribuicaoMqRequest);
        } catch (Exception ex) {
            log.error("Erro ao enviar distribuição de tabulações para o rabbit", ex);
        }
    }
}
