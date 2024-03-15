package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EquipeVendasUsuarioService {

    private final EquipeVendasUsuarioClient equipeVendasUsuarioClient;

    public List<EquipeVendaUsuarioResponse> getAll(Map<String, Object> filtros) {
        return equipeVendasUsuarioClient.getAll(filtros);
    }

    public List<Integer> buscarUsuarioEquipeVendasPorId(Integer id) {
        try {
            return equipeVendasUsuarioClient.buscarUsuarioPorId(id);
        } catch (HystrixBadRequestException | RetryableException ex) {
            throw new IntegracaoException(ex, EquipeVendasUsuarioService.class.getName(),
                EErrors.ERRO_OBTER_EQUIPE_USUARIO_SERVICE);
        }
    }
}
