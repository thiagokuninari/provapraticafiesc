package br.com.xbrain.autenticacao.modules.organizacaoempresa.repository;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresaHistorico;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizacaoEmpresaHistoricoRepository extends PagingAndSortingRepository<OrganizacaoEmpresaHistorico,
    Integer> {

    List<OrganizacaoEmpresaHistorico> findAllByOrganizacaoEmpresaIdOrderByDataAlteracaoDesc(Integer organizacaoEmpresaId);
}
