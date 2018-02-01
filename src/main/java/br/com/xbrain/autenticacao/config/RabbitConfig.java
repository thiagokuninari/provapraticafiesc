package br.com.xbrain.autenticacao.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${app-config.topic.autenticacao}")
    private String vendasTopic;
    @Value("${app-config.queue.contato}")
    private String contatoQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    TopicExchange topic() {
        return new TopicExchange(vendasTopic);
    }

    @Bean
    Queue contatoQueue() {
        return new Queue(contatoQueue, false);
    }

    @Bean
    Binding contatoBinding(TopicExchange exchange) {
        return BindingBuilder.bind(contatoQueue()).to(exchange).with(contatoQueue);
    }

}
