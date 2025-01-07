package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CargoPredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class CargoRepositoryTest {

    private static final int NIVEL_ID_RECEPTIVO = 8;
    @Autowired
    private CargoRepository repository;

    @Test
    public void findFirstByNomeIgnoreCaseAndNivelId_deveEncontrarCargoVendedorReceptivo_quandoInformarNivelIdValido() {
        assertThat(
            repository.findFirstByNomeIgnoreCaseAndNivelId("veNdEdor receptivo", NIVEL_ID_RECEPTIVO))
            .isPresent()
            .containsInstanceOf(Cargo.class);
    }

    @Test
    public void findFirstByNomeIgnoreCaseAndNivelId_deveRetornarOptionalEmpty_quandoInformarNivelIdInvalido() {
        assertThat(
            repository.findFirstByNomeIgnoreCaseAndNivelId("veNdEdor receptivo", 9999999))
            .isNotPresent();
    }

    @Test
    public void findAll_deveRetornarCargos_quandoPredicateEPaginacaoInformadoS() {
        assertThat(
            repository.findAll(new CargoPredicate().build(), new PageRequest()))
            .extracting("id", "nome", "codigo")
            .containsExactly(
                tuple(1, "Analista", OPERACAO_ANALISTA),
                tuple(2, "Assistente", ASSISTENTE_OPERACAO),
                tuple(3, "Consultor", null),
                tuple(4, "Coordenador", COORDENADOR_OPERACAO),
                tuple(5, "Executivo", EXECUTIVO),
                tuple(6, "Diretor", null),
                tuple(7, "Gerente", GERENTE_OPERACAO),
                tuple(8, "Vendedor", VENDEDOR_OPERACAO),
                tuple(9, "Técnico", null),
                tuple(10, "Supervisor", SUPERVISOR_OPERACAO));
    }

    @Test
    public void findAll_deveRetornarCargos_quandoPredicateInformado() {
        assertThat(
            repository.findAll(new CargoPredicate().build()).size())
            .isEqualTo(101);
    }
}
