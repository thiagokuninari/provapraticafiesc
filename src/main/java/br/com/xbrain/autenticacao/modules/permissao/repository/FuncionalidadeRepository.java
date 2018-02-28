package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface FuncionalidadeRepository extends PagingAndSortingRepository<Funcionalidade, Integer> {

    Optional<Funcionalidade> findByRole(String role);

}
