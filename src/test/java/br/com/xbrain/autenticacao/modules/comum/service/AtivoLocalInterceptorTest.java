package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.config.interceptor.AtivoLocalInterceptor;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.OPERACAO_TELEVENDAS;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@Import(HorarioAcessoAtivoLocalService.class)
public class AtivoLocalInterceptorTest {

    @Autowired
    private HorarioAcessoAtivoLocalService horarioAcessoAtivoLocalService;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private SiteService siteService;
    @MockBean
    private CallService callService;
    @MockBean
    private DataHoraAtual dataHoraAtual;

    private AtivoLocalInterceptor interceptor;

    @Before
    public void setup() {
        when(autenticacaoService.getAccessToken()).thenReturn(Optional.of(getTokenFromVendedor()));
        when(siteService.findById(eq(1))).thenReturn(umSite());
        when(callService.consultarStatusUsoRamalByUsuarioAutenticado()).thenReturn(false);
        interceptor = new AtivoLocalInterceptor(siteService, autenticacaoService, callService,
                horarioAcessoAtivoLocalService, dataHoraAtual);
    }

    @Test
    public void deveValidarAcesso_throwsException_quandoForaHorarioPermitido() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));

        assertThatExceptionOfType(UnauthorizedUserException.class)
                .isThrownBy(() -> interceptor.postHandle(new MockHttpServletRequest(),
                        new MockHttpServletResponse(), null, new ModelAndView()))
                .withMessage("Fora do horário permitido.");

        verify(callService, atLeastOnce()).consultarStatusUsoRamalByUsuarioAutenticado();
    }

    @Test
    public void deveValidarAcesso_notThrowsException_quandoForaHorarioPermitidoMasRamalEmUso() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));
        when(callService.consultarStatusUsoRamalByUsuarioAutenticado()).thenReturn(true);

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest(),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(callService, atLeastOnce()).consultarStatusUsoRamalByUsuarioAutenticado();
    }

    @Test
    public void deveValidarAcesso_notThrowsException_quandoDentroHorarioPermitido() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 5)));

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest(),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(callService, never()).consultarStatusUsoRamalByUsuarioAutenticado();
    }

    private Site umSite() {
        return Site.builder()
                .timeZone(ETimeZone.BRT)
                .id(1)
                .build();
    }

    private OAuth2AccessToken getTokenFromVendedor() {
        var token = new DefaultOAuth2AccessToken("12345");
        token.setAdditionalInformation(
                Map.of("cargo", OPERACAO_TELEVENDAS,
                        "siteId", 1,
                        "canais", Set.of(ECanal.ATIVO_PROPRIO.name()))
        );
        return token;
    }

}
