package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository;

import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ImportacaoAutomaticaFeriadoRepositoryCustom {

    Page<ImportacaoFeriado> findAllImportacaoHistorico(Pageable pageable, Predicate predicate) ;
}
