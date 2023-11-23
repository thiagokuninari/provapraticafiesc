package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioInativacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioFerias;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioFeriasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioFeriasService {

    private final UsuarioFeriasRepository repository;

    public Optional<UsuarioFerias> save(Usuario usuario, UsuarioInativacaoDto usuarioInativacao) {
        if (usuarioInativacao.isFerias()) {
            return Optional.of(repository.save(
                UsuarioFerias.of(usuario,
                        usuarioInativacao.getDataInicio(),
                        usuarioInativacao.getDataFim())));
        }
        return Optional.empty();
    }

    public List<Usuario> getUsuariosInativosComFeriasEmAberto(LocalDate dataFinalFerias) {
        return repository.getUsuariosInativosComFeriasEmAberto(dataFinalFerias);
    }
}
