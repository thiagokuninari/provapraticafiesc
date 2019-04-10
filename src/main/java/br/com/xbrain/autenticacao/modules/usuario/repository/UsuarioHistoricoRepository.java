package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UsuarioHistoricoRepository
        extends PagingAndSortingRepository<UsuarioHistorico, Integer>, UsuarioHistoricoRepositoryCustom {

    List<UsuarioHistorico> findByUsuarioId(Integer usuarioid);

    @Modifying
    @Query("UPDATE UsuarioHistorico uh SET uh.dataCadastro = ?1, uh.observacao = ?2, uh.situacao = ?3, "
            + "uh.motivoInativacao = ?4 WHERE uh.usuario.id = ?5")
    void updateUsuarioHistorico(LocalDateTime dataCadastro, String obs,
                                ESituacao situacao, MotivoInativacao motivoInativacao, Integer usuarioId);
}