package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Integer>,
        QueryDslPredicateExecutor<Usuario>, UsuarioRepositoryCustom {

    Optional<Usuario> findTop1UsuarioByEmailIgnoreCase(String email);

    Optional<Usuario> findTop1UsuarioByCpf(String cpf);

    Optional<Usuario> findById(Integer id);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAllByCpfIsNull();

    List<Usuario> findBySituacaoAndIdIn(ESituacao situacao, List<Integer> ids);

    List<Usuario> findAllByCargoAndDepartamento(Cargo cargoId, Departamento departamentoId);

    List<Usuario> findAllUsuarioByEmailIgnoreCase(String email);

    @Modifying
    @Query("update Usuario u set u.senha = ?1 where u.id = ?2")
    void updateSenha(String senha, Integer usuarioId);

    @Modifying
    @Query("update Usuario u set u.email = ?1 where u.id = ?2")
    void updateEmail(String email, Integer usuarioId);

    @Modifying
    @Query("update Usuario u set u.cargo = ?1 where u.id = ?2")
    void updateCargo(Cargo cargo, Integer usuarioId);
}
