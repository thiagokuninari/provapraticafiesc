package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.config.interceptor.AtivoLocalInterceptor;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.notificacaoapi.service.NotificacaoApiService;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.MSO_CONSULTOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.OPERACAO_TELEVENDAS;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Import({HorarioAcessoAtivoLocalService.class})
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
    @MockBean
    private NotificacaoApiService notificacaoApiService;

    private AtivoLocalInterceptor interceptor;

    @Before
    public void setup() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(getUsuarioAutenticado());
        when(callService.consultarStatusUsoRamalByUsuarioAutenticado()).thenReturn(false);
        when(notificacaoApiService.consultarStatusTabulacaoByUsuario(anyInt())).thenReturn(false);
        when(siteService.findById(anyInt())).thenReturn(umSite());
        when(siteService.getSitesPorPermissao(any())).thenReturn(List.of(SelectResponse.of(1, "Curitiba")));
        interceptor = new AtivoLocalInterceptor(horarioAcessoAtivoLocalService);
    }

    @Test
    public void deveValidarAcesso_throwsException_quandoForaHorarioPermitido() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));

        assertThatExceptionOfType(UnauthorizedUserException.class)
                .isThrownBy(() -> interceptor.postHandle(new MockHttpServletRequest("GET", "/api/usuarios"),
                        new MockHttpServletResponse(), null, new ModelAndView()))
                .withMessage("Fora do horário permitido.");

        verify(callService, atLeastOnce()).consultarStatusUsoRamalByUsuarioAutenticado();
        verify(autenticacaoService, atLeastOnce()).logout(anyInt());
    }

    @Test
    public void naoDeveValidarAcesso_notThrowsException_quandoRotaForParaLogin() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("POST", "/oauth/token"),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(notificacaoApiService, never()).consultarStatusTabulacaoByUsuario(any());
        verify(autenticacaoService, never()).logout(anyInt());
    }

    @Test
    public void naoDeveValidarAcesso_notThrowsException_quandoUsuarioDiferenteVendedorAtivo() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(getUsuarioAutenticadoMso());
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("GET", "/api/usuarios"),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(notificacaoApiService, never()).consultarStatusTabulacaoByUsuario(any());
        verify(autenticacaoService, never()).logout(anyInt());
    }

    @Test
    public void deveValidarAcesso_notThrowsException_quandoForaHorarioPermitidoMasDentroTabulacao() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));
        when(notificacaoApiService.consultarStatusTabulacaoByUsuario(anyInt())).thenReturn(true);

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("GET", "/api/usuarios"),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(notificacaoApiService, atLeastOnce()).consultarStatusTabulacaoByUsuario(any());
    }

    @Test
    public void deveValidarAcesso_notThrowsException_quandoForaHorarioPermitidoMasRamalEmUso() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));
        when(callService.consultarStatusUsoRamalByUsuarioAutenticado()).thenReturn(true);

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("GET", "/api/usuarios"),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(callService, atLeastOnce()).consultarStatusUsoRamalByUsuarioAutenticado();
    }

    @Test
    public void deveValidarAcesso_notThrowsException_quandoDentroHorarioPermitido() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 5)));

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("GET", "/api/usuarios"),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(callService, never()).consultarStatusUsoRamalByUsuarioAutenticado();
        verify(notificacaoApiService, never()).consultarStatusTabulacaoByUsuario(any());
        verify(autenticacaoService, never()).logout(anyInt());
    }

    private Site umSite() {
        return Site.builder()
                .timeZone(ETimeZone.BRT)
                .id(1)
                .build();
    }

    private UsuarioAutenticado getUsuarioAutenticadoMso() {
        return UsuarioAutenticado.builder()
                .usuario(
                        Usuario.builder()
                                .canais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                                .cargo(Cargo.builder()
                                        .codigo(MSO_CONSULTOR)
                                        .build())
                                .build())
                .canais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                .cargoCodigo(MSO_CONSULTOR)
                .build();
    }

    private UsuarioAutenticado getUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
                .usuario(
                        Usuario.builder()
                                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                                .cargo(Cargo.builder()
                                        .codigo(OPERACAO_TELEVENDAS)
                                        .build())
                                .build())
                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                .cargoCodigo(OPERACAO_TELEVENDAS)
                .build();
    }

}
