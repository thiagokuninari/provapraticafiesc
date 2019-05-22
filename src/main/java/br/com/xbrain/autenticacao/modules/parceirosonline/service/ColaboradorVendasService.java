package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisorResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColaboradorVendasService {
    private final ColaboradorVendasClient colaboradorVendasClient;

    public Optional<EquipeVendasSupervisorResponse> getEquipeVendasSupervisorDoUsuarioId(Integer usuarioId) {
        try {
            return Optional.ofNullable(colaboradorVendasClient.getEquipeVendasSupervisorDoUsuarioId(usuarioId));
        } catch (RetryableException | HystrixBadRequestException ex) {
            return Optional.empty();
        }
    }
}
