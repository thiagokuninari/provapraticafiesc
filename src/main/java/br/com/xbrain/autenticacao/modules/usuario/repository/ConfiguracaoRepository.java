package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Configuracao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConfiguracaoRepository extends PagingAndSortingRepository<Configuracao, Integer> {

    Optional<Configuracao> findByUsuario(Usuario usuario);

    List<Configuracao> findByRamal(Integer ramal);

    @Modifying
    @Query("update Configuracao set ramal = null where ramal in :ramaisId and fk_usuario in :usuariosId")
    void updateRamaisToNullByRamaisIdsAndUsuariosIds(@Param("ramaisId") List<Integer> ramaisId,
                                                     @Param("usuariosId") List<Integer> usuariosId);
}
