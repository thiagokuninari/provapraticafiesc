package br.com.xbrain.autenticacao.modules.usuarioacesso.repository;

import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioAcessoRepository
        extends CrudRepository<UsuarioAcesso, Integer>, UsuarioAcessoRepositoryCustom {
}
