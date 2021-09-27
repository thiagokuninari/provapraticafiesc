package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.dto.SiteFiltros;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.ADMINISTRADOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.DIRETOR_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.MSO_CONSULTOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.OPERACAO_TELEVENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.SUPERVISOR_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.buildUsuario;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.buildUsuarioMso;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

@ActiveProfiles("oracle-test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = {"classpath:/tests_sites_hierarquia.sql"})
@Transactional
public class SiteServiceOracle {

    private static final String[] extract = {"id", "nome", "timeZone", "situacao"};

    @Autowired
    private SiteService siteService;
    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    public void getAbaixoHierarquia_deveRetornarUmSite_quandoDiretorPossuirCoordenadorOuSupervisorComSiteVinculado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(100, CodigoCargo.DIRETOR_OPERACAO, CodigoDepartamento.COMERCIAL));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A)
            );
    }

    @Test
    public void getHierarquia_deveRetornarUmSite_quandoGerentePossuirCoordenadorOuSupervisorComSiteVinculado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(101, CodigoCargo.GERENTE_OPERACAO, CodigoDepartamento.COMERCIAL));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A)
            );
    }

    @Test
    public void getSiteProprio_deveRetornarUmSite_quandoSupervisorTiverSiteVinculadoAEle() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(102, CodigoCargo.SUPERVISOR_OPERACAO, CodigoDepartamento.COMERCIAL));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A)
            );
    }

    @Test
    public void getSiteProprio_deveRetornarUmSite_quandoCoordenadorTiverSiteVinculadoAEle() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(103, CodigoCargo.COORDENADOR_OPERACAO, CodigoDepartamento.COMERCIAL));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A)
            );
    }

    @Test
    public void getSiteDoSuperior_deveRetornarUmSite_quandoAssistenteTiverVinculoComCoordenadorComSite() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(108, CodigoCargo.ASSISTENTE_OPERACAO, CodigoDepartamento.COMERCIAL));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A)
            );
    }

    @Test
    public void getTodosSites_devePegarTodosOsSitesMesmoSemVinculo_quandoUsuarioForMso() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoMso(98, CodigoCargo.MSO_CONSULTOR, CodigoDepartamento.COMERCIAL));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A),
                tuple(111, "Manaus", ETimeZone.AMT, ESituacao.A),
                tuple(112, "Site Inativo", ETimeZone.FNT, ESituacao.I)
            );
    }

    @Test
    public void getTodosSites_devePegarTodosOsSitesMesmoSemVinculo_quandoUsuarioForAdmin() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoMso(99, CodigoCargo.ADMINISTRADOR, CodigoDepartamento.COMERCIAL));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A),
                tuple(111, "Manaus", ETimeZone.AMT, ESituacao.A),
                tuple(112, "Site Inativo", ETimeZone.FNT, ESituacao.I)
            );
    }

    @Test
    public void retornaVazio_deveRetornarVazio_quandoDiretorNaoTiverSitesCadastradosAbaixoDeSuaHierarquia() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(104, CodigoCargo.DIRETOR_OPERACAO, CodigoDepartamento.COMERCIAL));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
               .hasSize(0);
    }

    @Test
    public void retornaVazio_deveRetornarVazio_quandoUsuarioNaoTiverSitesVinculado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(106, CodigoCargo.SUPERVISOR_OPERACAO, CodigoDepartamento.COMERCIAL));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
                .hasSize(0);
    }

    @Test
    public void getSitesAtivos_deveRetornarSelectResponseComSitesVinculados_quandoSituacaoSiteForAtivo() {
        assertThat(siteService.getSitesPorPermissao(umUsuario(100, DIRETOR_OPERACAO, CodigoDepartamento.COMERCIAL)))
            .extracting("value", "label")
            .containsExactly(
                tuple(110, "Rio Branco")
            );
    }

    @Test
    public void getSitesVendedor_deveRetornarSitesAtivosVinculadosAoSupervisor_quandoVendedorTiverSupervisorComSiteAtivo() {
        assertThat(siteService.getSitesPorPermissao(umUsuario(102, SUPERVISOR_OPERACAO, CodigoDepartamento.COMERCIAL)))
            .extracting("value", "label")
            .containsExactly(
                tuple(110, "Rio Branco")
            );
    }

    @Test
    public void getSitesVendedor_deveRetornarSitesAtivosVinculadosAoSuperiorDoVendedor_quandoSupervisorPossuirSite() {
        assertThat(siteService.getSitesPorPermissao(umUsuario(109, OPERACAO_TELEVENDAS, CodigoDepartamento.COMERCIAL)))
            .extracting("value", "label")
            .containsExactly(
                tuple(110, "Rio Branco")
            );
    }

    @Test
    public void ignorarMso_deveRetornarListaVazia_quandoMsoNaoPossuirDepartamentoComercial() {
        assertThat(siteService.getSitesPorPermissao(umUsuario(110, MSO_CONSULTOR, CodigoDepartamento.OUVIDORIA)))
            .hasSize(0);
    }

    @Test
    public void ignorarInativos_deveIgnorarSitesInativos_quandoUsuarioTiverPermissaoParaVerTodos() {
        assertThat(siteService.getSitesPorPermissao(umUsuario(99, ADMINISTRADOR, CodigoDepartamento.COMERCIAL)))
            .hasSize(2)
            .extracting("label")
            .doesNotContain("Site Inativo");
    }

    @Test
    public void inativarSite_deveRemoverSupervisoresCoordenadoresSitesEcidadesVinculados_quandoInativarSite() {
        siteService.inativar(110);
        var siteInativado = siteService.findById(110);
        assertThat(siteInativado.getCoordenadores().isEmpty());
        assertThat(siteInativado.getSupervisores().isEmpty());
        assertThat(siteInativado.getSituacao().equals(ESituacao.I));
    }

    private Usuario umUsuario(Integer id, CodigoCargo codigoCargo, CodigoDepartamento departamento) {
        return Usuario.builder()
            .id(id)
            .cargo(Cargo.builder()
                .codigo(codigoCargo)
                .build())
            .departamento(Departamento.builder()
                .codigo(departamento)
                .build())
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticado(Integer id, CodigoCargo codigoCargo,
                                                    CodigoDepartamento codigoDepartamento) {
        return UsuarioAutenticado.builder()
                .id(id)
                .cargoCodigo(codigoCargo)
                .canais(Collections.singleton(ECanal.ATIVO_PROPRIO))
                .nivelCodigo(CodigoNivel.OPERACAO.name())
                .usuario(buildUsuario())
                .departamentoCodigo(codigoDepartamento)
                .build();
    }

    private UsuarioAutenticado umUsuarioAutenticadoMso(Integer id, CodigoCargo codigoCargo,
                                                    CodigoDepartamento codigoDepartamento) {
        return UsuarioAutenticado.builder()
            .id(id)
            .cargoCodigo(codigoCargo)
            .canais(Collections.singleton(ECanal.ATIVO_PROPRIO))
            .nivelCodigo(CodigoNivel.OPERACAO.name())
            .usuario(buildUsuarioMso())
            .departamentoCodigo(codigoDepartamento)
            .build();
    }
}
