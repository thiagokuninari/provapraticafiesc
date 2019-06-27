package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Integer>,
        QueryDslPredicateExecutor<Usuario>, UsuarioRepositoryCustom {

    Optional<Usuario> findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(String email, ESituacao situacao);

    Optional<Usuario> findTop1UsuarioByCpf(String cpf);

    Optional<Usuario> findTop1UsuarioByCpfAndSituacaoNot(String cpf, ESituacao situacao);

    Optional<Usuario> findById(Integer id);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findUsuarioByEmail(String email);

    List<Usuario> findAllByCpf(String cpf);

    List<Usuario> findBySituacaoAndIdIn(ESituacao situacao, List<Integer> ids);

    List<Usuario> findAllByCargoAndDepartamento(Cargo cargoId, Departamento departamentoId);

    List<Usuario> findAllUsuarioByEmailIgnoreCase(String email);

    List<Usuario> findAllByEmailIgnoreCaseOrCpfAndSituacaoNot(String email, String cpf, ESituacao situacao);

    @Modifying
    @Query("update Usuario u set u.senha = ?1, alterarSenha = ?2, recuperarSenhaHash = null, "
            + "recuperarSenhaTentativa = 0 where u.id = ?3")
    void updateSenha(String senha, Eboolean alterarSenha, Integer usuarioId);

    @Modifying
    @Query("update Usuario u set u.senha = ?1 where u.id = ?2")
    void updateSenha(String senha, Integer usuarioId);

    @Modifying
    @Query("update Usuario u set u.recuperarSenhaHash = ?1 where u.id = ?2")
    void updateRecuperarSenhaHash(String recuperarSenhaHash, Integer usuarioId);

    @Modifying
    @Query("update Usuario u set u.recuperarSenhaTentativa = ?1 where u.id = ?2")
    void updateRecuperarSenhaTentativa(Integer recuperarSenhaTentativa, Integer usuarioId);

    @Modifying
    @Query("update Usuario u set u.email = ?1 where u.id = ?2")
    void updateEmail(String email, Integer usuarioId);

    @Modifying
    @Query("update Usuario u set u.cpf = ?1 where u.id = ?2")
    void updateCpf(String cpf, Integer usuarioId);

    @Modifying
    @Query("update Usuario u set u.cargo = ?1 where u.id = ?2")
    void updateCargo(Cargo cargo, Integer usuarioId);

    @Modifying
    @Query("update Usuario u set u.situacao = ?1 where u.id = ?2")
    void updateSituacao(ESituacao situacao, Integer usuarioId);

    @Query("SELECT x.empresas FROM Usuario x WHERE x.id = :id")
    List<Empresa> findEmpresasById(@Param("id") Integer id);

    @Query("SELECT x.unidadesNegocios FROM Usuario x WHERE x.id = :id")
    List<UnidadeNegocio> findUnidadesNegociosById(@Param("id") Integer id);

    @Modifying
    @Query("update Usuario u set u.dataUltimoAcesso = ?1 where u.id = ?2")
    void atualizarDataUltimoAcesso(LocalDateTime data, Integer id);
}
