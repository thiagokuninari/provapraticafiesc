package br.com.xbrain.autenticacao.modules.usuarioacesso.repository;

import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsuarioAcessoRepository
        extends CrudRepository<UsuarioAcesso, Integer>, UsuarioAcessoRepositoryCustom {

    List<UsuarioAcesso> findAllByUsuarioId(Integer usuarioId);
}
