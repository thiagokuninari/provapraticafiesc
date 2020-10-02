package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.notificacaoapi.service.NotificacaoApiService;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.BACKOFFICE_COORDENADOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.OPERACAO_TELEVENDAS;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Import(HorarioAcessoAtivoLocalService.class)
public class HorarioAcessoAtivoLocalServiceTest {

    @Autowired
    private HorarioAcessoAtivoLocalService service;
    @MockBean
    private DataHoraAtual dataHoraAtual;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private SiteService siteService;
    @MockBean
    private CallService callService;
    @MockBean
    private NotificacaoApiService notificacaoApiService;

    @Before
    public void setup() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAtivoProprio());
        when(siteService.findById(any())).thenReturn(umSite());
    }

    @Test
    public void alterarHorariosAcesso_throwsException_quandoUsuarioDiferenteXbrain() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioEspertinho());

        assertThatCode(() -> service.alterarHorariosAcesso(
                LocalTime.now(),
                LocalTime.now(),
                LocalTime.now(),
                LocalTime.now()))
                .isInstanceOf(PermissaoException.class);
    }

    @Test
    public void alterarHorariosAcesso_doNotThrowsAnyException_quandoUsuarioXbrain() {
        var usuarioXbrain = umUsuarioEspertinho();
        usuarioXbrain.setNivelCodigo(CodigoNivel.XBRAIN.name());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioXbrain);

        assertThatCode(() -> service.alterarHorariosAcesso(
                LocalTime.now(),
                LocalTime.now(),
                LocalTime.now(),
                LocalTime.now()))
                .doesNotThrowAnyException();
    }

    @Test
    public void isDentroHorarioPermitido_deveRetornarFalse_quandoForAntesHorarioNaSemana() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(2020, 9, 23, 9, 0, 0));
        Assertions.assertThat(service.isDentroHorarioPermitido()).isFalse();
    }

    @Test
    public void isDentroHorarioPermitido_deveRetornarFalse_quandoForDepoisHorarioNaSemana() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(2020, 9, 23, 21, 0, 0));
        Assertions.assertThat(service.isDentroHorarioPermitido()).isFalse();
    }

    @Test
    public void isDentroHorarioPermitido_deveRetornarTrue_quandoForDentroHorarioNaSemana() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(2020, 9, 23, 20, 59, 59));
        Assertions.assertThat(service.isDentroHorarioPermitido()).isTrue();
    }

    @Test
    public void isDentroHorarioPermitido_deveRetornarFalse_quandoForAntesHorarioNoSabado() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(2020, 9, 26, 10, 0, 0));
        Assertions.assertThat(service.isDentroHorarioPermitido()).isFalse();
    }

    @Test
    public void isDentroHorarioPermitido_deveRetornarFalse_quandoForDepoisHorarioNoSabado() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(2020, 9, 26, 16, 0, 0));
        Assertions.assertThat(service.isDentroHorarioPermitido()).isFalse();
    }

    @Test
    public void isDentroHorarioPermitido_deveRetornarTrue_quandoForDentroHorarioNoSabado() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(umSabadoComHorarioCorreto());
        Assertions.assertThat(service.isDentroHorarioPermitido()).isTrue();
    }

    @Test
    public void isDentroHorarioPermitido_deveRetornarFalse_quandoForDomingo() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(umDomingo());
        Assertions.assertThat(service.isDentroHorarioPermitido()).isFalse();
    }

    @Test
    public void isDentroHorarioPermitido_deveRetornarTrue_quandoForOutroTipoUsuario() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioEspertinho());
        when(dataHoraAtual.getDataHora(any())).thenReturn(umDomingo());
        Assertions.assertThat(service.isDentroHorarioPermitido()).isTrue();
    }

    @Test
    public void validarSomenteHorarioAcesso_deveRetornarTrue_quandoEstiverDentroHorario() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(LocalDateTime.of(2020, 9, 23, 20, 59, 59));
        Assertions.assertThat(service.isDentroHorarioPermitido(umUsuario())).isTrue();
    }

    @Test
    public void validarSomenteHorarioAcesso_deveRetornarFalse_quandoEstiverForaHorario() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(umDomingo());
        Assertions.assertThat(service.isDentroHorarioPermitido(umUsuario())).isFalse();
    }

    @Test
    public void validarSomenteHorarioAcesso_deveRetornarTrue_quandoEstiverForaHorarioPoremOutroUsuarioCargo() {
        when(dataHoraAtual.getDataHora(any())).thenReturn(umDomingo());
        var usuario = umUsuario();
        usuario.setCargo(Cargo.builder().codigo(CodigoCargo.MSO_CONSULTOR).build());
        Assertions.assertThat(service.isDentroHorarioPermitido(usuario)).isTrue();
    }

    public LocalDateTime umDomingo() {
        return LocalDateTime.of(2020, 9, 27, 12, 0, 0);
    }

    public LocalDateTime umSabadoComHorarioCorreto() {
        return LocalDateTime.of(2020, 9, 26, 15, 50, 0);
    }

    public Usuario umUsuario() {
        return Usuario.builder()
            .cargo(Cargo.builder()
                .codigo(CodigoCargo.OPERACAO_TELEVENDAS)
                .build())
            .site(Site.builder()
                    .timeZone(ETimeZone.BRT)
                    .build())
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .build();
    }

    public UsuarioAutenticado umUsuarioAtivoProprio() {
        return UsuarioAutenticado.builder()
                .usuario(
                        Usuario.builder()
                                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                                .site(umSite())
                                .cargo(Cargo.builder()
                                        .codigo(OPERACAO_TELEVENDAS)
                                        .build())
                                .build())
                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                .cargoCodigo(OPERACAO_TELEVENDAS)
                .build();
    }

    public UsuarioAutenticado umUsuarioEspertinho() {
        return UsuarioAutenticado.builder()
                .nivelCodigo(CodigoNivel.OPERACAO.name())
                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                .usuario(
                        Usuario.builder()
                                .canais(Set.of(ECanal.ATIVO_PROPRIO))
                                .site(umSite())
                                .cargo(Cargo.builder()
                                        .codigo(BACKOFFICE_COORDENADOR)
                                        .build())
                                .build())
                .cargoCodigo(BACKOFFICE_COORDENADOR)
                .siteId(1)
                .build();
    }

    public Site umSite() {
        return Site.builder()
                .id(1)
                .timeZone(ETimeZone.BRT)
                .build();
    }
}
