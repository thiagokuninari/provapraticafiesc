package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioInativacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioFerias;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioFeriasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioFeriasService {

    @Autowired
    private UsuarioFeriasRepository repository;

    public UsuarioFerias save(Usuario usuario, UsuarioInativacaoDto usuarioInativacao) {
        if (usuarioInativacao.isFerias()) {
            return repository.save(
                UsuarioFerias.of(usuario,
                        usuarioInativacao.getDataInicio(),
                        usuarioInativacao.getDataFim()));
        }
        return null;
    }

    public List<Usuario> getUsuariosInativosComFeriasEmAberto(LocalDate dataFinalFerias) {
        return repository.getUsuariosInativosComFeriasEmAberto(dataFinalFerias);
    }
}
