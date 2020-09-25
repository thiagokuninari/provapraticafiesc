package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.AGENTE_AUTORIZADO;

@Service
public class AgenteAutorizadoService {

    private final Logger logger = LoggerFactory.getLogger(AgenteAutorizadoService.class);

    @Autowired
    private AgenteAutorizadoClient agenteAutorizadoClient;

    public List<Integer> getIdUsuariosPorAa(String cnpj, Boolean buscarInativos) {
        try {
            AgenteAutorizadoResponse aaResponse = getAaByCpnj(cnpj);
            return getUsuariosByAaId(Integer.valueOf(aaResponse.getId()), buscarInativos).stream()
                    .map(UsuarioAgenteAutorizadoResponse::getId)
                    .collect(Collectors.toList());
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_AA_BY_CNPJ);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public AgenteAutorizadoResponse getAaById(Integer idAgenteAutorizado) {
        try {
            return agenteAutorizadoClient.getAaById(idAgenteAutorizado);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_AA_BY_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    private AgenteAutorizadoResponse getAaByCpnj(String cnpj) {
        try {
            AgenteAutorizadoRequest request = new AgenteAutorizadoRequest();
            request.setCnpj(cnpj);
            Map map = new ObjectMapper().convertValue(request, Map.class);
            return agenteAutorizadoClient.getAaByCpnj(map);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_AA_BY_CNPJ);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<UsuarioAgenteAutorizadoResponse> getUsuariosByAaId(Integer aaId, Boolean buscarInativos) {
        try {
            return agenteAutorizadoClient.getUsuariosByAaId(aaId, buscarInativos);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_USUARIOS_AA_BY_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<UsuarioAgenteAutorizadoResponse> getUsuariosAaAtivoComVendedoresD2D(Integer aaId) {
        try {
            return agenteAutorizadoClient.getUsuariosAaAtivoComVendedoresD2D(aaId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_COLABORADORES_DO_AA);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<UsuarioAgenteAutorizadoAgendamentoResponse> getUsuariosByAaIdCanalDoUsuario(Integer aaId, Integer usuarioId) {
        try {
            return agenteAutorizadoClient.getUsuariosByAaIdCanalDoUsuario(aaId, usuarioId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_USUARIOS_AA_BY_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<Integer> getAgentesAutorizadosPermitidos(Usuario usuario) {
        return usuario.getNivelCodigo() == AGENTE_AUTORIZADO
                ? getAasPermitidos(usuario.getId())
                : Collections.emptyList();
    }

    public List<AgenteAutorizadoPermitidoResponse> getAgentesAutorizadosPermitidos() {
        try {
            return agenteAutorizadoClient.getAgentesAutorizadosPermitidos();
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                    AgenteAutorizadoService.class.getName(),
                    EErrors.ERRO_OBTER_AA_BY_CNPJ);
        }
    }

    public List<Integer> getAasPermitidos(int usuarioId) {
        try {
            return agenteAutorizadoClient.getAasPermitidos(usuarioId);
        } catch (Exception ex) {
            logger.warn("Erro ao consultar agentes autorizados do usuário", ex);
            return Collections.emptyList();
        }
    }

    public List<Empresa> getEmpresasPermitidas(int usuarioId) {
        try {
            return agenteAutorizadoClient
                    .getEmpresasPermitidas(usuarioId)
                    .stream()
                    .map(EmpresaResponse::convertTo)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            logger.warn("Erro ao consultar empresas do usuário", ex);
            return Collections.emptyList();
        }
    }

    public String getEstrutura(Integer usuarioId) {
        try {
            return agenteAutorizadoClient.getEstrutura(usuarioId);
        } catch (Exception ex) {
            logger.warn("Erro ao consultar a estrutura do AA", ex);
            return null;
        }
    }

    public boolean existeAaAtivoBySocioEmail(String usuarioEmail) {
        try {
            return agenteAutorizadoClient.existeAaAtivoBySocioEmail(usuarioEmail);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(
                    ex.getCause(), AgenteAutorizadoService.class.getName(), EErrors.ERRO_OBTER_AA);
        }
    }

    public boolean existeAaAtivoByUsuarioId(Integer usuarioId) {
        try {
            return agenteAutorizadoClient.existeAaAtivoByUsuarioId(usuarioId);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(
                    ex.getCause(), AgenteAutorizadoService.class.getName(), EErrors.ERRO_OBTER_AA);
        }
    }
}
