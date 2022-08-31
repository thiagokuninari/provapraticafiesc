package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.ModalidadeEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModalidadeEmpresaService {

    @Autowired
    private ModalidadeEmpresaRepository modalidadeEmpresaRepository;

    public List<SelectResponse> getAllModalidadeEmpresa() {
        return modalidadeEmpresaRepository.findAll()
            .stream()
            .map(modalidade -> new SelectResponse(modalidade.getId(), modalidade.getModalidadeEmpresa().name()))
            .collect(Collectors.toList());
    }
}
