package br.com.xbrain.autenticacao.modules.autenticacao.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_access_token")
@Data
public class OAuthAccessToken {

    private String tokenId;
    @Lob
    private Byte[] token;
    @Id
    private String authenticationId;

    private String userName;
    private String clientId;
    @Lob
    private Byte[] authentication;
    @Lob
    private Byte[] refreshToken;
}
