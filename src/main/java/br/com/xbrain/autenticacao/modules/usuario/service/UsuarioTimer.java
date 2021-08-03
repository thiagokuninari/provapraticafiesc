package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.service.DeslogarUsuarioPorExcessoDeUsoService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.UsuarioAcessoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Profile("!test")
@Slf4j
public class UsuarioTimer {

    @Autowired
    private UsuarioService service;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private UsuarioAcessoService usuarioAcessoService;
    @Autowired
    private DeslogarUsuarioPorExcessoDeUsoService deslogarUsuarioPorExcessoDeUsoService;

    private static final String EVERY_DAY_AT_THREE_AM = "0 0 3 * * *";

    private static final String EVERY_DAY_AT_TWO_AM = "0 0 2 * * *";

    private static final String EVERY_DAY_AT_FOUR_AM = "0 0 4 * * *";

    private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * *";

    private static final String EVERY_30_MINUTE = "0 */30 * * * *";

    private static final String TIME_ZONE = "America/Sao_Paulo";

    private static final String ORIGEM = "Usuário inativado pelo processo automático";

    @Scheduled(cron = EVERY_DAY_AT_TWO_AM)
    @Async
    public void inativarUsuariosSemAcesso() {
        Integer usuariosInativados = usuarioAcessoService.inativarUsuariosSemAcesso(ORIGEM);
        log.info("Usuários inativados: {}", usuariosInativados);
    }

    @Scheduled(cron = EVERY_DAY_AT_FOUR_AM)
    @Async
    public void deletarHistoricoUsuarioAcesso() {
        long contadorLinhasDeletadas = usuarioAcessoService.deletarHistoricoUsuarioAcesso();
        log.info("Total de histórico(s) removido da tabela USUARIO_ACESSO foi de : " + contadorLinhasDeletadas);
    }

    @Transactional
    @Async
    @Scheduled(cron = EVERY_DAY_AT_MIDNIGHT, zone = TIME_ZONE)
    public void deslogarTodosOsUsuarios() {
        autenticacaoService.logoutAllUsers();
    }

    @Scheduled(cron = EVERY_DAY_AT_THREE_AM)
    public void reativarUsuariosComFeriasComTerminoFinalizado() {
        service.reativarUsuariosInativosComFeriasTerminando(LocalDate.now().minusDays(1));
    }

    @Scheduled(cron = EVERY_DAY_AT_THREE_AM)
    public void reativarUsuariosComAfastamentoComTerminoFinalizado() {
        service.reativarUsuariosInativosComAfastamentoTerminando(LocalDate.now());
    }

    @Scheduled(cron = EVERY_30_MINUTE)
    public void deslogarUsuariosInativadosPorExcessoDeUsoDeApi() {
        deslogarUsuarioPorExcessoDeUsoService.deslogarUsuariosInativados();
    }
}
