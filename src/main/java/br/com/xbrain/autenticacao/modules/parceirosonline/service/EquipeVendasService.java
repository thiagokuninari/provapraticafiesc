package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisionadasResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EquipeVendasService {

    @Autowired
    private EquipeVendasClient equipeVendasClient;

    public List<EquipeVendasSupervisionadasResponse> getEquipesPorSupervisor(Integer supervisorId) {
        try {
            return equipeVendasClient.getEquipesPorSupervisor(supervisorId);
        } catch (Exception ex) {
            log.warn("Erro ao obter as equipes de venda do supervisor", ex);
            return Collections.emptyList();
        }
    }

    public EquipeVendaDto getByUsuario(Integer usuarioId) {
        try {
            return equipeVendasClient.getByUsuario(usuarioId);
        } catch (Exception ex) {
            log.warn("Erro ao obter a equipe de venda do usuário", ex);
            return null;
        }
    }

    public Map<Integer, Integer> getUsuarioEEquipeByUsuarioIds(List<Integer> usuarioIds) {
        try {
            if (!usuarioIds.isEmpty()) {
                return equipeVendasClient.getUsuarioEEquipeByUsuarioIds(usuarioIds);
            }
            return Map.of();
        } catch (Exception ex) {
            log.warn("Erro ao obter a equipe de venda dos usuários", ex);
            return Map.of();
        }
    }
}
