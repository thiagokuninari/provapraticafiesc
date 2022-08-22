package br.com.xbrain.autenticacao.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    private static final String DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    @Value("${app-config.topic.autenticacao}")
    private String autenticacaoTopic;

    @Value("${app-config.queue.usuario-cadastro}")
    private String usuarioCadastroMq;

    @Value("${app-config.queue.usuario-cadastro-socio-principal-success}")
    private String usuarioCadastroSocioPrincipalSuccessMq;

    @Value("${app-config.queue.usuario-cadastro-loja-futuro-success}")
    private String usuarioCadastroLojaFuturoSuccessMq;

    @Value("${app-config.queue.usuario-cadastro-success}")
    private String usuarioCadastroSuccessMq;

    @Value("${app-config.queue.usuario-cadastro-failure}")
    private String usuarioCadastroFailureMq;

    @Value("${app-config.queue.usuario-atualizacao}")
    private String usuarioAtualizacaoMq;

    @Value("${app-config.queue.usuario-atualizacao-lojafuturo}")
    private String usuarioLojaFuturoAtualizacaoMq;

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

    @Value("${app-config.queue.inativar-usuario-equipe-venda}")
    private String inativarUsuarioEquipeVendaMq;

    @Value("${app-config.queue.inativar-colaborador-pol}")
    private String inativarColaboradorPolMq;

    @Value("${app-config.queue.usuario-logout}")
    private String usuarioLogoutMq;

    @Value("${app-config.queue.usuario-ultimo-acesso-pol}")
    private String usuarioUltimoAcessoPolMq;

    @Value("${app-config.queue.usuario-remanejar-pol}")
    private String usuarioRemanejarPol;

    @Value("${app-config.queue.usuario-remanejado-aut}")
    private String usuarioRemanejadoAut;

    @Value("${app-config.queue.usuario-remanejado-aut-failure}")
    private String usuarioRemanejadoAutFailure;

    @Value("${app-config.queue.atualizar-permissao-feeder}")
    private String atualizarPermissaoFeederMq;

    @Value("${app-config.queue.atualizar-permissao-feeder-failure}")
    private String atualizarPermissaoFeederFailureMq;

    @Value("${app-config.queue.sucesso-cadastro-usuario-feeder}")
    private String sucessoCadastroUsuarioFeederMq;

    @Value("${app-config.queue.sucesso-cadastro-usuario-feeder-failure}")
    private String sucessoCadastroUsuarioFeederFailureMq;

    @Value("${app-config.queue.cadastro-usuario-feeder}")
    private String cadastroUsuarioFeederMq;

    @Value("${app-config.queue.cadastro-usuario-feeder-failure}")
    private String cadastroUsuarioFeederFailureMq;

    @Value("${app-config.queue.alterar-situacao-usuario-feeder}")
    private String alterarSituacaoUsuarioFeederMq;

    @Value("${app-config.queue.alterar-situacao-usuario-feeder-failure}")
    private String alterarSituacaoUsuarioFeederFailureMq;

    @Value("${app-config.queue.usuario-inativacao-por-aa}")
    private String usuarioInativacaoPorAaMq;

    @Value("${app-config.queue.limpar-cpf-e-alterar-email-feeder}")
    private String usuarioLimparCpfEAlterarEmailUsuarioFeederMq;

    @Value("${app-config.queue.limpar-cpf-e-alterar-email-feeder-failure}")
    private String usuarioLimparCpfEAlterarEmailUsuarioFeederFailureMq;

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
    Queue atualizarPermissaoFeederMq() {
        return QueueBuilder
            .durable(atualizarPermissaoFeederMq)
            .withArgument(DEAD_LETTER_EXCHANGE, "")
            .withArgument(DEAD_LETTER_ROUTING_KEY, atualizarPermissaoFeederFailureMq)
            .build();
    }

    @Bean
    Queue atualizarPermissaoFeederFailureMq() {
        return QueueBuilder.durable(atualizarPermissaoFeederFailureMq).build();
    }

    @Bean
    Queue usuarioCadastroSocioPrincipalSuccessMq() {
        return new Queue(usuarioCadastroSocioPrincipalSuccessMq, false);
    }

    @Bean
    Queue usuarioCadastroLojaFuturoSuccessMq() {
        return new Queue(usuarioCadastroLojaFuturoSuccessMq, false);
    }

    @Bean
    Queue atualizarUsuarioPOlMq() {
        return new Queue(atualizarUsuarioPOlMq, false);
    }

    @Bean
    Queue inativaUsuarioEquipeVendaMq() {
        return new Queue(inativarUsuarioEquipeVendaMq, false);
    }

    @Bean
    Queue inativarColaboradorPolMq() {
        return new Queue(inativarColaboradorPolMq, false);
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
    Queue usuarioLojaFuturoAtualizacaoMq() {
        return new Queue(usuarioLojaFuturoAtualizacaoMq, false);
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
    Queue usuarioLogoutMq() {
        return new Queue(usuarioLogoutMq, false);
    }

    @Bean
    Queue usuarioUltimoAcessoPol() {
        return new Queue(usuarioUltimoAcessoPolMq, false);
    }

    @Bean
    Queue usuarioRemanejarPol() {
        return new Queue(usuarioRemanejarPol, false);
    }

    @Bean
    Queue usuarioRemanejadoAut() {
        return new Queue(usuarioRemanejadoAut, false);
    }

    @Bean
    Queue usuarioRemanejadoAutFailure() {
        return new Queue(usuarioRemanejadoAutFailure, false);
    }

    @Bean
    Queue usuarioLimparCpfEAlterarEmailUsuarioFeederMq() {
        return new Queue(usuarioLimparCpfEAlterarEmailUsuarioFeederMq, false);
    }

    @Bean
    Queue usuarioLimparCpfEAlterarEmailUsuarioFeederFailureMq() {
        return new Queue(usuarioLimparCpfEAlterarEmailUsuarioFeederFailureMq, false);
    }

    @Bean
    Queue sucessoCadastroUsuarioFeederMq() {
        return QueueBuilder
            .durable(sucessoCadastroUsuarioFeederMq)
            .withArgument(DEAD_LETTER_EXCHANGE, "")
            .withArgument(DEAD_LETTER_ROUTING_KEY, sucessoCadastroUsuarioFeederFailureMq)
            .build();
    }

    @Bean
    Queue sucessoCadastroUsuarioFeederFailureMq() {
        return QueueBuilder.durable(sucessoCadastroUsuarioFeederFailureMq).build();
    }

    @Bean
    Queue cadastroUsuarioFeederMq() {
        return QueueBuilder
            .durable(cadastroUsuarioFeederMq)
            .withArgument(DEAD_LETTER_EXCHANGE, "")
            .withArgument(DEAD_LETTER_ROUTING_KEY, cadastroUsuarioFeederFailureMq)
            .build();
    }

    @Bean
    Queue cadastroUsuarioFeederFailureMq() {
        return QueueBuilder.durable(cadastroUsuarioFeederFailureMq).build();
    }

    @Bean
    Queue alterarSituacaoUsuarioFeederMq() {
        return QueueBuilder
            .durable(alterarSituacaoUsuarioFeederMq)
            .withArgument(DEAD_LETTER_EXCHANGE, "")
            .withArgument(DEAD_LETTER_ROUTING_KEY, alterarSituacaoUsuarioFeederFailureMq)
            .build();
    }

    @Bean
    Queue alterarSituacaoUsuarioFeederFailureMq() {
        return QueueBuilder.durable(alterarSituacaoUsuarioFeederFailureMq).build();
    }

    @Bean
    Queue usuarioInativacaoPorAaMq() {
        return QueueBuilder.nonDurable(usuarioInativacaoPorAaMq).build();
    }

    @Bean
    public Binding usuarioCadastroBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioCadastroMq()).to(exchange).with(usuarioCadastroMq);
    }

    @Bean
    public Binding usuarioCadastroSocioPrincipalSuccessBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioCadastroSocioPrincipalSuccessMq()).to(exchange)
            .with(usuarioCadastroSocioPrincipalSuccessMq);
    }

    @Bean
    public Binding usuarioCadastroLojaFuturoSuccessBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioCadastroLojaFuturoSuccessMq()).to(exchange)
            .with(usuarioCadastroLojaFuturoSuccessMq);
    }

    @Bean
    public Binding atualizarPermissaoFeederBinding(TopicExchange exchange) {
        return BindingBuilder.bind(atualizarPermissaoFeederMq()).to(exchange).with(atualizarPermissaoFeederMq);
    }

    @Bean
    public Binding atualizarPermissaoFeederFailureBinding(TopicExchange exchange) {
        return BindingBuilder.bind(atualizarPermissaoFeederFailureMq())
            .to(exchange).with(atualizarPermissaoFeederFailureMq);
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
    public Binding usuarioLojaFuturoAtualizcaoBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioLojaFuturoAtualizacaoMq()).to(exchange).with(usuarioLojaFuturoAtualizacaoMq);
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

    @Bean
    public Binding inativaUsuarioBinding(TopicExchange exchange) {
        return BindingBuilder.bind(inativaUsuarioEquipeVendaMq()).to(exchange).with(usuarioAlterarSituacaoMq);
    }

    @Bean
    public Binding inativarColaboradorPolBinding(TopicExchange exchange) {
        return BindingBuilder.bind(inativarColaboradorPolMq()).to(exchange).with(inativarColaboradorPolMq);
    }

    @Bean
    public Binding usuarioLogoutBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioLogoutMq()).to(exchange).with(usuarioLogoutMq);
    }

    @Bean
    public Binding usuarioUltimoAcessoPolBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioUltimoAcessoPol()).to(exchange).with(usuarioUltimoAcessoPolMq);
    }

    @Bean
    public Binding usuarioRemanejarPolBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioRemanejarPol()).to(exchange).with(usuarioRemanejarPol);
    }

    @Bean
    public Binding usuarioRemanejadoAutBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioRemanejadoAut()).to(exchange).with(usuarioRemanejadoAut);
    }

    @Bean
    public Binding usuarioRemanejadoAutFailureBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioRemanejadoAutFailure()).to(exchange).with(usuarioRemanejadoAutFailure);
    }

    @Bean
    public Binding sucessoCadastroUsuarioFeederMqBinding(TopicExchange exchange) {
        return BindingBuilder.bind(sucessoCadastroUsuarioFeederMq())
            .to(exchange).with(sucessoCadastroUsuarioFeederMq);
    }

    @Bean
    public Binding sucessoCadastroUsuarioFeederMqFailureBinding(TopicExchange exchange) {
        return BindingBuilder.bind(sucessoCadastroUsuarioFeederFailureMq())
            .to(exchange)
            .with(sucessoCadastroUsuarioFeederFailureMq);
    }

    @Bean
    public Binding cadastroUsuarioFeederMqBinding(TopicExchange exchange) {
        return BindingBuilder.bind(cadastroUsuarioFeederMq()).to(exchange).with(cadastroUsuarioFeederMq);
    }

    @Bean
    public Binding cadastroUsuarioFeederMqFailureBinding(TopicExchange exchange) {
        return BindingBuilder.bind(cadastroUsuarioFeederFailureMq())
            .to(exchange)
            .with(cadastroUsuarioFeederFailureMq);
    }

    @Bean
    public Binding alterarSituacaoUsuarioFeederMqBinding(TopicExchange exchange) {
        return BindingBuilder.bind(alterarSituacaoUsuarioFeederMq())
            .to(exchange)
            .with(alterarSituacaoUsuarioFeederMq);
    }

    @Bean
    public Binding alterarSituacaoUsuarioFeederMqFailureBinding(TopicExchange exchange) {
        return BindingBuilder.bind(alterarSituacaoUsuarioFeederFailureMq())
            .to(exchange)
            .with(alterarSituacaoUsuarioFeederFailureMq);
    }

    @Bean
    public Binding usuarioInativacaoPorAaMqBinding(TopicExchange exchange) {
        return BindingBuilder.bind(usuarioInativacaoPorAaMq())
            .to(exchange)
            .with(usuarioInativacaoPorAaMq);
    }
}
