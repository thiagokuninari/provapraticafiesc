package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
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
    public void getSubCanalIdsDosSubordinados_deveRetornarListaDeSubCanais_seExistirSubordinadosDoDiretor() {
        var diretorId = 126;

        assertThat(repository
            .getAllSubordinadosComSubCanalId(diretorId))
            .hasSize(21)
            .extracting("nome", "subCanalId")
            .containsExactlyInAnyOrder(
                tuple("COORDENADOR OPERACAO", ETipoCanal.PAP.getId()),
                tuple("COORDENADOR OPERACAO", ETipoCanal.INSIDE_SALES_PME.getId()),
                tuple("ASSISTENTE OPERACAO INSIDE_SALES_PME", ETipoCanal.INSIDE_SALES_PME.getId()),
                tuple("VENDEDOR OPERACAO INSIDE SALES PME", ETipoCanal.INSIDE_SALES_PME.getId()),
                tuple("COORDENADOR OPERACAO", ETipoCanal.PAP_PME.getId()),
                tuple("ASSISTENTE OPERACAO PAP PREMIUM", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("GERENTE OPERACAO", ETipoCanal.PAP.getId()),
                tuple("SUPERVISOR OPERACAO INSIDE SALES PME", ETipoCanal.INSIDE_SALES_PME.getId()),
                tuple("COORDENADOR OPERACAO", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("SUPERVISOR OPERACAO PAP PME", ETipoCanal.PAP_PME.getId()),
                tuple("SUPERVISOR OPERACAO PAP", ETipoCanal.PAP.getId()),
                tuple("ASSISTENTE OPERACAO PAP PME", ETipoCanal.PAP_PME.getId()),
                tuple("VENDEDOR OPERACAO PAP_PREMIUM", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("ASSISTENTE OPERACAO PAP", ETipoCanal.PAP.getId()),
                tuple("VENDEDOR OPERACAO PAP", ETipoCanal.PAP.getId()),
                tuple("COORDENADOR OPERACAO PAP_PREMIUM", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("SUPERVISOR OPERACAO PAP PREMIUM", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("GERENTE OPERACAO", ETipoCanal.PAP_PME.getId()),
                tuple("GERENTE OPERACAO", ETipoCanal.INSIDE_SALES_PME.getId()),
                tuple("VENDEDOR OPERACAO PAP_PME", ETipoCanal.PAP_PME.getId()),
                tuple("GERENTE OPERACAO", ETipoCanal.PAP_PREMIUM.getId())
            );
    }

    @Test
    @Sql({"classpath:/tests_usuario_subcanal_repository.sql"})
    public void getSubCanalIdsDosSubordinados_deveRetornarListaDeSubCanais_seExistirSubordinadosDoGerente() {
        var gerenteId = 127;

        assertThat(repository
            .getAllSubordinadosComSubCanalId(gerenteId))
            .hasSize(17)
            .extracting("nome", "subCanalId")
            .containsExactlyInAnyOrder(
                tuple("COORDENADOR OPERACAO", ETipoCanal.INSIDE_SALES_PME.getId()),
                tuple("COORDENADOR OPERACAO", ETipoCanal.PAP.getId()),
                tuple("ASSISTENTE OPERACAO INSIDE_SALES_PME", ETipoCanal.INSIDE_SALES_PME.getId()),
                tuple("VENDEDOR OPERACAO INSIDE SALES PME", ETipoCanal.INSIDE_SALES_PME.getId()),
                tuple("COORDENADOR OPERACAO", ETipoCanal.PAP_PME.getId()),
                tuple("ASSISTENTE OPERACAO PAP PREMIUM", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("SUPERVISOR OPERACAO INSIDE SALES PME", ETipoCanal.INSIDE_SALES_PME.getId()),
                tuple("COORDENADOR OPERACAO", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("SUPERVISOR OPERACAO PAP PME", ETipoCanal.PAP_PME.getId()),
                tuple("SUPERVISOR OPERACAO PAP", ETipoCanal.PAP.getId()),
                tuple("ASSISTENTE OPERACAO PAP PME", ETipoCanal.PAP_PME.getId()),
                tuple("VENDEDOR OPERACAO PAP_PREMIUM", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("ASSISTENTE OPERACAO PAP", ETipoCanal.PAP.getId()),
                tuple("VENDEDOR OPERACAO PAP", ETipoCanal.PAP.getId()),
                tuple("COORDENADOR OPERACAO PAP_PREMIUM", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("SUPERVISOR OPERACAO PAP PREMIUM", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("VENDEDOR OPERACAO PAP_PME", ETipoCanal.PAP_PME.getId())
            );
    }

    @Test
    @Sql({"classpath:/tests_usuario_subcanal_repository.sql"})
    public void getAllSubordinadosComSubCanalId_deveRetornarListaDeSubCanais_seExistirSubordinadosDoCoordenadorPapPremium() {
        var coordenadorPapPremiumId = 129;

        assertThat(repository
            .getAllSubordinadosComSubCanalId(coordenadorPapPremiumId))
            .hasSize(3)
            .extracting("nome", "subCanalId")
            .containsExactlyInAnyOrder(
                tuple("ASSISTENTE OPERACAO PAP PREMIUM", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("VENDEDOR OPERACAO PAP_PREMIUM", ETipoCanal.PAP_PREMIUM.getId()),
                tuple("SUPERVISOR OPERACAO PAP PREMIUM", ETipoCanal.PAP_PREMIUM.getId())
            );
    }
}
