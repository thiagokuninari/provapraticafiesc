package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SolicitacaoRamalRepository extends CrudRepository<SolicitacaoRamal, Integer>, SolicitacaoRamalRepositoryCustom {

    Optional<SolicitacaoRamal> findById(Integer id);

    @Modifying
    @Query("UPDATE SolicitacaoRamal r SET r.dataEnviadoEmailExpiracao = ?1 WHERE r.id = ?2")
    void updateFlagDataEnviadoEmailExpiracao(LocalDateTime dataAtual, Integer solicitacaoId);

}
