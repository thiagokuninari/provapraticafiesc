package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioFerias;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioFeriasRepository extends CrudRepository<UsuarioFerias, Integer>, UsuarioFeriasRepositoryCustom {
}
