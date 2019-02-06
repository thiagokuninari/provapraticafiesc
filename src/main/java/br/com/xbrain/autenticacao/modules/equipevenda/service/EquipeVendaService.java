package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_INATIVAR_SUPERVISOR_EQUIPE_VENDA;
import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_INATIVAR_USUARIO_EQUIPE_VENDA;


@Service
public class EquipeVendaService {

    @Autowired
    private EquipeVendaClient equipeVendaClient;

    @HystrixCommand(fallbackMethod = "verificaPausaEmAndamentoOnError")
    public boolean verificaPausaEmAndamento(String username) {
        try {
            return equipeVendaClient.verificarPausaEmAndamento(username);
        } catch (Exception ex) {
            throw new IntegracaoException(ex, EquipeVendaService.class.getName(), EErrors.ERRO_VERIFICAR_PAUSA);
        }
    }

    @SuppressWarnings({"PMD.UnusedFormalParameter","PMD.UnusedPrivateMethod"})
    private boolean verificaPausaEmAndamentoOnError(String username) {
        return false;
    }

    public void inativarSupervidor(Integer usuarioId) {
        try {
            equipeVendaClient.inativarSupervidor(usuarioId);
        } catch (Exception ex) {
            throw new IntegracaoException(ex,
                    EquipeVendaService.class.getName(),
                    ERRO_INATIVAR_SUPERVISOR_EQUIPE_VENDA);
        }
    }

    public void inativarUsuario(Integer usuarioId) {
        try {
            equipeVendaClient.inativarUsuarioEquipe(usuarioId);
        } catch (Exception ex) {
            throw new IntegracaoException(ex,
                    EquipeVendaService.class.getName(),
                    ERRO_INATIVAR_USUARIO_EQUIPE_VENDA);
        }
    }
}
