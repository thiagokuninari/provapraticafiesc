package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;

import java.time.LocalDate;
import java.util.List;

public interface FeriadoRepositoryCustom {

    List<Feriado> findAllByAnoAtual(LocalDate now);
}
