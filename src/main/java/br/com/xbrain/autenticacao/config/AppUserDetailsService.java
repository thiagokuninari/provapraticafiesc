package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private FuncionalidadeService funcionalidadeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository
                .findByEmail(username.toUpperCase())
                .map(u -> {
                    if (u.getDataInativacao() != null) {
                        throw new ValidacaoException("Usuário Inativo, solicite a ativação ao seu responsável");
                    }
                    return new User(
                            u.getId().toString() + "-" + u.getEmail(),
                            u.getSenha(),
                            funcionalidadeService.getPermissoes(u));
                }).orElseThrow(() ->
                        new UsernameNotFoundException("Erro ao autenticar."));
    }
}
