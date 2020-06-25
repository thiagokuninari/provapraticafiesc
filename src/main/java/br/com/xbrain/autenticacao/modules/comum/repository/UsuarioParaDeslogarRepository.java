package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioParaDeslogarRepository extends JpaRepository<UsuarioParaDeslogar, Integer> {

    List<UsuarioParaDeslogar> findAllByDeslogado(Eboolean deslogado);

    Optional<UsuarioParaDeslogar> findByUsuarioId(Integer usuarioId);
}