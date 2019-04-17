package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UsuarioCidadeRepository extends PagingAndSortingRepository<UsuarioCidade, Integer> {

    @Modifying
    @Query("delete from UsuarioCidade c WHERE c.cidade.id = ?1 AND c.usuario.id = ?2")
    void deleteByCidadeAndUsuario(int cidadeId, int usuarioId);

    @Modifying
    @Query("delete from UsuarioCidade c WHERE c.usuario.id = ?1")
    void deleteByUsuario(int usuarioId);

    @Query("select c.id from UsuarioCidade c where c.usuario.id = ?1")
    List<Integer> findCidadesIdByUsuarioId(int usuarioId);
}
