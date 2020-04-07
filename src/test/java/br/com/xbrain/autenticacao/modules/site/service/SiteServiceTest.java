package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.site.dto.SiteFiltros;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_2046;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelOperacaoGerente;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import({SiteService.class})
public class SiteServiceTest {

    @Autowired
    private SiteService siteService;
    @MockBean
    private SiteRepository repository;
    @MockBean
    private AutenticacaoService autenticacaoService;

    @Before
    public void setup() {
        var siteAtivo = Site.builder().id(1).situacao(ESituacao.A).build();
        var siteInativo = Site.builder().id(2).situacao(ESituacao.I).build();

        when(repository.findById(eq(1))).thenReturn(Optional.of(siteAtivo));
        when(repository.findById(eq(2))).thenReturn(Optional.of(siteInativo));
    }

    @Test
    public void validarPermissao_throwException_quandoUsuarioSemPermissaoParaListagem() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelBackoffice());
        Assertions.assertThatExceptionOfType(PermissaoException.class)
                .isThrownBy(() -> siteService.getAll(new PageRequest(), new SiteFiltros()));
    }

    @Test
    public void validarPermissao_throwException_quandoUsuarioSemPermissaoParaSalvar() {
        var user = umUsuarioAutenticadoNivelBackoffice();
        user.setPermissoes(List.of(new SimpleGrantedAuthority(AUT_2046.getRole())));
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(user);

        Assertions.assertThatExceptionOfType(PermissaoException.class)
                .isThrownBy(() -> siteService.save(new SiteRequest()));
    }

    @Test
    public void validarCanal_throwException_quandoUsuarioSemCanalAtivo() {
        var user = umUsuarioAutenticadoNivelBackoffice();
        user.setCanais(Set.of());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(user);

        Assertions.assertThatExceptionOfType(PermissaoException.class)
                .isThrownBy(() -> siteService.save(new SiteRequest()));
    }

    @Test
    public void naoDeveAtivar_void_quandoSiteJaEstiverAtivo() {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(umUsuarioAutenticadoNivelOperacaoGerente());
        siteService.ativar(1);

        verify(repository, never()).save(any(Site.class));
    }

    @Test
    public void naoDeveInativar_void_quandoSiteJaEstiverInativo() {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(umUsuarioAutenticadoNivelOperacaoGerente());
        siteService.inativar(2);

        verify(repository, never()).save(any(Site.class));
    }
}
