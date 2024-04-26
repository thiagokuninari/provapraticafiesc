package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioRequest;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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

    public void updateEquipeVendasUsuario(@RequestBody EquipeVendaUsuarioRequest request) {
        try {
            equipeVendasUsuarioClient.updateEquipeVendasUsuario(request);
        } catch (HystrixBadRequestException | RetryableException ex) {
            throw new IntegracaoException(ex, EquipeVendasUsuarioService.class.getName(),
                EErrors.ERRO_ATUALIZAR_EQUIPE_VENDAS_USUARIO);
        }
    }
}
