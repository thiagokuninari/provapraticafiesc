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

    @Value("${app-config.queue.usuario-atualizacao}")
    private String usuarioAtualizacaoMq;

    @Value("${app-config.queue.usuario-atualizacao-failure}")
    private String usuarioAtualizacaoFailureMq;

    @Value("${app-config.queue.usuario-aa-atualizacao}")
    private String usuarioAaAtualizacaoMq;

    @Value("${app-config.queue.usuario-aa-atualizacao-failure}")
    private String usuarioAaAtualizacaoFailureMq;

    @Value("${app-config.queue.usuario-recuperacao}")
    private String usuarioRecuperacaoMq;

    @Value("${app-config.queue.usuario-recuperacao-failure}")
    private String usuarioRecuperacaoFailureMq;

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

    @Value("${app-config.queue.usuario-alterar-situacao}")
    private String usuarioAlterarSituacaoMq;

    @Value("${app-config.queue.atualizar-usuario-pol}")
    private String atualizarUsuarioPOlMq;

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
    Queue atualizarUsuarioPOlMq() {
        return new Queue(atualizarUsuarioPOlMq, false);
    }

    @Bean
    Queue usuarioCadastroSuccessMq() {
        return new Queue(usuarioCadastroSuccessMq, false);
    }

    @Bean
    Queue usuarioAtualizacaoMq() {
        return new Queue(usuarioAtualizacaoMq, false);
    }

    @Bean
    Queue usuarioAtualizacaoFailureMq() {
        return new Queue(usuarioAtualizacaoFailureMq, false);
    }

    @Bean
    Queue usuarioAaAtualizacaoMq() {
        return new Queue(usuarioAaAtualizacaoMq, false);
    }

    @Bean
    Queue usuarioAaAtualizacaoFailureMq() {
        return new Queue(usuarioAaAtualizacaoFailureMq, false);
    }

    @Bean
    Queue usuarioRecuperacaoMq() {
        return new Queue(usuarioRecuperacaoMq, false);
    }

    @Bean
    Queue usuarioRecuperacaoFailureMq() {
        return new Queue(usuarioRecuperacaoFailureMq, false);
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
    Queue usuarioAlterarSituacaoMq() {
        return new Queue(usuarioAlterarSituacaoMq, false);
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
    public Binding usuarioAtualizcaoBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioAtualizacaoMq()).to(exchange).with(usuarioAtualizacaoMq);
    }

    @Bean
    public Binding usuarioAtualizacaoFailureBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioAtualizacaoFailureMq()).to(exchange).with(usuarioAtualizacaoFailureMq);
    }

    @Bean
    public Binding usuarioAaAtualizcaoBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioAaAtualizacaoMq()).to(exchange).with(usuarioAaAtualizacaoMq);
    }

    @Bean
    public Binding usuarioAaAtualizacaoFailureBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioAaAtualizacaoFailureMq()).to(exchange).with(usuarioAaAtualizacaoFailureMq);
    }

    @Bean
    public Binding usuarioRecuperacaoBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioRecuperacaoMq()).to(exchange).with(usuarioRecuperacaoMq);
    }

    @Bean
    public Binding usuarioRecuperacaoFailureBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioRecuperacaoFailureMq()).to(exchange).with(usuarioRecuperacaoFailureMq);
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

    @Bean
    public Binding usuarioAlterarSituacaoBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioAlterarSituacaoMq()).to(exchange).with(usuarioAlterarSituacaoMq);
    }

    @Bean
    public Binding atualizarUsuarioPolBinding(TopicExchange exchange) {
        return BindingBuilder.bind(atualizarUsuarioPOlMq()).to(exchange).with(usuarioAlterarSituacaoMq);
    }
}
