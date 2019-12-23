package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioInativacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioAfastamento;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioAfastamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;

@Service
public class UsuarioAfastamentoService {

    @Autowired
    private UsuarioAfastamentoRepository repository;

    public Optional<UsuarioAfastamento> save(Usuario usuario, UsuarioInativacaoDto usuarioInativacao) {
        if (usuarioInativacao.isAfastamento()) {
            return of(repository.save(
                    UsuarioAfastamento.of(usuario,
                            usuarioInativacao.getDataInicio(),
                            usuarioInativacao.getDataFim())));
        }
        return empty();
    }

    public List<Usuario> getUsuariosInativosComAfastamentoEmAberto(LocalDate dataFimAfastamento) {
        return repository.getUsuariosInativosComAfastamentoEmAberto(dataFimAfastamento);
    }

    public Long atualizaDataFimAfastamento(Integer usuarioId) {
        return repository.atualizaDataFim(usuarioId);
    }
}
