package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client.ColaboradorVendasClient;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradorVendasService {

    private final ColaboradorVendasClient colaboradorVendasClient;

    public void limparCpfColaboradorVendas(String email) {
        try {
            colaboradorVendasClient.limparCpfColaboradorVendas(email);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                ColaboradorVendasService.class.getName(),
                EErrors.ERRO_AO_LIMPAR_CPF_COLABORADOR);
        }
    }

    public List<Integer> getUsuariosAaFeederPorCargo(List<Integer> aaIds, List<CodigoCargo> cargos) {
        try {
            return colaboradorVendasClient.getUsuariosAaFeederPorCargo(aaIds, cargos);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                ColaboradorVendasService.class.getName(),
                EErrors.ERRO_BUSCAR_AAS_FEEDER_POR_CARGO);
        }
    }
}
