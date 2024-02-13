package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client.ColaboradorVendasClient;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioRemanejamentoRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColaboradorVendasService {

    private final ColaboradorVendasClient client;

    public void limparCpfColaboradorVendas(String email) {
        try {
            client.limparCpfColaboradorVendas(email);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                ColaboradorVendasService.class.getName(),
                EErrors.ERRO_AO_LIMPAR_CPF_COLABORADOR);
        }
    }

    public List<Integer> getUsuariosAaFeederPorCargo(List<Integer> aaIds, List<CodigoCargo> cargos) {
        try {
            return client.getUsuariosAaFeederPorCargo(aaIds, cargos);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                ColaboradorVendasService.class.getName(),
                EErrors.ERRO_BUSCAR_AAS_FEEDER_POR_CARGO);
        }
    }

    public void atualizarUsuarioRemanejado(UsuarioRemanejamentoRequest request) {
        try {
            log.info("Atualizando colaborador remanejado no Parceiros Online.");
            client.atualizarUsuarioRemanejado(request);
            log.info("Colaborador atualizado com sucesso.");
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                ColaboradorVendasService.class.getName(),
                EErrors.ERRO_REMANEJAR_COLABORADOR_POL
            );
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

}
