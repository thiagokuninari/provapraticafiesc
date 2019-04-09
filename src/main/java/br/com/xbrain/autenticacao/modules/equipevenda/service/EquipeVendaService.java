package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @SuppressWarnings({"PMD.UnusedFormalParameter", "PMD.UnusedPrivateMethod"})
    private boolean verificaPausaEmAndamentoOnError(String username) {
        return false;
    }

    public void inativarSupervisor(Integer usuarioId) {
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

    @HystrixCommand(fallbackMethod = "getEquipeVendasOnError")
    public List<EquipeVendaDto> getEquipeVendas(Integer id) {
        try {
            EquipeVendaUsuarioRequest request = EquipeVendaUsuarioRequest.builder()
                    .usuarioId(id)
                    .build();
            Map map = new ObjectMapper().convertValue(request, Map.class);
            return equipeVendaClient.getUsuario(map);
        } catch (Exception ex) {
            throw new IntegracaoException(ex, EquipeVendaService.class.getName(), EErrors.ERRO_OBTER_EQUIPE_VENDAS_USUARIO);
        }
    }

    @SuppressWarnings({"PMD.UnusedFormalParameter", "PMD.UnusedPrivateMethod"})
    private List<EquipeVendaDto> getEquipeVendasOnError(Integer id) {
        return Collections.emptyList();
    }
}
