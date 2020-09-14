package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.dto.SiteFiltros;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

@ActiveProfiles("oracle-test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = {"classpath:/tests_sites_hierarquia.sql"})
@Transactional
public class SiteServiceIT {

    private static final String[] extract = {"id", "nome", "timeZone", "situacao"};

    @Autowired
    private SiteService siteService;

    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    public void getAbixoHierarquia_deveRetornarUmSite_quandoDiretorPossuirCoordenadorOuSupervisorComSiteVinculado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(100, CodigoCargo.DIRETOR_OPERACAO));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .hasSize(1)
            .extracting("id", "nome", "timeZone", "situacao")
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A)
            );
    }

    @Test
    public void getAbixoHierarquia_deveRetornarUmSite_quandoGerentePossuirCoordenadorOuSupervisorComSiteVinculado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(101, CodigoCargo.GERENTE_OPERACAO));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .hasSize(1)
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A)
            );
    }

    @Test
    public void getSiteProprio_deveRetornarUmSite_quandoSupervisorTiverSiteVinculadoAEle() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(102, CodigoCargo.SUPERVISOR_OPERACAO));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .hasSize(1)
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A)
            );
    }

    @Test
    public void getSiteProprio_deveRetornarUmSite_quandoCoordenadorTiverSiteVinculadoAEle() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(103, CodigoCargo.COORDENADOR_OPERACAO));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .hasSize(1)
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A)
            );
    }

    @Test
    public void getSiteDoSuperior_deveRetornarUmSite_quandoAssistenteTiverVinculoComCoordenadorComSite() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(108, CodigoCargo.ASSISTENTE_OPERACAO));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .hasSize(1)
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A)
            );
    }

    @Test
    public void getTodosSites_devePegarTodosOsSitesMesmoSemVinculo_quandoUsuarioForMso() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(98, CodigoCargo.MSO_CONSULTOR));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .hasSize(2)
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A),
                tuple(111, "Manaus", ETimeZone.AMT, ESituacao.A)
            );
    }

    @Test
    public void getTodosSites_devePegarTodosOsSitesMesmoSemVinculo_quandoUsuarioForAdmin() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(99, CodigoCargo.ADMINISTRADOR));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
            .hasSize(2)
            .extracting(extract)
            .containsExactly(
                tuple(110, "Rio Branco", ETimeZone.ACT, ESituacao.A),
                tuple(111, "Manaus", ETimeZone.AMT, ESituacao.A)
            );
    }

    @Test
    public void retornaVazio_deveRetornarVazio_quandoDiretorNaoTiverSitesCadastradosAbaixoDeSuaHierarquia() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(104, CodigoCargo.DIRETOR_OPERACAO));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
               .hasSize(0);
    }

    @Test
    public void retornaVazio_deveRetornarVazio_quandoUsuarioNaoTiverSitesVinculado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(106, CodigoCargo.SUPERVISOR_OPERACAO));
        assertThat(siteService.getAll(new SiteFiltros(), new PageRequest()))
                .hasSize(0);
    }

    private UsuarioAutenticado umUsuarioAutenticado(Integer id, CodigoCargo codigoCargo) {
        return UsuarioAutenticado.builder()
                .id(id)
                .cargoCodigo(codigoCargo)
                .canais(Collections.singleton(ECanal.ATIVO_PROPRIO))
                .nivelCodigo(CodigoNivel.OPERACAO.name())
                .build();
    }
}
