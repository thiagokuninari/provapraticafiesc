package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquiaPk;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.COORDENADOR_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.SUPERVISOR_OPERACAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql({"classpath:/tests_usuario_repository.sql"})
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

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
    public void findAllUsuariosSemDataUltimoAcesso_deveRetornarUsuario_quandoNaoPossuirDataUltimoAcessoAndEstiverAtivo() {
        assertThat(repository.findAllUsuariosSemDataUltimoAcesso(LocalDateTime.now().minusMonths(2)))
            .extracting("id", "email")
            .containsExactlyInAnyOrder(
                tuple(100, "ADMIN@XBRAIN.COM.BR"),
                tuple(103, "CARLOS@HOTMAIL.COM"),
                tuple(104, "MARIA@HOTMAIL.COM"));
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
        assertThat(repository.findAllExecutivosAndAssistenteOperacaoDepartamentoComercial(getUsuarioPredicate().build()))
            .hasSize(4)
            .extracting("value", "text")
            .containsExactly(
                Assertions.tuple(125, "ASSISTENTE OP"),
                Assertions.tuple(107, "EXECUTIVO 1"),
                Assertions.tuple(108, "EXECUTIVO 2"),
                Assertions.tuple(124, "EXECUTIVO OP")
            );
    }

    private UsuarioPredicate getUsuarioPredicate() {
        return new UsuarioPredicate()
            .comDepartamento(List.of(3))
            .comNivel(List.of(1))
            .comCargo(List.of(2, 5));
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
    public void findIdUsuariosByCodigoCargos_deveRetornarListaIdUsuariosAtivos_pelosCodigosDosCargos() {
        assertThat(repository.findIdUsuariosByCodigoCargos(List.of(SUPERVISOR_OPERACAO, COORDENADOR_OPERACAO)))
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
    public void findAllAtivosByNivelOperacaoCanalAa_doisUsuarios_quandoAtivoECanalAgenteAutorizado() {
        assertThat(repository.findAllAtivosByNivelOperacaoCanalAa())
            .hasSize(2);
    }

    @Test
    public void obterIdsPorUsuarioCadastroId_deveRetornarListaVazia_quandoNaoEncontrarUsuarios() {
        assertThat(repository.obterIdsPorUsuarioCadastroId(1000))
            .isEmpty();
    }

    @Test
    public void obterIdsPorUsuarioCadastroId_deveRetornarListaIds_quandoEncontrarUsuarios() {
        assertThat(repository.obterIdsPorUsuarioCadastroId(100))
            .hasSize(3)
            .containsExactly(200, 300, 400);
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
}
