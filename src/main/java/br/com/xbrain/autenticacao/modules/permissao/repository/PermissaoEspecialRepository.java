package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PermissaoEspecialRepository extends CrudRepository<PermissaoEspecial, Integer>,
        PermissaoEspecialRepositoryCustom  {

    @Modifying
    @Query("delete from PermissaoEspecial p WHERE p.funcionalidade.id = ?1")
    void deleteByFuncionalidade(int funcionalidadeId);
}
