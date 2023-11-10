package br.com.xbrain.autenticacao.modules.organizacaoempresa.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.model.QOrganizacaoEmpresa.organizacaoEmpresa;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.BACKOFFICE_SUPORTE_VENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.model.QNivel.nivel;

public class OrganizacaoEmpresaRepositoryImpl extends CustomRepository<OrganizacaoEmpresa>
    implements OrganizacaoEmpresaRepositoryCustom {

    @Override
    public List<OrganizacaoEmpresa> findByPredicate(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .select(organizacaoEmpresa)
            .from(organizacaoEmpresa)
            .where(predicate)
            .orderBy(organizacaoEmpresa.nome.asc())
            .fetch();
    }
}
