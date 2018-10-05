package br.com.xbrain.autenticacao.modules.equipevendas.service;

import br.com.xbrain.autenticacao.modules.equipevendas.dto.EquipeVendasSupervisionadasResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class EquipeVendasService {

    private final Logger logger = LoggerFactory.getLogger(EquipeVendasService.class);

    @Autowired
    private EquipeVendasClient equipeVendasClient;

    public List<EquipeVendasSupervisionadasResponse> getEquipesPorSupervisor(Integer supervisorId) {
        try {
            return equipeVendasClient.getEquipesPorSupervisor(supervisorId);
        } catch (Exception ex) {
            logger.warn("Erro ao equipes de venda do supervisor", ex);
            return Collections.emptyList();
        }
    }
}
