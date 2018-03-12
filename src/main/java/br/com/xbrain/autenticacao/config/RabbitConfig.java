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
    private String usuarioCadastroMq;

    @Value("${app-config.queue.usuario-cadastro-success}")
    private String usuarioCadastroSuccessMq;

    @Value("${app-config.queue.usuario-cadastro-failure}")
    private String usuarioCadastroFailureMq;

    @Value("${app-config.queue.usuario-alterar-email}")
    private String usuarioAlterarEmailMq;

    @Value("${app-config.queue.usuario-alterar-cargo}")
    private String usuarioAlterarCargoMq;

    @Value("${app-config.queue.usuario-ativar}")
    private String usuarioAtivarMq;

    @Value("${app-config.queue.usuario-inativar}")
    private String usuarioInativarMq;

    @Value("${app-config.queue.usuario-alterar-senha}")
    private String usuarioAlterarSenhaMq;

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public TopicExchange topic() {
        return new TopicExchange(autenticacaoTopic);
    }

    @Bean
    Queue usuarioCadastroMq() {
        return new Queue(usuarioCadastroMq, false);
    }

    @Bean
    Queue usuarioCadastroSuccessMq() {
        return new Queue(usuarioCadastroSuccessMq, false);
    }

    @Bean
    Queue usuarioCadastroFailureMq() {
        return new Queue(usuarioCadastroFailureMq, false);
    }

    @Bean
    Queue usuarioAlterarEmailMq() {
        return new Queue(usuarioAlterarEmailMq, false);
    }

    @Bean
    Queue usuarioAlterarCargoMq() {
        return new Queue(usuarioAlterarCargoMq, false);
    }

    @Bean
    Queue usuarioAtivarMq() {
        return new Queue(usuarioAtivarMq, false);
    }

    @Bean
    Queue usuarioInativarMq() {
        return new Queue(usuarioInativarMq, false);
    }

    @Bean
    Queue usuarioAlterarSenhaMq() {
        return new Queue(usuarioAlterarSenhaMq, false);
    }

    @Bean
    public Binding usuarioCadastroBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioCadastroMq()).to(exchange).with(usuarioCadastroMq);
    }

    @Bean
    public Binding usuarioCadastroSuccessBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioCadastroSuccessMq()).to(exchange).with(usuarioCadastroSuccessMq);
    }

    @Bean
    public Binding usuarioCadastroSuccessFailureBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioCadastroFailureMq()).to(exchange).with(usuarioCadastroFailureMq);
    }

    @Bean
    public Binding usuarioAlterarEmailBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioAlterarEmailMq()).to(exchange).with(usuarioAlterarEmailMq);
    }

    @Bean
    public Binding usuarioAlterarCargoBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioAlterarCargoMq()).to(exchange).with(usuarioAlterarCargoMq);
    }

    @Bean
    public Binding usuarioAtivarBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioAtivarMq()).to(exchange).with(usuarioAtivarMq);
    }

    @Bean
    public Binding usuarioInativarBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioInativarMq()).to(exchange).with(usuarioInativarMq);
    }

    @Bean
    public Binding usuarioAlterarSenhaBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioAlterarSenhaMq()).to(exchange).with(usuarioAlterarSenhaMq);
    }
}
