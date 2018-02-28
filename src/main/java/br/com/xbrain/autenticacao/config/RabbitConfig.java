package br.com.xbrain.autenticacao.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private String autenticacaoTopic;

    @Value("${app-config.queue.usuario-cadastro}")
    private String usuarioCadastroQueue;

    @Value("${app-config.queue.usuario-cadastro-success}")
    private String usuarioCadastroSuccessQueue;

    @Value("${app-config.queue.usuario-cadastro-failure}")
    private String usuarioCadastroFailureQueue;

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public TopicExchange topic() {
        return new TopicExchange(autenticacaoTopic);
    }

    @Bean
    Queue usuarioCadastroQueue() {
        return new Queue(usuarioCadastroQueue, false);
    }

    @Bean
    Queue usuarioCadastroSuccessQueue() {
        return new Queue(usuarioCadastroSuccessQueue, false);
    }

    @Bean
    Queue usuarioCadastroFailureQueue() {
        return new Queue(usuarioCadastroFailureQueue, false);
    }

    @Bean
    public Binding usuarioCadastroBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioCadastroQueue()).to(exchange).with(usuarioCadastroQueue);
    }

    @Bean
    public Binding usuarioCadastroSuccessBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioCadastroSuccessQueue()).to(exchange).with(usuarioCadastroSuccessQueue);
    }

    @Bean
    public Binding usuarioCadastroSuccessFailure(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioCadastroFailureQueue()).to(exchange).with(usuarioCadastroFailureQueue);
    }
}
