package br.com.xbrain.autenticacao.modules.agenteautorizado.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.client.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.AgenteAutorizadoFiltros;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoRequest;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgenteAutorizadoUsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.PublicoAlvoComunicadoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.AGENTE_AUTORIZADO;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgenteAutorizadoService {

    private final AgenteAutorizadoClient client;
    private final AutenticacaoService autenticacaoService;

    public List<UsuarioDtoVendas> buscarTodosUsuariosDosAas(List<Integer> aasIds, Boolean buscarInativos) {
        try {
            return client.buscarTodosUsuariosDosAas(aasIds, buscarInativos);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_BUSCAR_TODOS_USUARIOS_DOS_AAS);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public Set<Integer> getIdsUsuariosSubordinados(boolean incluirProprio) {
        try {
            var idsUsuarios = client.getIdUsuariosDoUsuario(Map.of());
            return Stream.concat(
                idsUsuarios.stream(),
                Optional.ofNullable(incluirProprio ? autenticacaoService.getUsuarioId() : null).stream()
            ).collect(Collectors.toSet());
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_IDS_USUARIOS_SUBORDINADOS);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<Integer> getIdsUsuariosSubordinadosByFiltros(PublicoAlvoComunicadoFiltros filtros) {
        try {
            var request = new ObjectMapper().convertValue(AgenteAutorizadoFiltros.of(filtros), Map.class);
            return client.getIdsUsuariosPermitidosDoUsuario(request);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_IDS_USUARIOS_SUBORDINADOS);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public AgenteAutorizadoResponse getAaByCpnj(String cnpj) {
        try {
            var request = new AgenteAutorizadoRequest();
            request.setCnpj(cnpj);
            var map = new ObjectMapper().convertValue(request, Map.class);
            return client.getAaByCpnj(map);
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
            return client.getAaById(idAgenteAutorizado);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_AA_BY_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<Empresa> getEmpresasPermitidas(int usuarioId) {
        try {
            return client
                .getEmpresasPermitidas(usuarioId)
                .stream()
                .map(EmpresaResponse::convertTo)
                .collect(Collectors.toList());

        } catch (Exception ex) {
            log.warn("Erro ao consultar empresas do usuário", ex);
            return Collections.emptyList();
        }
    }

    public String getEstruturaByUsuarioIdAndAtivo(Integer usuarioId) {
        try {
            return client.getEstruturaByUsuarioIdAndAtivo(usuarioId);
        } catch (Exception ex) {
            log.warn("Erro ao consultar a estrutura do AA", ex);
            return null;
        }
    }

    public String getEstruturaByUsuarioId(Integer usuarioId) {
        try {
            return client.getEstruturaByUsuarioId(usuarioId);
        } catch (Exception ex) {
            log.warn("Erro ao consultar a estrutura do AA", ex);
            return null;
        }
    }

    public boolean existeAaAtivoBySocioEmail(String usuarioEmail) {
        try {
            return client.existeAaAtivoBySocioEmail(usuarioEmail);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(
                ex.getCause(), AgenteAutorizadoService.class.getName(), EErrors.ERRO_OBTER_AA);
        }
    }

    public boolean existeAaAtivoByUsuarioId(Integer usuarioId) {
        try {
            return client.existeAaAtivoByUsuarioId(usuarioId);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(
                ex.getCause(), AgenteAutorizadoService.class.getName(), EErrors.ERRO_OBTER_AA);
        }
    }

    public List<Integer> getAasPermitidos(int usuarioId) {
        try {
            return client.getAasPermitidos(usuarioId);
        } catch (Exception ex) {
            log.warn("Erro ao consultar agentes autorizados do usuário", ex);
            return Collections.emptyList();
        }
    }

    public List<Integer> getAgentesAutorizadosPermitidos(Usuario usuario) {
        return usuario.getNivelCodigo() == AGENTE_AUTORIZADO
            ? getAasPermitidos(usuario.getId())
            : Collections.emptyList();
    }

    public List<Integer> getIdUsuariosPorAa(String cnpj, Boolean buscarInativos) {
        try {
            var aaResponse = getAaByCpnj(cnpj);
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

    public List<UsuarioAgenteAutorizadoResponse> getUsuariosByAaId(Integer aaId, Boolean buscarInativos) {
        try {
            return client.getUsuariosByAaId(aaId, buscarInativos);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_USUARIOS_AA_BY_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<Integer> getUsuariosIdsByAaId(Integer aaId, Boolean buscarInativos) {
        return getUsuariosByAaId(aaId, buscarInativos).stream()
            .map(UsuarioAgenteAutorizadoResponse::getId)
            .distinct()
            .collect(Collectors.toList());
    }

    public List<AgenteAutorizadoUsuarioDto> getAgenteAutorizadosUsuarioDtosByUsuarioIds(UsuarioRequest request) {
        try {
            return client.getAgenteAutorizadosUsuarioDtosByUsuarioIds(request);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_AA_USUARIO_DTO_BY_USUARIO_ID);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<AgenteAutorizadoResponse> findAgenteAutorizadoByUsuarioId(Integer usuarioId) {
        try {
            return client.findAgenteAutorizadoByUsuarioId(usuarioId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_BUSCAR_TODOS_AAS_DO_USUARIO);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<AgenteAutorizadoResponse> findAgentesAutorizadosByUsuariosIds(List<Integer> usuariosIds,
                                                                              boolean incluirAasInativos) {
        try {
            return client.findAgentesAutorizadosByUsuariosIds(usuariosIds, incluirAasInativos);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_BUSCAR_TODOS_AAS_DO_USUARIO);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<UsuarioAgenteAutorizadoResponse> getUsuariosAaAtivoSemVendedoresD2D(Integer aaId) {
        try {
            return client.getUsuariosAaAtivoSemVendedoresD2D(aaId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_COLABORADORES_DO_AA);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<UsuarioAgenteAutorizadoResponse> getUsuariosAaAtivoComVendedoresD2D(Integer aaId) {
        try {
            return client.getUsuariosAaAtivoComVendedoresD2D(aaId);
        } catch (HystrixBadRequestException | RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_COLABORADORES_DO_AA);
        }
    }

    public List<String> recuperarColaboradoresDoAgenteAutorizado(String cnpj) {
        try {
            return client.recuperarColaboradoresDoAgenteAutorizado(cnpj);
        } catch (HystrixBadRequestException | RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_EMAILS_DOS_COLABORADORES_DO_AA);
        }
    }

    public List<UsuarioAgenteAutorizadoAgendamentoResponse> getUsuariosByAaIdCanalDoUsuario(Integer aaId, Integer usuarioId) {
        try {
            return client.getUsuariosByAaIdCanalDoUsuario(aaId, usuarioId);
        } catch (HystrixBadRequestException | RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_USUARIOS_AA_BY_ID);
        }
    }

    public List<Integer> getUsuariosIdsSuperioresPol() {
        try {
            return client.getUsuariosIdsSuperioresPol();
        } catch (HystrixBadRequestException | RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_COLABORADORES_DO_AA);
        }
    }

    public void atualizarEmailSocioPrincipalInativo(String emailAtual, String emailInativo, Integer socioPrincipalId) {
        try {
            client.atualizarEmailSocioPrincipalInativo(emailAtual, emailInativo, socioPrincipalId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_EMAIL_SOCIO_NAO_ATUALIZADO_NO_POL);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public void inativarAntigoSocioPrincipal(String email) {
        try {
            client.inativarAntigoSocioPrincipal(email);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_SOCIO_NAO_INATIVADO_NO_POL);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public void ativarUsuario(Integer usuarioId) {
        try {
            client.ativarUsuario(usuarioId);
        } catch (HystrixBadRequestException | RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_ATIVAR_USUARIO_AA);
        }
    }

    public void inativarUsuario(Integer usuarioId) {
        try {
            client.inativarUsuario(usuarioId);
        } catch (HystrixBadRequestException | RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_INATIVAR_USUARIO_AA);
        }
    }
}
