package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioEquipeDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ActiveProfiles("oracle-test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = {"classpath:/tests_sites_hierarquiaOracle.sql"})
@Transactional
public class UsuarioSiteServiceOracle {

    @Autowired
    private UsuarioSiteService usuarioSiteService;
    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    public void filtrarVendedoresInativosHierarquia_deveRetornarApenasUsuarioAtivosHierarquiaCoordenador_quandoEParametroFalse() {
        var vendedoresAtivos = usuarioSiteService.getVendoresDoSiteIdPorHierarquiaComEquipe(110, 103, false);

        assertThat(vendedoresAtivos).hasSize(1)
            .extracting(UsuarioEquipeDto::getUsuarioId)
            .contains(109);
    }

    @Test
    public void todosHierarquia_deveRetornarTodosIncluindoInativosDaHierarquiaCoordenador_quandoEParametroTrue() {
        var vendedores = usuarioSiteService.getVendoresDoSiteIdPorHierarquiaComEquipe(110, 103, true);

        assertThat(vendedores).hasSize(2)
            .extracting(UsuarioEquipeDto::getUsuarioId)
            .contains(109, 115);
    }

    @Test
    public void filtrarVendedoresInativosHierarquia_deveRetornarApenasUsuarioAtivosHierarquiaSupervisor_quandoEParametroFalse() {
        var vendedoresAtivos = usuarioSiteService.getVendoresDoSiteIdPorHierarquiaComEquipe(110, 102, false);

        assertThat(vendedoresAtivos).hasSize(1)
            .extracting(UsuarioEquipeDto::getUsuarioId)
            .contains(109);
    }

    @Test
    public void todosHierarquia_deveRetornarTodosIncluindoInativosDaHierarquiaSupervisor_quandoEParametroTrue() {
        var vendedores = usuarioSiteService.getVendoresDoSiteIdPorHierarquiaComEquipe(110, 102, true);

        assertThat(vendedores).hasSize(2)
            .extracting(UsuarioEquipeDto::getUsuarioId)
            .contains(109, 115);
    }

    @Test
    public void filtrarVendedoresInativosHierarquia_deveRetornarApenasUsuarioAtivosHierarquiaGerente_quandoEParametroFalse() {
        var vendedoresAtivos = usuarioSiteService.getVendoresDoSiteIdPorHierarquiaComEquipe(110, 101, false);

        assertThat(vendedoresAtivos).hasSize(1)
            .extracting(UsuarioEquipeDto::getUsuarioId)
            .contains(109);
    }

    @Test
    public void todosHierarquia_deveRetornarTodosIncluindoInativosDaHierarquiaGerente_quandoEParametroTrue() {
        var vendedores = usuarioSiteService.getVendoresDoSiteIdPorHierarquiaComEquipe(110, 101, true);

        assertThat(vendedores).hasSize(2)
            .extracting(UsuarioEquipeDto::getUsuarioId)
            .contains(109, 115);
    }

    @Test
    public void todosSemHierarquiaAdmin_deveRetornarTodosEIgnorarHierarquia_quandoAdmin() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAdmin());
        var vendedores = usuarioSiteService.getVendoresDoSiteIdPorHierarquiaComEquipe(110, 99, true);
        assertThat(vendedores).hasSize(2)
            .extracting(UsuarioEquipeDto::getUsuarioId)
            .contains(109, 115);
    }

    @Test
    public void usuarioIdAdmin_deveRetornarException_quandoUsuarioIdForAdminELogadoNao() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoSupervisor());
        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> usuarioSiteService.getVendoresDoSiteIdPorHierarquiaComEquipe(110, 99, true));
    }

    private UsuarioAutenticado umUsuarioAutenticadoAdmin() {
        return UsuarioAutenticado.builder()
            .id(99)
            .cargoCodigo(CodigoCargo.ADMINISTRADOR)
            .canais(Collections.singleton(ECanal.ATIVO_PROPRIO))
            .nivelCodigo(CodigoNivel.XBRAIN.name())
            .usuario(Usuario.builder()
                .cargo(null)
                .build())
            .departamentoCodigo(CodigoDepartamento.ADMINISTRADOR)
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticadoSupervisor() {
        return UsuarioAutenticado.builder()
            .id(99)
            .cargoCodigo(CodigoCargo.SUPERVISOR_OPERACAO)
            .canais(Collections.singleton(ECanal.ATIVO_PROPRIO))
            .nivelCodigo(CodigoNivel.OPERACAO.name())
            .usuario(Usuario.builder()
                .cargo(null)
                .build())
            .departamentoCodigo(CodigoDepartamento.COMERCIAL)
            .build();
    }
}
