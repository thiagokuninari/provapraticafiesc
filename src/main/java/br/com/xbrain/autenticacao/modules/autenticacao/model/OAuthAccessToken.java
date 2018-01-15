package br.com.xbrain.autenticacao.modules.autenticacao.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_access_token")
@Data
public class OAuthAccessToken {

    //FIXME verificar qual a melhor forma de salvar Byte[] no banco em memória (HSQL)
    private String tokenId;
    @Column(length = 20971520)
    private Byte[] token;
    @Id
    private String authenticationId;
    private String userName;
    private String clientId;
    @Column(length = 20971520)
    private Byte[] authentication;
    @Column(length = 20971520)
    private Byte[] refreshToken;

}
