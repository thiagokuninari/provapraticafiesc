package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubCanalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SubCanalService {

    private static final ValidacaoException SUBCANAL_NAO_ENCONTRADO =
            new ValidacaoException("Erro, subcanal não encontrado.");

    @Autowired
    private SubCanalRepository repository;

    @Autowired
    private UsuarioService usuarioService;

    public List<SubCanalDto> getAll() {
        return repository.findAll()
            .stream()
            .map(SubCanalDto::of)
            .collect(Collectors.toList());
    }

    public SubCanalDto getSubCanalById(Integer id) {
        return SubCanalDto.of(repository.findById(id)
            .orElseThrow(() -> SUBCANAL_NAO_ENCONTRADO));
    }

    public Set<SubCanalDto> getSubCanalByUsuarioId(Integer usuarioId) {
        return usuarioService.buscarSubCanaisPorUsuarioId(usuarioId)
            .stream()
            .map(SubCanalDto::of)
            .collect(Collectors.toSet());
    }
}
