package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EquipeVendasUsuarioService {

    @Autowired
    private EquipeVendasUsuarioClient equipeVendasUsuarioClient;

    public List<EquipeVendaUsuarioResponse> getAll(Map<String, Object> filtros) {
        return equipeVendasUsuarioClient.getAll(filtros);
    }

    public List<Integer> buscarUsuarioEquipeVendasPorId(Integer id) {
        try {
            return equipeVendasUsuarioClient.buscarUsuarioPorId(id);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                EquipeVendasUsuarioService.class.getName(),
                EErrors.ERRO_OBTER_EQUIPE_USUARIO_SERVICE);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
