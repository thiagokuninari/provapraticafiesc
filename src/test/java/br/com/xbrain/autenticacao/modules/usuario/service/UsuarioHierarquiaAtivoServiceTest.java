package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaSupervisorDto;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("oracle-test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_sites_hierarquiaOracle.sql"})
public class UsuarioHierarquiaAtivoServiceTest {

    @SpyBean
    private UsuarioHierarquiaAtivoService service;
    @SpyBean
    private UsuarioSiteService usuarioSiteService;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private EquipeVendaD2dService equipeVendasService;

    @Test
    public void getSupervisoresDaHierarquia_deveRetornarSupervisoresENaoFiltrarHierarquia_quandoVendedorPertencerAoLogado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(99, CodigoCargo.ADMINISTRADOR,
            CodigoNivel.XBRAIN, CodigoDepartamento.ADMINISTRADOR));
        var usuarioNomeResponses = service.supervisoresDaHierarquia(umUsuarioHieraquiaFiltro(103, 110, null));
        verify(service, times(0)).filtrarHierarquia(any(), any());
        assertThat(usuarioNomeResponses)
            .hasSize(1);
    }

    @Test
    public void getSupervisoresDaHierarquia_deveRetornarSupervisoresEFiltrarHierarquia_quandoVendedorPertencerAoLogado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(101, CodigoCargo.GERENTE_OPERACAO,
            CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL));
        var usuarioNomeResponses = service.supervisoresDaHierarquia(umUsuarioHieraquiaFiltro(103, 110, null));
        verify(service, times(1)).filtrarHierarquia(any(), any());
        assertThat(usuarioNomeResponses)
            .hasSize(1);
    }

    @Test
    public void semHierarquia_deveRetornarListaVazia_quandoUsuarioLogadoNaoPossuirSupervisorEmSuaHieraquia() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(135, CodigoCargo.GERENTE_OPERACAO,
            CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL));
        var usuarioNomeResponses = service.supervisoresDaHierarquia(umUsuarioHieraquiaFiltro(103, 110, null));
        assertThat(usuarioNomeResponses)
            .hasSize(0);
    }

    @Test
    public void vendedores_deveRetornarVendedoresSemEquipe_quandoSiteIdEUsuarioLogadoTiverPermissaoSobreUsuario() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(103, CodigoCargo.GERENTE_OPERACAO,
            CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL));
        var usuarioNomeResponses = service.vendedoresDaHierarquia(umUsuarioHieraquiaFiltro(null, 110, null));
        assertThat(usuarioNomeResponses)
            .extracting(UsuarioNomeResponse::getId)
            .containsExactlyInAnyOrder(109, 115);
    }

    @Test
    public void vendedores_deveRetornarVendedoresSemEquipeEUsuariosAtivos_quandoUsuarioLogadoTiverPermissaoSobreUsuario() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(103, CodigoCargo.GERENTE_OPERACAO,
            CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL));
        var filtro = umUsuarioHieraquiaFiltro(null, 110, null);
        filtro.setBuscarInativo(false);
        var usuarioNomeResponses = service.vendedoresDaHierarquia(filtro);
        assertThat(usuarioNomeResponses)
            .extracting(UsuarioNomeResponse::getId)
            .containsExactlyInAnyOrder(109);
    }

    @Test
    public void vendedores_deveRetornarVendedoresPorEquipe_quandoFiltroEquipe() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(103, CodigoCargo.GERENTE_OPERACAO,
            CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL));
        when(equipeVendasService.getEquipeVendas(any())).thenReturn(List.of(new EquipeVendaDto(114, "Equipe", null)));
        when(equipeVendasService.getEquipeVendasComSupervisor(any()))
            .thenReturn(List.of(new EquipeVendaSupervisorDto(114, "Equipe", null, null)));
        var filtro = umUsuarioHieraquiaFiltro(null, 110, 114);
        filtro.setBuscarInativo(false);
        var usuarioNomeResponses = service.vendedoresDaHierarquia(filtro);
        assertThat(usuarioNomeResponses)
            .extracting(UsuarioNomeResponse::getId)
            .containsExactlyInAnyOrder(109);
    }

    @Test
    public void vendedores_deveRetornarListVazia_quandoFiltroVendedorNaoPertencerAquipeFiltrada() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(103, CodigoCargo.GERENTE_OPERACAO,
            CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL));
        when(equipeVendasService.getEquipeVendas(any())).thenReturn(List.of(new EquipeVendaDto(166, "Equipe", null)));
        when(equipeVendasService.getEquipeVendasComSupervisor(any()))
            .thenReturn(List.of(new EquipeVendaSupervisorDto(166, "Equipe", null, null)));
        var filtro = umUsuarioHieraquiaFiltro(null, 110, 114);
        filtro.setBuscarInativo(false);
        var usuarioNomeResponses = service.vendedoresDaHierarquia(filtro);
        assertThat(usuarioNomeResponses)
            .hasSize(0);
    }

    @Test
    public void buscarVendedoresPorSupervisor_quandoUsuarioLogadoForAssistente() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(108, CodigoCargo.ASSISTENTE_OPERACAO,
            CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL));
        var usuarioNomeResponses = service.vendedoresDaHierarquia(umUsuarioHieraquiaFiltro(null, 110, null));
        verify(usuarioSiteService, times(1)).getVendedoresPorCargoUsuario(any(), any());
        assertThat(usuarioNomeResponses)
            .extracting(UsuarioNomeResponse::getId)
            .containsExactlyInAnyOrder(109, 115);

    }

    @Test
    public void buscarTodosVendedores_quandoUsuarioLogadoForAdmin_naoDeveFiltrarUsuario() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(99, CodigoCargo.ADMINISTRADOR,
            CodigoNivel.XBRAIN, CodigoDepartamento.ADMINISTRADOR));
        var usuarioNomeResponses = service.vendedoresDaHierarquia(umUsuarioHieraquiaFiltro(null, 110, null));
        verify(usuarioSiteService, times(0)).getVendedoresPorCargoUsuario(any(), any());
        assertThat(usuarioNomeResponses)
            .extracting(UsuarioNomeResponse::getId)
            .containsExactlyInAnyOrder(109, 115);

    }

    private UsuarioHierarquiaFiltros umUsuarioHieraquiaFiltro(Integer coordenadorId, Integer siteId, Integer equipeId) {
        return UsuarioHierarquiaFiltros.builder()
            .coordenadorId(coordenadorId)
            .siteId(siteId)
            .equipeVendaId(equipeId)
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticado(Integer id, CodigoCargo codigoCargo,
                                                         CodigoNivel codigoNivel, CodigoDepartamento codigoDepartamento) {
        return UsuarioAutenticado.builder()
            .id(id)
            .cargoCodigo(codigoCargo)
            .canais(Collections.singleton(ECanal.ATIVO_PROPRIO))
            .nivelCodigo(codigoNivel.name())
            .usuario(Usuario.builder()
                .id(id)
                .cargo(null)
                .build())
            .departamentoCodigo(codigoDepartamento)
            .build();
    }
}
