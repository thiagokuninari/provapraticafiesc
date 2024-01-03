package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.dto.SubClusterDto;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgenteAutorizadoComunicadosFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.PublicoAlvoComunicadoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AgenteAutorizadoService {

    @Autowired
    private AgenteAutorizadoClient agenteAutorizadoClient;
    @Autowired
    private AutenticacaoService autenticacaoService;

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

    public List<Integer> getIdsUsuariosPermitidosDoUsuario(PublicoAlvoComunicadoFiltros filtros) {
        try {
            var request = new ObjectMapper().convertValue(AgenteAutorizadoComunicadosFiltros.of(filtros), Map.class);
            return agenteAutorizadoClient.getIdsUsuariosPermitidosDoUsuario(request);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_COLABORADORES_DO_AA);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<Integer> getUsuariosIdsSuperioresPol() {
        try {
            return agenteAutorizadoClient.getUsuariosIdsSuperioresPol();
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_COLABORADORES_DO_AA);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<ClusterDto> getClusters(Integer grupoId) {
        if (!isUsuarioComCanalAa()) {
            return List.of();
        }
        try {
            return agenteAutorizadoClient.getClusters(grupoId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_CIDADE_DO_POL);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<GrupoDto> getGrupos(Integer regionalId) {
        if (!isUsuarioComCanalAa()) {
            return List.of();
        }
        try {
            return agenteAutorizadoClient.getGrupos(regionalId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_CIDADE_DO_POL);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<RegionalDto> getRegionais() {
        if (!isUsuarioComCanalAa()) {
            return List.of();
        }
        try {
            return agenteAutorizadoClient.getRegionais();
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_CIDADE_DO_POL);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<SubClusterDto> getSubclusters(Integer clusterId) {
        if (!isUsuarioComCanalAa()) {
            return List.of();
        }
        try {
            return agenteAutorizadoClient.getSubclusters(clusterId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_CIDADE_DO_POL);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public List<UsuarioCidadeDto> getCidades(Integer subclusterId) {
        if (!isUsuarioComCanalAa()) {
            return List.of();
        }
        try {
            return agenteAutorizadoClient.getCidades(subclusterId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_OBTER_CIDADE_DO_POL);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public boolean isUsuarioComCanalAa() {
        return autenticacaoService.getUsuarioAutenticado().haveCanalAgenteAutorizado();
    }

    public List<Integer> getUsuariosAaFeederPorCargo(List<Integer> aaIds, List<CodigoCargo> cargos) {
        try {
            return agenteAutorizadoClient.getUsuariosAaFeederPorCargo(aaIds, cargos);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_BUSCAR_AAS_FEEDER_POR_CARGO);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public void atualizarEmailSocioPrincipalInativo(String emailAtual, String emailInativo, Integer idSocioPrincipal) {
        try {
            agenteAutorizadoClient.atualizarEmailSocioPrincipalInativo(emailAtual, emailInativo, idSocioPrincipal);
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
            agenteAutorizadoClient.inativarAntigoSocioPrincipal(email);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                AgenteAutorizadoService.class.getName(),
                EErrors.ERRO_SOCIO_NAO_INATIVADO_NO_POL);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }
}
