package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.permissao.exception.ExceedMaxTriesResetPassException;
import br.com.xbrain.autenticacao.modules.permissao.exception.InvalidTokenResetPassException;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JsonWebTokenService {

    private static final long EXPIRACAO_EM_VINTE_MINUTOS = (long) 1200 * 1000L;
    private SignatureAlgorithm signatureAlgorithm;
    private Key secretKey;

    public JsonWebTokenService() {
        signatureAlgorithm = SignatureAlgorithm.HS512;
        String encodedKey = "UVT/z+i0v9lJX36/nej7ug==";
        secretKey = deserializeKey(encodedKey);
    }

    private Key getSecretKey() {
        return secretKey;
    }

    private Key deserializeKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        Key key = new SecretKeySpec(decodedKey, getSignatureAlgorithm().getJcaName());
        return key;
    }

    public String createJsonWebTokenResetSenha(String email, Integer id) {
        return Jwts.builder()
                .claim("id", id)
                .claim("email", email)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRACAO_EM_VINTE_MINUTOS))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, getSecretKey()).compact();
    }

    public Jws<Claims> validateTokenPasswordReset(String hash) {
        return Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(hash);
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }
}
