package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.usuarioacesso.client.NotificacaoUsuarioAcessoClient;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoResponse;
import com.google.common.collect.Maps;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@Slf4j
public class NotificacaoUsuarioAcessoService {

    @Autowired
    private NotificacaoUsuarioAcessoClient client;

    public List<PaLogadoResponse> countUsuariosLogadosPorHora(List<Integer> usuariosIds, LocalDateTime dataInicio,
                                                              LocalDateTime dataFim) {
        try {
            if (isEmpty(usuariosIds)) {
                return List.of();
            }
            return client.countUsuariosLogadosPorHora(criarUsuariosLogadosRequestMap(usuariosIds, dataInicio, dataFim));
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                NotificacaoUsuarioAcessoService.class.getName(),
                EErrors.ERRO_OBTER_USUARIOS_LOGADOS_POR_HORA);
        }
    }

    private Map<String, Object> criarUsuariosLogadosRequestMap(List<Integer> usuariosIds, LocalDateTime dataInicio,
                                                               LocalDateTime dataFim) {
        var map = Maps.<String, Object>newHashMap();
        map.put("usuariosIds", usuariosIds.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(",")));
        map.put("dataInicio", DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_T, dataInicio));
        map.put("dataFim", DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_T, dataFim));
        return map;
    }
}
