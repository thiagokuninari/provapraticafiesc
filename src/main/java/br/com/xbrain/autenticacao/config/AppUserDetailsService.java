package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private FuncionalidadeService funcionalidadeService;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private HorarioAcessoService horarioAcessoService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository
            .findByEmailIgnoreCase(username)
            .map(u -> {
                u.forceLoad();
                validarUsuarioPendente(u);
                validarUsuarioInativo(u);
                validarUsuarioForaHorarioPermitido(u);
                return new User(
                    u.getId().toString() + "-" + u.getEmail(),
                    autenticacaoService.isEmulacao() ? new BCryptPasswordEncoder().encode("") : u.getSenha(),
                    funcionalidadeService.getPermissoes(u));
            }).orElseThrow(() ->
                new UsernameNotFoundException("Email ou senha inválidos."));
    }

    private void validarUsuarioPendente(Usuario usuario) {
        if (!autenticacaoService.isEmulacao() && usuario.getSituacao() == ESituacao.P) {
            throw new ValidacaoException("Agente Autorizado com aceite de contrato pendente.");
        }
    }

    private void validarUsuarioInativo(Usuario usuario) {
        if (!autenticacaoService.isEmulacao() && usuario.getSituacao() == ESituacao.I) {
            throw new ValidacaoException("Usuário Inativo, solicite a ativação ao seu responsável.");
        }
    }

    private void validarUsuarioForaHorarioPermitido(Usuario usuario) {
        horarioAcessoService.isDentroHorarioPermitido(usuario);
    }
}
