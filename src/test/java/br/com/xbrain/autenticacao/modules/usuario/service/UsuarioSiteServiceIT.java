package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepositoryImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_editar_sites.sql"})
public class UsuarioSiteServiceIT {

    @Autowired
    private UsuarioSiteService usuarioSiteService;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @SpyBean
    private UsuarioRepositoryImpl usuarioRepository;

    @Test
    @SuppressWarnings("LineLength")
    public void buscarUsuariosSitePorCargo_deveRetornarListaVazia_quandoNaoExistirSupervisoresVinculadosAoUsuarioLogadoDisponiveis() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(10220, CodigoCargo.GERENTE_OPERACAO,
            CodigoDepartamento.COMERCIAL, CodigoNivel.OPERACAO));

        doReturn(Collections.emptyList())
            .when(usuarioRepository).findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(anyInt(), any());

        assertThat(usuarioSiteService.buscarUsuariosSitePorCargo(CodigoCargo.SUPERVISOR_OPERACAO)).isEmpty();
    }

    @Test
    @SuppressWarnings("LineLength")
    public void buscarCoordenadoresDisponiveis_deveRetornarListaVazia_quandoNaoExistirCoordenadoresVinculadosAoUsuarioLogadoDisponiveis() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(10220, CodigoCargo.GERENTE_OPERACAO,
            CodigoDepartamento.COMERCIAL, CodigoNivel.OPERACAO));

        doReturn(List.of(umUsuarioNomeResponse(1)))
            .when(usuarioRepository).findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(anyInt(), any());

        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveis()).isEmpty();
    }

    @Test
    @SuppressWarnings("LineLength")
    public void buscarSupervisoresDisponiveisEVinculadosAoSite_deveRetornarSupervisoresVinculadosAoSiteEAoUsuarioLogado_quandoSolicitarComSiteIdParaEditar() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(10220, CodigoCargo.GERENTE_OPERACAO,
            CodigoDepartamento.COMERCIAL, CodigoNivel.OPERACAO));

        doReturn(List.of(umUsuarioNomeResponse(11123)))
            .when(usuarioRepository).findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(anyInt(), any());

        assertThat(usuarioSiteService.buscarSupervisoresDisponiveisEVinculadosAoSite(List.of(11122), 2))
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .contains(tuple(11123, "Supervisor operacao ativo local"));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void getSupervisoresSemSitePorCoordenadorsId_deveRetornarSupervisoresDisponiveis_quandoGerenteTiverSupervisoresSemSite() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(10220, CodigoCargo.GERENTE_OPERACAO,
            CodigoDepartamento.COMERCIAL, CodigoNivel.OPERACAO));

        doReturn(List.of(umUsuarioNomeResponse(11123), umUsuarioNomeResponse(11124)))
            .when(usuarioRepository).findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(anyInt(), any());

        assertThat(usuarioSiteService.getSupervisoresSemSitePorCoordenadorsId(List.of(11122)))
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .containsExactlyInAnyOrder(
                tuple(11123, "Supervisor operacao ativo local"),
                tuple(11124, "Supervisor2 operacao ativo local"));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void buscarCoordenadoresDisponiveisEVinculadosAoSite_deveRetornarCoordenadoresVinculadosAoSiteEAoUsuarioLogado_quandoSolicitarComSiteIdParaEditar() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(10220, CodigoCargo.GERENTE_OPERACAO,
            CodigoDepartamento.COMERCIAL, CodigoNivel.OPERACAO));

        doReturn(List.of(umUsuarioNomeResponse(11122))).when(usuarioRepository)
            .findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(anyInt(), any());

        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveisEVinculadosAoSite(1))
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .contains(tuple(11122, "Coordenador operacao ativo local"));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void buscarCoordenadoresDisponiveisEVinculadosAoSite_deveRetornarCoordenadoresDisponiveisEVinculadosAoSiteEUsuarioLogado_quandoEditarSite() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(10220, CodigoCargo.GERENTE_OPERACAO,
            CodigoDepartamento.COMERCIAL, CodigoNivel.OPERACAO));

        doReturn(List.of(umUsuarioNomeResponse(11122), umUsuarioNomeResponse(11125)))
            .when(usuarioRepository).findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(anyInt(), any());

        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveisEVinculadosAoSite(1))
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .contains(
                tuple(11122, "Coordenador operacao ativo local"),
                tuple(11125, "Coordenador2 operacao ativo local"));
    }

    @Test
    public void buscarCoordenadoresDisponiveis_deveRetornarTodosCoordenadoresDisponiveis_quandoUsuarioLogadoForMso() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoMSO(1));

        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveis())
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .contains(
                tuple(11125, "Coordenador2 operacao ativo local"),
                tuple(11126, "Coordenador sem site operacao ativo local")
            );
    }

    @Test
    @SuppressWarnings("LineLength")
    public void getSupervisoresSemSitePorCoordenadorsId_deveRetornarTodosSupervisoresDisponiveisDoCoordenador_quandoUsuarioLogadoForMso() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoMSO(1));

        assertThat(usuarioSiteService.getSupervisoresSemSitePorCoordenadorsId(List.of(11122)))
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .contains(
                tuple(11124, "Supervisor2 operacao ativo local")
            );
    }

    @Test
    @SuppressWarnings("LineLength")
    public void buscarCoordenadoresDisponiveis_deveIgnorarCoordenadoresSemVinculoComUserLogado_quandoCoordenadorNaoFazerParteDaHierarquia() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(10220, CodigoCargo.GERENTE_OPERACAO,
            CodigoDepartamento.COMERCIAL, CodigoNivel.OPERACAO));

        doReturn(List.of(umUsuarioNomeResponse(11155), umUsuarioNomeResponse(11145)))
            .when(usuarioRepository).findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(anyInt(), any());

        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveis()).isEmpty();
    }

    @Test
    public void buscarCoordenadoresDisponiveis_deveRetornarListaVazia_quandoHieraquiaNaoPossuirCoordenador() {

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(10219, CodigoCargo.GERENTE_OPERACAO,
            CodigoDepartamento.COMERCIAL, CodigoNivel.OPERACAO));

        doReturn(Collections.emptyList())
            .when(usuarioRepository).findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(anyInt(), any());

        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveis()).isEmpty();
    }

    @Test
    public void getSupervisoresSemSitePorCoordenadorsId_deveRetornarListaVazia_quandoHieraquiaNaoPossuirSupervisor() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(10219, CodigoCargo.GERENTE_OPERACAO,
            CodigoDepartamento.COMERCIAL, CodigoNivel.OPERACAO));

        doReturn(Collections.emptyList())
            .when(usuarioRepository).findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(anyInt(), any());

        assertThat(usuarioSiteService.getSupervisoresSemSitePorCoordenadorsId(List.of(100))).isEmpty();
    }

    private UsuarioNomeResponse umUsuarioNomeResponse(Integer idUsuario) {
        return UsuarioNomeResponse.builder()
            .nome("Usuario1")
            .id(idUsuario)
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticado(Integer id, CodigoCargo codigoCargo,
                                                    CodigoDepartamento codigoDepartamento,
                                                    CodigoNivel codigoNivel) {
        return UsuarioAutenticado.builder()
            .id(id)
            .cargoCodigo(codigoCargo)
            .canais(Collections.singleton(ECanal.ATIVO_PROPRIO))
            .nivelCodigo(codigoNivel.name())
            .usuario(Usuario.builder()
                .cargo(null)
                .build())
            .departamentoCodigo(codigoDepartamento)
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticadoMSO(Integer id) {
        return UsuarioAutenticado.builder()
            .id(id)
            .cargoCodigo(CodigoCargo.MSO_CONSULTOR)
            .canais(Collections.singleton(ECanal.ATIVO_PROPRIO))
            .nivelCodigo(CodigoNivel.MSO.name())
            .usuario(Usuario.builder()
                .cargo(umCargoMsoConsultor())
                .build())
            .build();
    }

    private Cargo umCargoMsoConsultor() {
        return Cargo.builder()
            .id(22)
            .codigo(CodigoCargo.MSO_CONSULTOR)
            .nivel(umNivelMso())
            .build();
    }

    private Nivel umNivelMso() {
        return Nivel.builder()
            .id(2)
            .codigo(CodigoNivel.MSO)
            .build();
    }
}
