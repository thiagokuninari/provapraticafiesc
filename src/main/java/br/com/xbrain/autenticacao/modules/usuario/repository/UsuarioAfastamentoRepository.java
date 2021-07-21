package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioAfastamento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioAfastamentoRepository extends CrudRepository<UsuarioAfastamento, Integer>,
    UsuarioAfastamentoRepositoryCustom {
}

