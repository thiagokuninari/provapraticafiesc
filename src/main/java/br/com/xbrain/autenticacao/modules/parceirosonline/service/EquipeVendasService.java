package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisionadasResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
}
