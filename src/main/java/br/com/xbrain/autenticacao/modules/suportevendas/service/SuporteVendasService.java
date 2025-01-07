package br.com.xbrain.autenticacao.modules.suportevendas.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.suportevendas.client.SuporteVendasClient;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static br.com.xbrain.autenticacao.modules.comum.util.StreamUtils.mapNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuporteVendasService {

    private final SuporteVendasClient client;

    public void desvincularGruposByUsuario(Usuario usuarioAntigo, Usuario usuarioAtualizado) {
        if (usuarioAntigo.isOperadorSuporteVendas() && houveAlteracaoDeCargoOuOrganizacao(usuarioAntigo, usuarioAtualizado)) {
            try {
                client.desvincularGruposByUsuarioId(usuarioAtualizado.getId());
            } catch (RetryableException ex) {
                throw new IntegracaoException(ex,
                    SuporteVendasService.class.getName(),
                    "Ocorreu um erro ao desvincular grupo do usuário no suporte-vendas.");
            } catch (HystrixBadRequestException ex) {
                throw new IntegracaoException(ex);
            }
        }
    }

    private boolean houveAlteracaoDeCargoOuOrganizacao(Usuario usuarioAntigo, Usuario usuarioAtualizado) {
        return mapNull(usuarioAtualizado.getCargoId(), id -> !id.equals(usuarioAntigo.getCargoId()), false)
            || mapNull(usuarioAtualizado.getOrganizacaoId(), id -> !id.equals(usuarioAntigo.getOrganizacaoId()), false);
    }
}
