package br.com.xbrain.autenticacao.modules.claroindico.service;

import br.com.xbrain.autenticacao.modules.claroindico.client.ClaroIndicoClient;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.CARGOS_IDS_COLABORADOR_BKO_CENTRALIZADO;
import static br.com.xbrain.autenticacao.modules.comum.util.StreamUtils.mapNull;

@Service
@RequiredArgsConstructor
public class ClaroIndicoService {

    private final ClaroIndicoClient client;

    public List<Integer> buscarUsuariosVinculados() {
        try {
            return client.buscarUsuariosVinculados();
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                ClaroIndicoService.class.getName(),
                "Ocorreu um erro ao buscar usuários vinculados às filas de tratamento.");
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public void desvincularUsuarioDaFilaTratamento(Integer id) {
        try {
            client.desvincularUsuarioDaFilaTratamento(id);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                ClaroIndicoService.class.getName(),
                "Ocorreu um erro ao desvincular usuário da fila de tratamento.");
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public void desvincularUsuarioDaFilaTratamento(Usuario usuarioAntigo, Usuario usuarioAtualizado) {
        if (usuarioAntigo.isNivelBkoCentralizado() && houveAlteracao(usuarioAntigo, usuarioAtualizado)) {
            desvincularUsuarioDaFilaTratamento(usuarioAtualizado.getId());
        }
    }

    private boolean houveAlteracao(Usuario usuarioAntigo, Usuario usuarioAtualizado) {
        return !usuarioAtualizado.isNivelBkoCentralizado()
            || mapNull(usuarioAtualizado.getCargoId(), id -> !id.equals(usuarioAntigo.getCargoId()), false)
                && CARGOS_IDS_COLABORADOR_BKO_CENTRALIZADO.contains(usuarioAntigo.getCargoId())
                && !CARGOS_IDS_COLABORADOR_BKO_CENTRALIZADO.contains(usuarioAtualizado.getCargoId());
    }
}
