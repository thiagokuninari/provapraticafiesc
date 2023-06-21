package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.QImportacaoFeriado.importacaoFeriado;

public class ImportacaoAutomaticaFeriadoRepositoryImpl extends CustomRepository<ImportacaoFeriado>
    implements ImportacaoAutomaticaFeriadoRepositoryCustom {

    @Override
    public Page<ImportacaoFeriado> findAllImportacaoHistorico(Pageable pageable, Predicate predicate) {
        var query = new JPAQueryFactory(entityManager)
            .selectFrom(importacaoFeriado)
            .where(predicate);

        return findAll(pageable, query);
    }
}
