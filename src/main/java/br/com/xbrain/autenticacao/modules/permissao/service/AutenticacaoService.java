package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.permissao.repository.OAuthAccessTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@SuppressWarnings("unchecked")
public class AutenticacaoService {

    public static final String HEADER_USUARIO_EMULADOR = "X-Usuario-Emulador";

    @Autowired
    private OAuthAccessTokenRepository tokenRepository;

    public String getLoginUsuario() {
        return getAuthentication().getName();
    }

    public Integer getUsuarioId() {
        return Integer.parseInt(getAuthentication().getName().split(Pattern.quote("-"))[0]);
    }

    @Transactional
    public void logout(String login) {
        tokenRepository.deleteTokenByUsername(login);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}