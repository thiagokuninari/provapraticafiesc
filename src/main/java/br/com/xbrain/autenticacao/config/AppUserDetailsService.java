package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
                log.info("Iniciando validação de horário permitido para o usuário {}", u.getEmail());
                validarUsuarioForaHorarioPermitido(u);
                log.info("Gerando Auth User para o usuário");
                var user = new User(
                    u.getId().toString() + "-" + u.getEmail(),
                    autenticacaoService.isEmulacao() ? new BCryptPasswordEncoder().encode("") : u.getSenha(),
                    funcionalidadeService.getPermissoes(u));
                log.info("Auth User gerado com sucesso {}", user);
                return user;
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
