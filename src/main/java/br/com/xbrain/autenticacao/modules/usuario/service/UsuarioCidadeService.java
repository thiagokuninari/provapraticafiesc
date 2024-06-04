package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioCidadeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioCidadeService {

    private final UsuarioCidadeRepository repository;

    public List<UsuarioCidadeDto> getUsuarioCidadesById(Integer id) {
        return repository.findUsuarioCidadesByUsuarioId(id)
            .stream()
            .map(UsuarioCidadeDto::of);
    }
}
