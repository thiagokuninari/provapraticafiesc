package br.com.xbrain.autenticacao.modules.usuario.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.ASSISTENTE_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.OPERACAO_TELEVENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioObjectArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("oracle-test")
@Transactional
@Sql({"classpath:/tests_usuario_repository-oracle.sql"})
public class UsuarioRepositoryOracleTest {

    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private CargoRepository cargoRepository;
    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Test
    public void getSubordinadosPorCargo_listObjectArray_seNaoHouverUsuariosSubordinados() {
        assertThat(repository.getSubordinadosPorCargo(1001, Set.of(OPERACAO_TELEVENDAS.name())))
            .isEmpty();
    }

    @Test
    public void getSubordinadosPorCargo_listObjectArray_seNaoHouverUsuariosSubordinadosDoCargo() {
        assertThat(repository.getSubordinadosPorCargo(1002, Set.of(ASSISTENTE_OPERACAO.name())))
            .isEmpty();
    }

    @Test
    public void getSubordinadosPorCargo_listObjectArray_seHouverUsuariosSubordinadosDoCargo() {
        var listObject = new ArrayList<>();
        listObject.add(umUsuarioObjectArray(BigDecimal.valueOf(1003), "TELEVENDAS_1@XBRAIN.COM.BR",
            "TELEVENDAS 1", "Operador Televendas", OPERACAO_TELEVENDAS));

        assertThat(repository.getSubordinadosPorCargo(1002, Set.of(OPERACAO_TELEVENDAS.name())).toArray())
            .isEqualTo(listObject.toArray());
    }

    @Test
    public void buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos_listaVazia_seNaoHouverUsuariosSubordinados() {
        assertThat(repository
                .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(List.of(1001, 1004), Set.of(OPERACAO_TELEVENDAS.name())))
            .isEmpty();
    }

    @Test
    public void buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos_listaVazia_seNaoHouverUsuariosSubordinadosDosCargos() {
        assertThat(repository
                .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(List.of(1002, 1005), Set.of(ASSISTENTE_OPERACAO.name())))
            .isEmpty();
    }

    @Test
    @SuppressWarnings("LineLength")
    public void buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos_listaVazia_seNaoHouverUsuariosSubordinadosAtivosDosCargos() {
        assertThat(repository
                .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(List.of(1005), Set.of(ASSISTENTE_OPERACAO.name())))
            .isEmpty();
    }

    @Test
    @SuppressWarnings("LineLength")
    public void buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos_listaDeUsuarioResponse_seHouverUsuariosSubordinadosAtivosDosCargos() {
        assertThat(repository
                .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(List.of(1002), Set.of(OPERACAO_TELEVENDAS.name())))
            .extracting("id", "nome", "situacao", "codigoCargo")
            .containsExactly(
                tuple(1003, "TELEVENDAS 1", A, OPERACAO_TELEVENDAS));
    }
}
