package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.dto.SubClusterDto;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParceirosOnlineService {

    private final ParceirosOnlineClient client;
    private final AutenticacaoService autenticacaoService;

    public List<ClusterDto> getClusters(Integer grupoId) {
        if (!isUsuarioComCanalAa()) {
            return List.of();
        }
        try {
            return client.getClusters(grupoId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                ParceirosOnlineService.class.getName(),
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
            return client.getGrupos(regionalId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                ParceirosOnlineService.class.getName(),
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
            return client.getRegionais();
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                ParceirosOnlineService.class.getName(),
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
            return client.getSubclusters(clusterId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                ParceirosOnlineService.class.getName(),
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
            return client.getCidades(subclusterId);
        } catch (RetryableException ex) {
            throw new IntegracaoException(ex,
                ParceirosOnlineService.class.getName(),
                EErrors.ERRO_OBTER_CIDADE_DO_POL);
        } catch (HystrixBadRequestException ex) {
            throw new IntegracaoException(ex);
        }
    }

    public boolean isUsuarioComCanalAa() {
        return autenticacaoService.getUsuarioAutenticado().haveCanalAgenteAutorizado();
    }
}
