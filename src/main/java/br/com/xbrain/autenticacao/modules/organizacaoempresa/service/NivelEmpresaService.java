package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.NivelEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NivelEmpresaService {

    @Autowired
    private NivelEmpresaRepository nivelEmpresaRepository;

    public List<SelectResponse> getAllNivelEmpresa() {
        return nivelEmpresaRepository.findAll()
            .stream()
            .map(nivel -> new SelectResponse(nivel.getId(), nivel.getNivelEmpresa().name()))
            .collect(Collectors.toList());
    }
}
