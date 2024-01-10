package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface NivelRepository extends PagingAndSortingRepository<Nivel, Integer>, NivelRepositoryCustom {

    Nivel findByCodigo(CodigoNivel codigo);

    boolean existsByCodigo(CodigoNivel codigo);

    Optional<Nivel> findById(Integer id);

    List<Nivel> findByCodigoIn(List<CodigoNivel> niveis);
}
