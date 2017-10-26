package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFiltros;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.xbrain.autenticacao.modules.comum.enums.Eboolean.F;

@Service
public class UsuarioService {

    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Usuario não encontrado.");

    @Autowired
    @Getter
    private UsuarioRepository repository;

    public Usuario findById(int id) {
        return repository
                .findComplete(id)
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public Page<Usuario> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        return repository.findAll(filtros.toPredicate(), pageRequest);
    }

    public Usuario save(Usuario usuario) {
        if (usuario.isNovoCadastro()) {
            usuario.setDataCadastro(LocalDateTime.now());
            usuario.setSenha("123456");
            usuario.setAlterarSenha(F);
        }
        return repository.save(usuario);
    }
}
