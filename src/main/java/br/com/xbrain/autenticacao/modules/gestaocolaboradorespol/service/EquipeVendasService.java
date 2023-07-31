package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service;

import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client.EquipeVendasClient;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeVendasSupervisionadasResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipeVendasService {

    private final EquipeVendasClient client;

    public List<EquipeVendasSupervisionadasResponse> getEquipesPorSupervisor(Integer supervisorId) {
        try {
            return client.getEquipesPorSupervisor(supervisorId);
        } catch (Exception ex) {
            log.warn("Erro ao obter as equipes de venda do supervisor", ex);
            return Collections.emptyList();
        }
    }

    public EquipeVendaDto getByUsuario(Integer usuarioId) {
        try {
            return client.getByUsuario(usuarioId);
        } catch (Exception ex) {
            log.warn("Erro ao obter a equipe de venda do usuário", ex);
            return null;
        }
    }

    public Map<Integer, Integer> getUsuarioEEquipeByUsuarioIds(List<Integer> usuarioIds) {
        try {
            if (!usuarioIds.isEmpty()) {
                return client.getUsuarioEEquipeByUsuarioIds(usuarioIds);
            }
            return Map.of();
        } catch (Exception ex) {
            log.warn("Erro ao obter a equipe de venda dos usuários", ex);
            return Map.of();
        }
    }
}
