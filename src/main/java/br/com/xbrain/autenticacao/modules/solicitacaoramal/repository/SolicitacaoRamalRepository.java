package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SolicitacaoRamalRepository extends JpaRepository<SolicitacaoRamal, Integer>, SolicitacaoRamalRepositoryCustom {

    Optional<SolicitacaoRamal> findById(Integer id);

    @Modifying
    @Query("UPDATE SolicitacaoRamal r SET r.dataEnviadoEmailExpiracao = ?1 WHERE r.id = ?2")
    void updateFlagDataEnviadoEmailExpiracao(LocalDateTime dataAtual, Integer solicitacaoId);

}
