package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioFiltros;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaSupervisorDto;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioRequest;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EquipeVendaD2dService {

    @Autowired
    private EquipeVendaD2dClient equipeVendaD2dClient;

    @HystrixCommand(fallbackMethod = "verificaPausaEmAndamentoOnError")
    public boolean verificaPausaEmAndamento(String username) {
        try {
            return equipeVendaD2dClient.verificarPausaEmAndamento(username);
        } catch (Exception ex) {
            throw new IntegracaoException(ex, EquipeVendaD2dService.class.getName(), EErrors.ERRO_VERIFICAR_PAUSA);
        }
    }

    @SuppressWarnings({"PMD.UnusedFormalParameter", "PMD.UnusedPrivateMethod"})
    private boolean verificaPausaEmAndamentoOnError(String username) {
        return false;
    }

    @HystrixCommand(fallbackMethod = "getEquipeVendasOnError")
    public List<EquipeVendaDto> getEquipeVendas(Integer id) {
        try {
            EquipeVendaUsuarioRequest request = EquipeVendaUsuarioRequest.builder()
                .usuarioId(id)
                .build();
            Map map = new ObjectMapper().convertValue(request, Map.class);
            return equipeVendaD2dClient.getUsuario(map);
        } catch (Exception ex) {
            throw new IntegracaoException(ex, EquipeVendaD2dService.class.getName(), EErrors.ERRO_OBTER_EQUIPE_VENDAS_USUARIO);
        }
    }

    @SuppressWarnings({"PMD.UnusedFormalParameter", "PMD.UnusedPrivateMethod"})
    private List<EquipeVendaDto> getEquipeVendasOnError(Integer id) {
        return Collections.emptyList();
    }

    public List<EquipeVendaSupervisorDto> getEquipeVendasComSupervisor(Integer id) {
        try {
            var request = EquipeVendaUsuarioRequest.builder()
                .usuarioId(id)
                .build();
            var map = new ObjectMapper().convertValue(request, Map.class);
            return equipeVendaD2dClient.getUsuarioComSupervisor(map);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex, EquipeVendaD2dService.class.getName(), EErrors.ERRO_OBTER_EQUIPE_VENDAS_USUARIO);
        }
    }

    @HystrixCommand(fallbackMethod = "getUsuariosPermitidosOnError")
    public List<EquipeVendaUsuarioResponse> getUsuariosPermitidos(List<CodigoCargo> cargos) {
        try {
            return equipeVendaD2dClient.getUsuariosPermitidos(cargos);
        } catch (Exception ex) {
            throw new IntegracaoException(ex, EquipeVendaD2dService.class.getName(),
                EErrors.ERRO_OBTER_EQUIPE_VENDAS_USUARIOS_PERMITIDOS);
        }
    }

    @SuppressWarnings({"PMD.UnusedPrivateMethod", "PMD.UnusedFormalParameter"})
    private List<EquipeVendaUsuarioResponse> getUsuariosPermitidosOnError(List<CodigoCargo> cargos) {
        return Collections.emptyList();
    }

    public List<Integer> getVendedoresPorEquipe(List<Integer> equipesIds) {
        try {
            return equipeVendaD2dClient.getVendedoresPorEquipe(EquipeVendaUsuarioFiltros.builder()
                .equipeVendaIds(equipesIds)
                .ativo(Boolean.TRUE)
                .build()
                .toMap()).stream()
                .map(SelectResponse::getValueInt)
                .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Erro ao tentar recuperar usuários da equipe.", ex);
            return List.of();
        }
    }

    public List<UsuarioResponse> filtrarUsuariosQuePodemAderirAEquipe(List<UsuarioResponse> vendedores, Integer equipeId) {
        try {
            var usuarioIdsComEquipes = equipeVendaD2dClient.filtrarUsuariosComEquipeByUsuarioIdInOuNaEquipe(
                vendedores.stream()
                    .map(UsuarioResponse::getId)
                    .collect(Collectors.toList()), equipeId);
            return vendedores.stream()
                .filter(vendedor -> usuarioIdsComEquipes.contains(vendedor.getId()))
                .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Erro ao recuperar vendedores sem equipe", ex);
            return List.of();
        }
    }

    public List<Integer> getUsuariosDaEquipe(List<Integer> equipesVendasId) {
        try {
            return equipeVendaD2dClient.getUsuariosDaEquipe(EquipeVendaUsuarioFiltros.builder()
                .equipeVendaIds(equipesVendasId)
                .ativo(Boolean.TRUE)
                .build()
                .toMap()).stream()
                .map(SelectResponse::getValueInt)
                .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Erro ao tentar recuperar usuários da equipe.", ex);
            return List.of();
        }
    }

    public List<Integer> getEquipeVendasBySupervisorId(Integer supervisorId) {
        try {
            return equipeVendaD2dClient.getEquipeVendaBySupervisorId(supervisorId);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex, EquipeVendaD2dService.class.getName(), EErrors.ERRO_OBTER_EQUIPE_VENDAS_USUARIO);
        }
    }

    public List<Integer> getSubCanaisDaEquipeVendaD2dByUsuarioId(Integer usuarioId) {
        try {
            return equipeVendaD2dClient.getSubCanaisDaEquipeVendaD2dByUsuarioId(usuarioId);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex, EquipeVendaD2dService.class.getName(),
                EErrors.ERRO_OBTER_SUBCANAIS_USUARIO_DO_EQUIPE_VENDAS);
        }
    }
}
