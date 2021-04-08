package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissaoEspecialRepository extends JpaRepository<PermissaoEspecial, Integer>,
        PermissaoEspecialRepositoryCustom {

    Optional<PermissaoEspecial> findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(Integer usuarioId,
                                                                                        Integer funcionalidadeId);
}
