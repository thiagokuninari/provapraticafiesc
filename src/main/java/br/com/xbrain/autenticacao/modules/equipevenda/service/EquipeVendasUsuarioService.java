package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EquipeVendasUsuarioService {

    @Autowired
    private EquipeVendasUsuarioClient equipeVendasUsuarioClient;

    public List<EquipeVendaUsuarioResponse> getAll(Map<String, Object> filtros) {
        return equipeVendasUsuarioClient.getAll(filtros);
    }

    public List<EquipeVendaUsuarioResponse> buscarUsuarioPorId(Integer id) {
        return equipeVendasUsuarioClient.buscarUsuarioPorId(id);
    }
}
