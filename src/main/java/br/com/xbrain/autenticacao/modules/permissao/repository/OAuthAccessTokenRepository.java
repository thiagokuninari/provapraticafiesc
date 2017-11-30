package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.OAuthAccessToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OAuthAccessTokenRepository extends CrudRepository<OAuthAccessToken, String> {

    @Modifying
    @Query("delete from OAuthAccessToken a where a.userName = ?1")
    void deleteTokenByUsername(String userName);
}
