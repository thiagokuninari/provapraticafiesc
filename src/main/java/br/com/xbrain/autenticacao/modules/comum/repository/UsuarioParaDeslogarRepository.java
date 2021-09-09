package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsuarioParaDeslogarRepository extends JpaRepository<UsuarioParaDeslogar, Integer> {

    List<UsuarioParaDeslogar> findAllByDeslogado(Eboolean deslogado);

    List<UsuarioParaDeslogar> findByUsuarioId(Integer usuarioId);

    @Modifying
    @Query("DELETE FROM UsuarioParaDeslogar u WHERE u.usuarioId = ?1")
    void deleteByUsuarioId(Integer usuarioId);
}
