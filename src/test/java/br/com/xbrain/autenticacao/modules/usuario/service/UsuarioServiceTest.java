package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioComLoginNetSalesResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioExecutivoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSituacaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;
    @Mock
    private UsuarioRepository repository;

    private static UsuarioExecutivoResponse umUsuarioExecutivo() {
        return new UsuarioExecutivoResponse(1, "bakugo@teste.com", "BAKUGO");
    }

    private static UsuarioSituacaoResponse umUsuarioSituacaoResponse(Integer id, String nome, ESituacao situacao) {
        return UsuarioSituacaoResponse
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }

    @Test
    public void getSubclustersUsuario_deveConverterORetornoEmSelectResponse_conformeListaDeSubclusters() {
        when(repository.getSubclustersUsuario(anyInt()))
            .thenReturn(List.of(
                SubCluster.of(1, "TESTE1"),
                SubCluster.of(2, "TESTE2")));

        assertThat(service.getSubclusterUsuario(1))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "TESTE1"),
                tuple(2, "TESTE2"));
    }

    @Test
    public void buscarExecutivosPorSituacao_deveRetornarOsExecutivos() {
        when(repository.findAllExecutivosBySituacao(eq(ESituacao.A)))
            .thenReturn(List.of(umUsuarioExecutivo()));

        assertThat(service.buscarExecutivosPorSituacao(ESituacao.A))
            .hasSize(1)
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "BAKUGO"));
    }

    @Test
    public void findById_deveRetornarUsuarioResponse_quandoSolicitado() {
        when(repository.findById(1))
            .thenReturn(Optional.of(Usuario.builder()
                .id(1)
                .nome("RENATO")
                .build()));

        assertThat(service.findById(1))
            .extracting("id", "nome")
            .containsExactly(1, "RENATO");
    }

    @Test
    public void findById_deveRetornarException_quandoNaoEncontrarUsuarioById() {
        when(repository.findById(1))
            .thenReturn(Optional.empty());

        assertThatCode(() -> service.findById(1))
            .hasMessage("Usuário não encontrado.")
            .isInstanceOf(ValidacaoException.class);
    }

    @Test
    public void findUsuariosByCodigoCargo_deveRetornarUsuariosAtivos_peloCodigoDoCargo() {
        when(repository.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .thenReturn(umaListaUsuariosExecutivosAtivo());

        assertThat(service.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .extracting("id", "nome", "email", "codigoNivel", "codigoCargo", "codigoDepartamento", "situacao")
            .containsExactly(
                tuple(1, "JOSÉ", "JOSE@HOTMAIL.COM", CodigoNivel.AGENTE_AUTORIZADO,
                    CodigoCargo.EXECUTIVO, CodigoDepartamento.AGENTE_AUTORIZADO, ESituacao.A),
                tuple(2, "HIGOR", "HIGOR@HOTMAIL.COM", CodigoNivel.AGENTE_AUTORIZADO,
                    CodigoCargo.EXECUTIVO, CodigoDepartamento.AGENTE_AUTORIZADO, ESituacao.A));

        verify(repository, times(1)).findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO);
    }

    private List<Usuario> umaListaUsuariosExecutivosAtivo() {
        return List.of(
            Usuario.builder()
                .id(1)
                .nome("JOSÉ")
                .email("JOSE@HOTMAIL.COM")
                .situacao(ESituacao.A)
                .departamento(Departamento.builder()
                    .id(1)
                    .codigo(CodigoDepartamento.AGENTE_AUTORIZADO)
                    .build())
                .cargo(Cargo.builder()
                    .id(1)
                    .codigo(CodigoCargo.EXECUTIVO)
                    .nivel(Nivel.builder()
                        .id(1)
                        .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                        .build())
                    .build())
                .build(),
            Usuario.builder()
                .id(2)
                .nome("HIGOR")
                .email("HIGOR@HOTMAIL.COM")
                .situacao(ESituacao.A)
                .departamento(Departamento.builder()
                    .id(1)
                    .codigo(CodigoDepartamento.AGENTE_AUTORIZADO)
                    .build())
                .cargo(Cargo.builder()
                    .id(1)
                    .codigo(CodigoCargo.EXECUTIVO)
                    .nivel(Nivel.builder()
                        .id(1)
                        .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                        .build())
                    .build())
                .build()
        );
    }

    @Test
    public void findUsuariosByIds_deveRetonarUsuarios_quandoForPassadoIdsDosUsuarios() {
        when(repository.findUsuariosByIds(any()))
            .thenReturn(List.of(
                umUsuarioSituacaoResponse(1, "JONATHAN", ESituacao.A),
                umUsuarioSituacaoResponse(2, "FLAVIA", ESituacao.I)));

        assertThat(service.findUsuariosByIds(List.of(1, 2)))
            .extracting("id", "nome", "situacao")
            .containsExactlyInAnyOrder(
                tuple(1, "JONATHAN", ESituacao.A),
                tuple(2, "FLAVIA", ESituacao.I));
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSales() {
        var umUsuarioComLogin = 1000;

        when(repository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(umUsuarioComLoginNetSales(umUsuarioComLogin)));

        var response = service.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "ATIVO_LOCAL_PROPRIO",
                "123.456.887-91");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSalesEForOperador() {
        var umUsuarioComLogin = 1000;
        var user = umUsuarioComLoginNetSales(umUsuarioComLogin);
        user.setCanais(Set.of(ECanal.AGENTE_AUTORIZADO));
        user.getCargo().setNivel(Nivel.builder().codigo(CodigoNivel.OPERACAO).build());
        when(repository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(user));

        var response = service.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "OPERACAO_AGENTE_AUTORIZADO",
                "123.456.887-91");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSalesEForReceptivo() {
        var umUsuarioComLogin = 1000;
        var user = umUsuarioComLoginNetSales(umUsuarioComLogin);
        user.setOrganizacao(Organizacao.builder().codigo("ATENTO").build());
        user.getCargo().setNivel(Nivel.builder().codigo(CodigoNivel.RECEPTIVO).build());
        when(repository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(user));

        var response = service.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "RECEPTIVO_ATENTO",
                "123.456.887-91");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveLancarException_seUsuarioNaoPossuirLoginNetSales() {
        var umUsuarioSemLogin = 1001;

        when(repository.findById(umUsuarioSemLogin))
            .thenReturn(Optional.of(umUsuarioSemLoginNetSales(umUsuarioSemLogin)));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getUsuarioByIdComLoginNetSales(umUsuarioSemLogin))
            .withMessage("Usuário não possui login NetSales válido.");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveLancarException_seUsuarioNaoEncontrado() {
        var umUsuarioInexistente = 1002;

        when(repository.findById(umUsuarioInexistente))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getUsuarioByIdComLoginNetSales(umUsuarioInexistente))
            .withMessage("Usuário não encontrado.");
    }

    private Usuario umUsuarioComLoginNetSales(int id) {
        return Usuario.builder()
            .id(id)
            .nome("UM USUARIO COM LOGIN")
            .loginNetSales("UM LOGIN NETSALES")
            .cargo(Cargo.builder()
                .codigo(CodigoCargo.VENDEDOR_ATIVO_LOCAL_PROPRIO)
                .nivel(Nivel.builder().codigo(CodigoNivel.ATIVO_LOCAL_PROPRIO).build())
                .build())
            .cpf("123.456.887-91")
            .situacao(ESituacao.A)
            .build();
    }

    private Usuario umUsuarioSemLoginNetSales(int id) {
        var usuario = umUsuarioComLoginNetSales(id);
        usuario.setLoginNetSales(null);
        return usuario;
    }
}