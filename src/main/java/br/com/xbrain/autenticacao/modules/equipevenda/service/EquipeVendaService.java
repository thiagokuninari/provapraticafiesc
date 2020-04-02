package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioRequest;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @HystrixCommand(fallbackMethod = "getUsuariosPermitidosOnError")
    public List<EquipeVendaUsuarioResponse> getUsuariosPermitidos(List<CodigoCargo> cargos) {
        try {
            return equipeVendaClient.getUsuariosPermitidos(cargos);
        } catch (Exception ex) {
            throw new IntegracaoException(ex, EquipeVendaService.class.getName(),
                EErrors.ERRO_OBTER_EQUIPE_VENDAS_USUARIOS_PERMITIDOS);
        }
    }

    @SuppressWarnings({"PMD.UnusedPrivateMethod", "PMD.UnusedFormalParameter"})
    private List<EquipeVendaUsuarioResponse> getUsuariosPermitidosOnError(List<CodigoCargo> cargos) {
        return Collections.emptyList();
    }
}
