package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.config.interceptor.AtivoLocalInterceptor;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAtuacaoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;
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
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.horarioacesso.helper.HorarioHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.MSO_CONSULTOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.OPERACAO_TELEVENDAS;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Import({HorarioAcessoService.class})
public class AtivoLocalInterceptorTest {

    @Autowired
    private HorarioAcessoService horarioAcessoService;
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
    @MockBean
    private HorarioAcessoRepository horarioAcessoRepository;
    @MockBean
    private HorarioAtuacaoRepository horarioAtuacaoRepository;
    @MockBean
    private HorarioHistoricoRepository horarioHistoricoRepository;

    private AtivoLocalInterceptor interceptor;

    @Before
    public void setup() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(getUsuarioAutenticado());
        when(autenticacaoService.getAccessToken()).thenReturn(getTokenFromVendedor());
        when(callService.consultarStatusUsoRamalByUsuarioAutenticado()).thenReturn(false);
        when(notificacaoApiService.consultarStatusTabulacaoByUsuario(anyInt())).thenReturn(false);
        when(horarioAcessoRepository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(siteService.findById(anyInt())).thenReturn(umSite());
        when(siteService.getSitesPorPermissao(any())).thenReturn(List.of(SelectResponse.of(1, "Curitiba")));
        interceptor = new AtivoLocalInterceptor(horarioAcessoService);
    }

    @Test
    public void deveValidarAcesso_throwsException_quandoForaHorarioPermitido() {
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));
        when(horarioAtuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(umaListaHorariosAtuacao(8, 18));

        assertThatExceptionOfType(UnauthorizedUserException.class)
                .isThrownBy(() -> interceptor.postHandle(new MockHttpServletRequest("GET", "/api/usuarios"),
                        new MockHttpServletResponse(), null, new ModelAndView()))
                .withMessage("Usuário fora do horário permitido.");

        verify(callService, atLeastOnce()).consultarStatusUsoRamalByUsuarioAutenticado();
        verify(autenticacaoService, atLeastOnce()).logout(anyInt());
        verify(callService, atLeastOnce()).liberarRamalUsuarioAutenticado();
    }

    @Test
    public void naoDeveValidarAcesso_notThrowsException_quandoRotaForParaLogin() {
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("POST", "/oauth/token"),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(notificacaoApiService, never()).consultarStatusTabulacaoByUsuario(any());
        verify(autenticacaoService, never()).logout(anyInt());
    }

    @Test
    public void naoDeveValidarAcesso_notThrowsException_quandoRotaForParaLiberarRamal() {
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("PUT", "api/usuarios/remover-ramal-configuracao"),
            new MockHttpServletResponse(), null, new ModelAndView()))
            .doesNotThrowAnyException();

        verify(notificacaoApiService, never()).consultarStatusTabulacaoByUsuario(any());
        verify(autenticacaoService, never()).logout(anyInt());
    }

    @Test
    public void naoDeveValidarAcesso_notThrowsException_quandoUsuarioDiferenteVendedorAtivo() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(getUsuarioAutenticadoMso());
        when(autenticacaoService.getAccessToken()).thenReturn(getTokenFromMso());
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("GET", "/api/usuarios"),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(notificacaoApiService, never()).consultarStatusTabulacaoByUsuario(any());
        verify(autenticacaoService, never()).logout(anyInt());
    }

    @Test
    public void deveValidarAcesso_notThrowsException_quandoForaHorarioPermitidoMasDentroTabulacao() {
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));
        when(horarioAtuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(umaListaHorariosAtuacao(9, 18));
        when(notificacaoApiService.consultarStatusTabulacaoByUsuario(anyInt())).thenReturn(true);

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("GET", "/api/usuarios"),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(notificacaoApiService, atLeastOnce()).consultarStatusTabulacaoByUsuario(any());
    }

    @Test
    public void deveValidarAcesso_notThrowsException_quandoForaHorarioPermitidoMasRamalEmUso() {
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 5)));
        when(horarioAtuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(umaListaHorariosAtuacao(9, 22));

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("GET", "/api/usuarios"),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(horarioAcessoRepository, atLeastOnce()).findBySiteId(eq(100));
        verify(horarioAtuacaoRepository, atLeastOnce()).findByHorarioAcessoId(eq(1));
    }

    @Test
    public void deveValidarAcesso_notThrowsException_quandoDentroHorarioPermitido() {
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 5)));

        assertThatCode(() -> interceptor.postHandle(new MockHttpServletRequest("GET", "/api/usuarios"),
                new MockHttpServletResponse(), null, new ModelAndView()))
                .doesNotThrowAnyException();

        verify(callService, never()).consultarStatusUsoRamalByUsuarioAutenticado();
        verify(notificacaoApiService, never()).consultarStatusTabulacaoByUsuario(any());
        verify(autenticacaoService, never()).logout(anyInt());
    }

    private Site umSite() {
        return Site.builder()
                .id(100)
                .build();
    }

    private List<HorarioAtuacao> umaListaHorariosAtuacao(Integer horaInicio, Integer horaFim) {
        return List.of(HorarioAtuacao.builder()
            .id(1)
            .diaSemana(EDiaSemana.valueOf(LocalDateTime.now()))
            .horarioAcesso(umHorarioAcesso())
            .horarioInicio(LocalTime.of(horaInicio, 0))
            .horarioFim(LocalTime.of(horaFim, 0))
            .build());
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
                                .id(101)
                                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                                .cargo(Cargo.builder()
                                        .codigo(OPERACAO_TELEVENDAS)
                                        .build())
                                .build())
                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                .cargoCodigo(OPERACAO_TELEVENDAS)
                .build();
    }

    private Optional<OAuth2AccessToken> getTokenFromVendedor() {
        var token = new DefaultOAuth2AccessToken("12345");
        token.setAdditionalInformation(
            Map.of("cargo", OPERACAO_TELEVENDAS,
                "canais", Set.of(ECanal.ATIVO_PROPRIO.name()))
        );
        return Optional.of(token);
    }

    private Optional<OAuth2AccessToken> getTokenFromMso() {
        var token = new DefaultOAuth2AccessToken("123455");
        token.setAdditionalInformation(
            Map.of("cargo", MSO_CONSULTOR,
                "canais", Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO))
        );
        return Optional.of(token);
    }

}
