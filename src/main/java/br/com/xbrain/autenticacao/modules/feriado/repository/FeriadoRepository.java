package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface FeriadoRepository extends CrudRepository<Feriado,Integer> {

    Optional<Feriado> findByDataFeriadoAndFeriadoNacional(LocalDate data, Eboolean nacional);

    Optional<Feriado> findByDataFeriadoAndCidadeId(LocalDate data, Integer cidadeId);
}
