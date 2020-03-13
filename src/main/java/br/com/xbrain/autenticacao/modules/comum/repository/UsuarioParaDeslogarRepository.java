package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsuarioParaDeslogarRepository extends CrudRepository<UsuarioParaDeslogar, Integer> {

    List<UsuarioParaDeslogar> findAll();

}
