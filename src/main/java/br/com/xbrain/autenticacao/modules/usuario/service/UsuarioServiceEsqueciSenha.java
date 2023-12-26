package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.permissao.exception.ExceedMaxTriesResetPassException;
import br.com.xbrain.autenticacao.modules.permissao.exception.InvalidTokenResetPassException;
import br.com.xbrain.autenticacao.modules.permissao.service.JsonWebTokenService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.R;

@Service
public class UsuarioServiceEsqueciSenha {

    private static final int MAX_CARACTERES_SENHA = 6;
    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Usuário não encontrado.");
    private static final Integer MAXIMO_TENTATIVAS_RESETAR_SENHA = 3;

    @Value("${app-config.url}")
    private String projetoUrl;

    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NotificacaoService notificacaoService;
    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @Transactional
    public void enviarConfirmacaoResetarSenha(String email) {
        Usuario usuario = repository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(email, R)
            .orElseThrow(() -> EX_NAO_ENCONTRADO);

        String hash = usuario.getRecuperarSenhaHash();
        if (hash == null) {
            notificarCliente(usuario, true, null);
        } else {
            try {
                if (usuario.getRecuperarSenhaTentativa() == 0) {
                    notificarCliente(usuario, true, null);
                } else {
                    jsonWebTokenService.validateTokenPasswordReset(hash);
                    if (usuario.getRecuperarSenhaTentativa() < MAXIMO_TENTATIVAS_RESETAR_SENHA) {
                        notificarCliente(usuario, false, null);
                    } else {
                        throw new ExceedMaxTriesResetPassException("Excedido o número de solicitações para resetar "
                                + "a senha. Aguarde 20 minutos para realizar outra tentativa.");
                    }
                }
            } catch (ExpiredJwtException exception) {
                // usuario requisitou a token,
                // mas não realizou a etapa de resetar a senha (que remove o token)
                // nesse caso o token existe mas expirou
                // podemos excluir o token e criar outra
                repository.updateRecuperarSenhaTentativa(0, usuario.getId());
                notificarCliente(usuario, true, 0);
            } catch (SignatureException exception) {
                repository.updateRecuperarSenhaTentativa(0, usuario.getId());
                notificarCliente(usuario, true, 0);
            }
        }
    }

    public void notificarCliente(Usuario usuario, Boolean gerarToken, Integer tentativa) {
        String hash;
        if (gerarToken) {
            hash = jsonWebTokenService.createJsonWebTokenResetSenha(
                    usuario.getEmail(),
                    usuario.getId());
            repository.updateRecuperarSenhaHash(hash, usuario.getId());
        } else {
            hash = usuario.getRecuperarSenhaHash();
        }
        tentativa = tentativa != null ? tentativa : usuario.getRecuperarSenhaTentativa();
        repository.updateRecuperarSenhaTentativa(tentativa + 1, usuario.getId());
        String link = projetoUrl + "/login?token=" + hash;
        notificacaoService.enviarEmailResetSenha(usuario, link);
    }

    @Transactional
    public void resetarSenha(String hash) {
        try {
            Claims deserializedHash = jsonWebTokenService.validateTokenPasswordReset(hash).getBody();
            Usuario usuario = repository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(
                deserializedHash.get("email").toString(), R)
                    .orElseThrow(() -> EX_NAO_ENCONTRADO);
            if (usuario.getRecuperarSenhaHash() != null && usuario.getRecuperarSenhaHash().contentEquals(hash)) {

                String senhaDescriptografada = StringUtil.getSenhaRandomica(MAX_CARACTERES_SENHA);
                repository.updateSenha(passwordEncoder.encode(senhaDescriptografada), Eboolean.V, usuario.getId());
                notificacaoService.enviarEmailAtualizacaoSenha(usuario, senhaDescriptografada);

            } else {
                throw new InvalidTokenResetPassException("Token inválida ou expirada.");
            }
        } catch (JwtException exception) {
            throw new InvalidTokenResetPassException("Token inválida ou expirada.");
        }
    }
}
