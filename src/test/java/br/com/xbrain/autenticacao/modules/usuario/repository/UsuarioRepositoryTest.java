package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.PublicoAlvoComunicadoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquiaPk;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.XBRAIN;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelMso;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.COORDENADOR_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.SUPERVISOR_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql({"classpath:/tests_usuario_repository.sql"})
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;
    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void getSubclustersUsuario_deveRetornarOsSubclusters_somenteAtivosSemDuplicar() {

        assertThat(repository.getSubclustersUsuario(100))
            .extracting("id", "nome")
            .containsExactly(
                tuple(26600, "CHAPECÓ"),
                tuple(189, "LONDRINA"));

        assertThat(repository.getSubclustersUsuario(101))
            .extracting("id", "nome")
            .containsExactly(
                tuple(164, "BRI - LINS - SP"));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void findAllUsuariosSemDataUltimoAcessoAndDataReativacaoDepoisTresDias_deveRetornarUsuario_quandoNaoPossuirDataUltimoAcessoAndEstiverAtivoComDataReativacaoNullComDataReativacaoTresDiasDepois() {
        assertThat(repository.findAllUsuariosSemDataUltimoAcessoAndDataReativacaoDepoisTresDiasAndNotViabilidade(LocalDateTime.now().minusMonths(2)
            ))
            .extracting("id", "email", "nivelCodigo")
            .containsExactlyInAnyOrder(tuple(100, "ADMIN@XBRAIN.COM.BR", XBRAIN), tuple(104, "MARIA@HOTMAIL.COM", XBRAIN));
    }

    @Test
    public void getUfsUsuario_deveRetornarOsEstados_somenteAtivosSemDuplicar() {

        assertThat(repository.getUfsUsuario(100))
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "PARANA"),
                tuple(22, "SANTA CATARINA"));

        assertThat(repository.getUfsUsuario(101))
            .extracting("id", "nome")
            .containsExactly(
                tuple(2, "SAO PAULO"));
    }

    @Test
    public void findAllExecutivosDosIdsCoordenador_deveRetornarExecutivosEspecificos_quandoUsuarioForCoordenador() {
        assertThat(repository.findAllExecutivosDosIdsCoordenadorGerente(List.of(107, 108, 109), 109))
            .hasSize(2)
            .extracting("value", "text")
            .containsExactly(
                tuple(107, "EXECUTIVO 1"),
                tuple(108, "EXECUTIVO 2"));
    }

    @Test
    public void findAllResponsaveisDdd_deveRetornarResponsaveis_quandoFeitaRequisicao() {
        assertThat(repository.findAllResponsaveisDdd())
            .hasSize(9)
            .extracting("value", "text")
            .containsExactlyInAnyOrder(
                tuple(220, "ANALISTA OP"),
                tuple(125, "ASSISTENTE OP"),
                tuple(109, "COORDENADOR"),
                tuple(107, "EXECUTIVO 1"),
                tuple(108, "EXECUTIVO 2"),
                tuple(124, "EXECUTIVO OP"),
                tuple(221, "GERENTE OP"),
                tuple(110, "HUNTER 1"),
                tuple(111, "HUNTER 2")
            );
    }

    @Test
    public void findAllExecutivosDosIdsCoordenador_deveRetornarListaVazia_quandoExecutivoNaoPertencerAoCoordenador() {
        assertThat(repository.findAllExecutivosDosIdsCoordenadorGerente(List.of(100, 101, 102), 109))
            .hasSize(0)
            .isEmpty();
    }

    @Test
    public void findAllExecutivosBySituacao_deveRetornarExecutivosAtivos() {
        assertThat(repository.findAllExecutivosBySituacao(ESituacao.A))
            .extracting("id", "email")
            .containsExactly(
                tuple(107, "EXECUTIVO1@TESTE.COM"),
                tuple(108, "EXECUTIVO2@TESTE.COM"),
                tuple(110, "EXECUTIVOHUNTER1@TESTE.COM"),
                tuple(111, "EXECUTIVOHUNTER2@TESTE.COM"),
                tuple(117, "EXECUTIVOHUNTER1@TESTE.COM"),
                tuple(118, "EXECUTIVOHUNTER2@TESTE.COM"),
                tuple(124, "EXECUTIVOOP@TESTE.COM"));
    }

    @Test
    public void findAllExecutivosBySituacao_deveRetornarExecutivosInativos() {
        assertThat(repository.findAllExecutivosBySituacao(ESituacao.I))
            .extracting("id", "email")
            .containsExactlyInAnyOrder(
                tuple(112, "EXECUTIVOHUNTER3@TESTE.COM"),
                tuple(113, "RENATO@TESTE.COM"),
                tuple(119, "EXECUTIVOHUNTER3@TESTE.COM"));
    }

    @Test
    public void findUsuarioByIds_deveRetornarUsuarios_quandoForPassadoIdsDosUsuarios() {
        assertThat(repository.findUsuariosByIds(List.of(107, 108, 110, 111)))
            .extracting("id", "nome")
            .containsExactly(
                tuple(107, "EXECUTIVO 1"),
                tuple(108, "EXECUTIVO 2"),
                tuple(110, "HUNTER 1"),
                tuple(111, "HUNTER 2"));
    }

    @Test
    public void findUsuariosByCodigoCargo_deveRetornarDoisUsuariosAtivos_peloCodigoDoCargo() {
        assertThat(repository.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .hasSize(3)
            .extracting("id", "nome", "email", "situacao")
            .containsExactly(
                tuple(107, "EXECUTIVO 1", "EXECUTIVO1@TESTE.COM", A),
                tuple(108, "EXECUTIVO 2", "EXECUTIVO2@TESTE.COM", A),
                tuple(124, "EXECUTIVO OP", "EXECUTIVOOP@TESTE.COM", A));
    }

    @Test
    public void findIdUsuariosAtivosByCodigoCargos_deveRetornarListaIdUsuariosAtivos_pelosCodigosDosCargos() {
        assertThat(repository.findIdUsuariosAtivosByCodigoCargos(List.of(SUPERVISOR_OPERACAO, COORDENADOR_OPERACAO)))
            .containsExactly(109, 114, 115, 116);
    }

    @Test
    public void getUsuarioSuperior_usuarioHierarquia_quandoBuscarUsuarioSuperiorDoUsuarioIdInformado() {
        assertThat(repository.getUsuarioSuperior(115))
            .isEqualTo(Optional.of(
                UsuarioHierarquia.builder()
                    .dataCadastro(LocalDateTime.of(2020, 4, 3, 12, 0))
                    .usuario(new Usuario(115))
                    .usuarioCadastro(new Usuario(113))
                    .usuarioHierarquiaPk(new UsuarioHierarquiaPk(115, 113))
                    .usuarioSuperior(new Usuario(113))
                    .build()));
    }

    @Test
    public void findUsuariosAtivosOperacaoComercialByCargoId_doisUsuarios_quandoAtivoECanalAgenteAutorizado() {
        assertThat(repository.findUsuariosAtivosOperacaoComercialByCargoId(95))
            .hasSize(2)
            .extracting("id", "nome", "email")
            .containsExactly(
                tuple(110, "HUNTER 1", "EXECUTIVOHUNTER1@TESTE.COM"),
                tuple(111, "HUNTER 2", "EXECUTIVOHUNTER2@TESTE.COM"));
    }

    @Test
    public void findUsuariosAtivosOperacaoComercialByCargoId_deveRetornarListaVazia_quandoNaoEncontrarCargo() {
        assertThat(repository.findUsuariosAtivosOperacaoComercialByCargoId(1000))
            .isEmpty();
    }

    @Test
    public void findAllAtivosByNivelOperacaoCanalAa_doisUsuarios_quandoAtivoECanalAgenteAutorizado() {
        assertThat(repository.findAllAtivosByNivelOperacaoCanalAa())
            .hasSize(9);
    }

    @Test
    public void obterIdsPorUsuarioCadastroId_deveRetornarListaVazia_quandoNaoEncontrarUsuarios() {
        assertThat(repository.obterIdsPorUsuarioCadastroId(1000))
            .isEmpty();
    }

    @Test
    public void obterIdsPorUsuarioCadastroId_deveRetornarListaIds_quandoEncontrarUsuarios() {
        assertThat(repository.obterIdsPorUsuarioCadastroId(100))
            .hasSize(6)
            .containsExactly(200, 300, 400, 500, 600, 700);
    }

    @Test
    public void buscarUsuarioSituacao_listaVazia_seNaoHouverResultados() {
        assertThat(repository.buscarUsuarioSituacao(new UsuarioPredicate().comIds(List.of(999999999)).build()))
            .isEmpty();
    }

    @Test
    public void buscarUsuarioSituacao_listaDeUsuarioSituacao_seHouverResultados() {
        assertThat(repository.buscarUsuarioSituacao(new UsuarioPredicate().comIds(List.of(200, 300, 400)).build()))
            .extracting("id", "nome", "situacao")
            .containsExactly(
                tuple(200, "USUARIO 200", A),
                tuple(300, "USUARIO 300", A),
                tuple(400, "USUARIO 400", A)
            );
    }

    @Test
    public void findAllVendedoresReceptivos_deveRetornarTodosVendedoresReceptivos_seHouver() {
        assertThat(repository.findAllVendedoresReceptivos())
            .extracting("id", "nome")
            .containsExactlyInAnyOrder(
                tuple(121, "VR 1"),
                tuple(122, "VR 2"),
                tuple(123, "VR 3")
            );
    }

    @Test
    public void findAllVendoresReceptivosByIds_deveRetornarVendedoresReceptivos_quandoTerIdPassado() {
        assertThat(repository.findAllVendedoresReceptivosByIds(List.of(100, 121, 123)))
            .extracting("id", "nome")
            .containsExactlyInAnyOrder(
                tuple(100, "ADMIN"),
                tuple(121, "VR 1"),
                tuple(123, "VR 3")
            );
    }

    @Test
    @SuppressWarnings("LineLength")
    public void findAllUltimoAcessoUsuariosComDataReativacaoDepoisTresDiasAndNotViabilidade_deveRetornarUsuario_quandoNaoPossuirDataUltimoAcessoAndEstiverAtivoComDataReativacaoNullComDataReativacaoTresDiasDepois() {
        Assertions.assertThat(repository.findAllUltimoAcessoUsuariosComDataReativacaoDepoisTresDiasAndNotViabilidade(LocalDateTime.now().minusMonths(2)))
            .extracting("id", "email", "nivelCodigo")
            .containsExactlyInAnyOrder(
                tuple(114, "SUPERVISOR@TESTE.COM", OPERACAO),
                tuple(115, "SUPERVISOR@TESTE.COM", OPERACAO),
                tuple(116, "SUPERVISOR@TESTE.COM", OPERACAO),
                tuple(217, "VIABILIDADE@TESTE.COM", XBRAIN));
    }

    @Test
    public void getUsuariosOperacaoCanalAa_deveRetornarUsuariosOpNivelAa() {
        assertThat(repository.getUsuariosOperacaoCanalAa(CodigoNivel.OPERACAO))
            .hasSize(10);
    }

    @Test
    public void findAllIdsBySituacaoAndIdsIn_deveRetornarListaIdsAtivos_quandoSolicitado() {
        var listaIds = List.of(100, 101, 102, 103, 104, 110, 111, 112, 113, 114, 115);
        var predicate = new UsuarioPredicate().comUsuariosIds(listaIds).build();
        assertThat(repository.findAllIdsBySituacaoAndIdsIn(A, predicate))
            .hasSize(8)
            .isEqualTo(List.of(100, 101, 103, 104, 110, 111, 114, 115));
    }

    @Test
    public void findAllIds_listaVazia_quandoInformadoNovaRegional() {
        var filtros = PublicoAlvoComunicadoFiltros.builder()
            .todoCanalAa(false)
            .todoCanalD2d(false)
            .comUsuariosLogadosHoje(false)
            .regionalId(1027)
            .usuarioService(usuarioService)
            .usuarioAutenticado(umUsuarioAutenticadoNivelMso())
            .build();
        assertThat(repository.findAllIds(filtros)).isEmpty();
    }

    @Test
    public void findAllNomesIds_listaVazia_quandoInformadoNovaRegional() {
        var filtros = PublicoAlvoComunicadoFiltros.builder()
            .todoCanalAa(false)
            .todoCanalD2d(false)
            .comUsuariosLogadosHoje(false)
            .regionalId(1027)
            .usuarioService(usuarioService)
            .usuarioAutenticado(umUsuarioAutenticadoNivelMso())
            .build();
        assertThat(repository.findAllNomesIds(filtros)).isEmpty();
    }

    @Test
    @Sql({"classpath:/tests_usuario_subcanal_repository.sql"})
    public void getSubCanaisByUsuarioIds_deveRetornarSetDeSubCanais_sePossuirSubCanais() {
        assertThat(repository.getSubCanaisByUsuarioIds(List.of(126, 127, 128)))
            .hasSize(4)
            .extracting("id", "codigo")
            .containsExactlyInAnyOrder(
                tuple(1, PAP),
                tuple(2, PAP_PME),
                tuple(3, PAP_PREMIUM),
                tuple(4, INSIDE_SALES_PME)
            );
    }

    @Test
    public void getSubCanaisByUsuarioIds_deveRetornarVazio_seNaoPossuirSubCanais() {
        assertThat(repository.getSubCanaisByUsuarioIds(List.of(100, 121, 123))).isEmpty();
    }

    @Test
    @Sql({"classpath:/tests_usuario_subcanal_repository.sql"})
    public void findByPredicate_deveRetornarUsuario_seSolicitado() {
        var predicate = new UsuarioPredicate().comCpf("51466849606");

        assertThat(repository.findByPredicate(predicate.build()))
            .get()
            .extracting("id", "nome", "cpf")
            .containsExactlyInAnyOrder(132, "SUPERVISOR OPERACAO PAP PREMIUM", "51466849606");
    }

    @Test
    public void findByPredicate_deveRetornarEmpty_seUsuarioNaoExistir() {
        var predicate = new UsuarioPredicate().comCpf("51466849606");

        assertThat(repository.findByPredicate(predicate.build())).isEmpty();
    }

    private UsuarioPredicate getUsuarioPredicate() {
        return new UsuarioPredicate()
            .comDepartamento(List.of(3))
            .comNivel(List.of(1))
            .comCargo(List.of(2, 5));
    }

    @Test
    public void findByIdInAndCargoIn_deveRetornarUsuarios_quandoInformarIdsDosUsuariosAndCargos() {
        var cargo = Cargo.builder().id(58).codigo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS).build();
        assertThat(repository.findByIdInAndCargoIn(List.of(500, 600, 700), List.of(cargo)))
            .extracting("id", "nome", "email")
            .containsExactly(
                Assertions.tuple(500, "USUARIO 500", "USUARIO_500@TESTE.COM"),
                Assertions.tuple(600, "USUARIO 600", "USUARIO_600@TESTE.COM"),
                Assertions.tuple(700, "USUARIO 700", "USUARIO_700@TESTE.COM"));
    }

    @Test
    public void findByIdInAndCargoInAndSituacaoNot_deveRetornarUsuariosNaoRealocados_quandoInformarIdsAndCargosAndSituacao() {
        var cargo = Cargo.builder().id(58).codigo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS).build();
        assertThat(repository.findByIdInAndCargoInAndSituacaoNot(List.of(500, 600, 700), List.of(cargo), ESituacao.R))
            .extracting("id", "nome", "email")
            .containsExactly(
                Assertions.tuple(500, "USUARIO 500", "USUARIO_500@TESTE.COM"),
                Assertions.tuple(700, "USUARIO 700", "USUARIO_700@TESTE.COM"));
    }

    @Test
    public void getIdsUsuariosHierarquiaPorCargos_deveRetornarListaIdUsuarios_pORCodigosDosCargos() {
        assertThat(repository.getIdsUsuariosHierarquiaPorCargos(Set.of(INTERNET_VENDEDOR)))
            .containsExactly(219);
    }

    @Test
    public void findByCodigoCargoAndOrganizacaoId_deveRetornarUsuariosSelectConformeCargoEOrganizacao_quandoSolicitado() {
        assertThat(repository.findByCodigoCargoAndOrganizacaoId(VENDEDOR_RECEPTIVO, 5))
            .extracting("value", "label")
            .containsExactlyInAnyOrder(
                tuple(121, "VR 1"),
                tuple(122, "VR 2"),
                tuple(123, "VR 3"));
    }

    @Test
    public void findAllUsuariosReceptivosIdsByOrganizacaoId_deveRetornarUsuariosIdsPorOrganizacao_quandoSolicitado() {
        assertThat(repository.findAllUsuariosReceptivosIdsByOrganizacaoId(5))
            .containsExactly(121, 122, 123);
    }

    @Test
    public void findExecutivosPorCoordenadoresIds_deveRetornarExecutivosDoCoordenador_quandoSolicitado() {
        var predicate = new UsuarioPredicate()
            .comUsuariosSuperiores(List.of(109))
            .comCargo(EXECUTIVO)
            .comCanal(ECanal.AGENTE_AUTORIZADO).build();

        assertThat(repository.findExecutivosPorCoordenadoresIds(predicate))
            .hasSize(2)
            .extracting("id", "nome")
            .containsExactly(
                tuple(107, "EXECUTIVO 1"),
                tuple(108, "EXECUTIVO 2"));
    }
}
