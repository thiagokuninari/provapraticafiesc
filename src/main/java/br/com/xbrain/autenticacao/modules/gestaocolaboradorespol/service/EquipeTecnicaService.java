package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service;

import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client.EquipeTecnicaClient;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeTecnicaSupervisionadasResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipeTecnicaService {

    private final EquipeTecnicaClient client;

    public List<EquipeTecnicaSupervisionadasResponse> getEquipesPorSupervisor(Integer supervisorId) {
        try {
            return client.getEquipesTecnicasPorSupervisor(supervisorId);
        } catch (Exception ex) {
            log.warn("Erro ao obter as equipes técnicas do supervisor", ex);
            return Collections.emptyList();
        }
    }
}
