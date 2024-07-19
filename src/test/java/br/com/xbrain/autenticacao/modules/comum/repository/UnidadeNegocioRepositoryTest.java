package br.com.xbrain.autenticacao.modules.comum.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio.*;
import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.data.domain.Sort.Direction.ASC;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
public class UnidadeNegocioRepositoryTest {

    @Autowired
    UnidadeNegocioRepository unidadeNegocioRepository;

    @Test
    public void findAll_deveRetornarUnidadesDeNegocioOrdenadasPorNome_quandoSolicitado() {
        assertThat(unidadeNegocioRepository.findAll(new Sort(ASC, "nome")))
            .extracting("id", "nome", "codigo", "situacao")
            .containsExactly(
                tuple(1, "Pessoal", PESSOAL, A),
                tuple(2, "Residencial e Combos", RESIDENCIAL_COMBOS, A),
                tuple(3, "Xbrain", XBRAIN, A));
    }
}
