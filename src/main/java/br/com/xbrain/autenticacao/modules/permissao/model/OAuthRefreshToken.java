package br.com.xbrain.autenticacao.modules.permissao.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_refresh_token")
@Data
public class OAuthRefreshToken {

    @Id
    private String tokenId;
    @Lob
    private Byte[] token;
    @Lob
    private Byte[] authentication;

}
