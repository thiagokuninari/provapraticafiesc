package br.com.xbrain.autenticacao.modules.usuario.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.ASSISTENTE_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.OPERACAO_TELEVENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.util.UsuarioConstantesUtils.*;
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

    @Test
    @Sql({"classpath:/tests_usuario_subcanal_repository.sql"})
    @SuppressWarnings("LineLength")
    public void getSubCanalIdsDosSubordinados_deveRetornarListaDeSubCanais_seExistirSubordinadosDoDiretor() {
        var diretorId = 126;

        assertThat(repository
            .getSubCanalIdsDosSubordinados(diretorId))
            .hasSize(4)
            .containsAll(List.of(PAP_ID, PAP_PME_ID, PAP_PREMIUM_ID, INSIDE_SALES_PME_ID));
    }

    @Test
    @Sql({"classpath:/tests_usuario_subcanal_repository.sql"})
    @SuppressWarnings("LineLength")
    public void getSubCanalIdsDosSubordinados_deveRetornarListaDeSubCanais_seExistirSubordinadosDoGerente() {
        var gerenteId = 127;

        assertThat(repository
            .getSubCanalIdsDosSubordinados(gerenteId))
            .hasSize(4)
            .containsAll(List.of(PAP_ID, PAP_PME_ID, PAP_PREMIUM_ID, INSIDE_SALES_PME_ID));
    }

    @Test
    @Sql({"classpath:/tests_usuario_subcanal_repository.sql"})
    @SuppressWarnings("LineLength")
    public void getSubCanalIdsDosSubordinados_deveRetornarListaDeSubCanais_seExistirSubordinadosDoCoordenadorPapPremium() {
        var coordenadorPapPremiumId = 129;

        assertThat(repository
            .getSubCanalIdsDosSubordinados(coordenadorPapPremiumId))
            .hasSize(1)
            .containsAll(List.of(PAP_PREMIUM_ID));
    }
}
