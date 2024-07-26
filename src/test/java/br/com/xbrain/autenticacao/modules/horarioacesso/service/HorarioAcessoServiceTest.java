package br.com.xbrain.autenticacao.modules.horarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAtuacaoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.notificacaoapi.service.NotificacaoApiService;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.horarioacesso.helper.HorarioHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioOperadorTelevendas;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class HorarioAcessoServiceTest {

    @InjectMocks
    private HorarioAcessoService service;
    @Mock
    private CallService callService;
    @Mock
    private SiteService siteService;
    @Mock
    private Environment environment;
    @Mock
    private DataHoraAtual dataHoraAtual;
    @Mock
    private HorarioAcessoRepository repository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private HorarioAtuacaoRepository atuacaoRepository;
    @Mock
    private NotificacaoApiService notificacaoApiService;
    @Mock
    private HorarioHistoricoRepository historicoRepository;

    @Test
    public void getHorariosAcesso_deveRetornarListaDeHorario_quandoDadosValidos() {
        when(repository.findAll(new HorarioAcessoFiltros().toPredicate().build(), new PageRequest()))
            .thenReturn(new PageImpl<>(List.of(umHorarioAcesso())));

        assertThat(service.getHorariosAcesso(new PageRequest(), new HorarioAcessoFiltros()))
            .extracting("horarioAcessoId", "siteId", "siteNome", "usuarioAlteracaoNome")
            .containsExactly(tuple(1, 100, "SITE TESTE", "USUARIO TESTE"));

        verify(repository).findAll(new HorarioAcessoFiltros().toPredicate().build(), new PageRequest());
        verify(atuacaoRepository).findByHorarioAcessoId(1);
    }

    @Test
    public void getHistoricos_deveRetornarListaDeHorarioAcessoResponse_quandoBuscarHistoricos() {
        when(historicoRepository.findByHorarioAcessoId(1, new PageRequest()))
            .thenReturn(new PageImpl<>(List.of(umHorarioHistorico())));
        when(atuacaoRepository.findByHorarioHistoricoId(1)).thenReturn(umaListaHorariosAtuacao());

        assertThat(service.getHistoricos(new PageRequest(), 1))
            .isEqualTo(new PageImpl<>(List.of(umHorarioHistoricoResponse())));

        verify(historicoRepository).findByHorarioAcessoId(1, new PageRequest());
        verify(atuacaoRepository).findByHorarioHistoricoId(1);
    }

    @Test
    public void getHorarioAcesso_deveRetornarHorarioAcessoResponse_aoBuscarHorarioAcesso() {
        var response = umHorarioAcessoResponse();
        response.setHorariosAtuacao(List.of());

        when(repository.findById(1)).thenReturn(Optional.of(umHorarioAcesso()));

        assertThat(service.getHorarioAcesso(1)).isEqualTo(response);

        verify(repository).findById(1);
        verify(atuacaoRepository).findByHorarioAcessoId(1);
    }

    @Test
    public void getHorarioAcesso_deveRetornarException_quandoNaoEncontrarHorarioAcesso() {
        when(repository.findById(anyInt())).thenThrow(new ValidacaoException("Horário de acesso não encontrado."));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> repository.findById(100))
            .withMessage("Horário de acesso não encontrado.");

        verify(repository).findById(100);
    }

    @Test
    public void save_deveRetornarHorarioAcesso_quandoSalvarNovoHorario() {
        var request = umHorarioAcessoRequest();
        request.setId(null);

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umAdmin());
        when(repository.save(any(HorarioAcesso.class))).thenReturn(umHorarioAcesso());
        when(historicoRepository.save(any(HorarioHistorico.class))).thenReturn(umHorarioHistorico());

        assertThat(service.save(request)).isEqualTo(umHorarioAcesso());

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).save(any(HorarioAcesso.class));
        verify(repository, never()).findById(any());
        verify(historicoRepository).save(any(HorarioHistorico.class));
    }

    @Test
    public void save_deveRetornarHorarioAcesso_quandoEditarHorario() {
        var usuario = Usuario.builder().id(101).nome("USUARIO TESTE EDIÇÃO").build();
        var horario = umHorarioAcesso();
        horario.setUsuarioAlteracaoId(usuario.getId());
        horario.setUsuarioAlteracaoNome(usuario.getNome());

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().usuario(usuario).build());
        when(repository.findById(1)).thenReturn(Optional.of(horario));
        when(repository.save(any(HorarioAcesso.class))).thenReturn(horario);
        when(historicoRepository.save(any(HorarioHistorico.class))).thenReturn(umHorarioHistorico());

        assertThat(service.save(umHorarioAcessoRequest())).isEqualTo(horario);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).save(any(HorarioAcesso.class));
        verify(repository).findById(1);
        verify(historicoRepository).save(any(HorarioHistorico.class));
    }

    @Test
    public void save_deveRetornarException_quandoHorarioAcessoNaoEncontrado() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umHorarioAcessoRequest()))
            .withMessage("Horário de acesso não encontrado.");

        verify(repository).findById(1);
    }

    @Test
    public void save_deveRetornarException_quandoSiteJaPossuiHorarioAcesso() {
        when(repository.existsBySiteId(100)).thenReturn(true);
        var request = umHorarioAcessoRequest();
        request.setId(null);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(request))
            .withMessage("Site já possui horário de acesso cadastrado.");

        verify(repository).existsBySiteId(100);
    }

    @Test
    public void criaHorariosAcesso_deveCriarHorariosAcesso_quandoChamado() {
        var horarioAtuacao = umaListaHorariosAtuacao();
        horarioAtuacao.get(0).setHorarioAcesso(umHorarioAcesso());
        horarioAtuacao.get(1).setHorarioAcesso(umHorarioAcesso());
        horarioAtuacao.get(2).setHorarioAcesso(umHorarioAcesso());
        horarioAtuacao.get(0).setHorarioHistorico(umHorarioHistorico());
        horarioAtuacao.get(1).setHorarioHistorico(umHorarioHistorico());
        horarioAtuacao.get(2).setHorarioHistorico(umHorarioHistorico());

        service.criaHorariosAcesso(umaListaHorariosAtuacao(), umHorarioAcesso(), umHorarioHistorico());

        verify(atuacaoRepository).save(horarioAtuacao.get(0));
        verify(atuacaoRepository).save(horarioAtuacao.get(1));
        verify(atuacaoRepository).save(horarioAtuacao.get(2));
    }

    @Test
    public void criaHorariosAcesso_deveRetornarException_quandoAlgumErroAoSalvar() {
        doThrow(new RuntimeException("Error"))
            .when(atuacaoRepository)
            .save(any(HorarioAtuacao.class));

        assertThatThrownBy(() -> service.criaHorariosAcesso(umaListaHorariosAtuacao(), umHorarioAcesso(), umHorarioHistorico()))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Error");

        verify(atuacaoRepository).save(any(HorarioAtuacao.class));
    }

    @Test
    public void getStatus_deveRetornarTrue_quandoHorarioAtualEstiverDentroDoHorarioPermitido() {
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.SEGUNDA)
            .horarioInicio(LocalTime.of(9, 0))
            .horarioFim(LocalTime.of(18, 0))
            .build();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umOperadorTelevendas());
        when(repository.findBySiteId(100)).thenReturn(Optional.of(umHorarioAcesso()));
        when(siteService.getSitesPorPermissao(umUsuarioOperadorTelevendas()))
            .thenReturn(List.of(SelectResponse.of(100, "SITE TEST")));
        when(siteService.findById(100)).thenReturn(umSite());
        when(dataHoraAtual.getDataHora())
            .thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        when(atuacaoRepository.findByHorarioAcessoId(1)).thenReturn(List.of(horarioAtuacao));

        assertThat(service.getStatus(ECanal.ATIVO_PROPRIO)).isTrue();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).findBySiteId(100);
        verify(siteService).getSitesPorPermissao(umUsuarioOperadorTelevendas());
        verify(siteService).findById(100);
        verify(dataHoraAtual).getDataHora();
        verify(atuacaoRepository).findByHorarioAcessoId(1);
    }

    @Test
    public void getStatus_deveRetornarFalse_quandoHorarioAtualNaoEstiverDentroDoHorarioPermitido() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umOperadorTelevendas());
        when(repository.findBySiteId(100)).thenReturn(Optional.of(umHorarioAcesso()));
        when(siteService.getSitesPorPermissao(umUsuarioOperadorTelevendas()))
            .thenReturn(List.of(SelectResponse.of(100, "SITE TEST")));
        when(siteService.findById(100)).thenReturn(umSite());
        when(dataHoraAtual.getDataHora())
            .thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));

        assertThat(service.getStatus(ECanal.ATIVO_PROPRIO)).isFalse();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(siteService).getSitesPorPermissao(umOperadorTelevendas().getUsuario());
        verify(siteService).findById(100);
        verify(repository).findBySiteId(100);
        verify(atuacaoRepository).findByHorarioAcessoId(1);
    }

    @Test
    public void getStatus_deveRetornarFalse_quandoHorarioAtualNaoSeEncaixarEmNenhumDiaPermitido() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umOperadorTelevendas());
        when(repository.findBySiteId(100)).thenReturn(Optional.of(umHorarioAcesso()));
        when(siteService.getSitesPorPermissao(umUsuarioOperadorTelevendas()))
            .thenReturn(List.of(SelectResponse.of(100, "SITE TEST")));
        when(siteService.findById(100)).thenReturn(umSite());
        when(dataHoraAtual.getDataHora())
            .thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));

        assertThat(service.getStatus(ECanal.ATIVO_PROPRIO)).isFalse();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(siteService).getSitesPorPermissao(umOperadorTelevendas().getUsuario());
        verify(siteService).findById(100);
        verify(repository).findBySiteId(100);
        verify(atuacaoRepository).findByHorarioAcessoId(1);
    }

    @Test
    public void getStatus_deveLancarException_quandoCanalInformadoNaoForAtivoProprio() {
        assertThatCode(() -> service.getStatus(ECanal.D2D_PROPRIO))
            .hasMessage("O canal informado não é válido.")
            .isInstanceOf(ValidacaoException.class);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, never()).findBySiteId(any());
        verify(atuacaoRepository, never()).findByHorarioAcessoId(any());
    }

    @Test
    public void getStatus_deveLancarException_quandoUsuarioNaoPossuirCanalAtivoProprio() {
        var usuarioAutenticado = umOperadorTelevendas();
        usuarioAutenticado.setCanais(Set.of(ECanal.D2D_PROPRIO));
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticado);

        assertThatCode(() -> service.getStatus(ECanal.ATIVO_PROPRIO))
            .hasMessage("Usuário não possui o canal válido.")
            .isInstanceOf(ValidacaoException.class);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, never()).findBySiteId(any());
        verify(atuacaoRepository, never()).findByHorarioAcessoId(any());
    }

    @Test
    public void getStatus_comParametroSiteId_deveRetornarTrue_quandoHorarioAtualEstiverDentroDoHorarioPermitido() {
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.SEGUNDA)
            .horarioInicio(LocalTime.of(9, 0))
            .horarioFim(LocalTime.of(18, 0))
            .build();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umOperadorTelevendas());
        when(repository.findBySiteId(100)).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora())
            .thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        when(atuacaoRepository.findByHorarioAcessoId(1)).thenReturn(List.of(horarioAtuacao));

        assertThat(service.getStatus(ECanal.ATIVO_PROPRIO, 100)).isTrue();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).findBySiteId(100);
        verify(siteService, never()).getSitesPorPermissao(any());
        verify(siteService, never()).findById(any());
        verify(dataHoraAtual).getDataHora();
        verify(atuacaoRepository).findByHorarioAcessoId(1);
    }

    @Test
    public void getStatus_comParametroSiteId_deveRetornarFalse_quandoHorarioAtualNaoEstiverDentroDoHorarioPermitido() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umOperadorTelevendas());
        when(repository.findBySiteId(100)).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora())
            .thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));

        assertThat(service.getStatus(ECanal.ATIVO_PROPRIO, 100)).isFalse();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).findBySiteId(100);
        verify(siteService, never()).getSitesPorPermissao(any());
        verify(siteService, never()).findById(any());
        verify(dataHoraAtual).getDataHora();
        verify(atuacaoRepository).findByHorarioAcessoId(1);
    }

    @Test
    public void getStatus_comParametroSiteId_deveRetornarFalse_quandoHorarioAtualNaoSeEncaixarEmNenhumDiaPermitido() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umOperadorTelevendas());
        when(repository.findBySiteId(100)).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora())
            .thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));

        assertThat(service.getStatus(ECanal.ATIVO_PROPRIO, 100)).isFalse();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).findBySiteId(100);
        verify(siteService, never()).getSitesPorPermissao(any());
        verify(siteService, never()).findById(any());
        verify(dataHoraAtual).getDataHora();
        verify(atuacaoRepository).findByHorarioAcessoId(1);
    }

    @Test
    public void getStatus_comParametroSiteId_deveLancarException_quandoCanalInformadoNaoForAtivoProprio() {
        assertThatCode(() -> service.getStatus(ECanal.D2D_PROPRIO, 100))
            .hasMessage("O canal informado não é válido.")
            .isInstanceOf(ValidacaoException.class);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, never()).findBySiteId(any());
        verify(atuacaoRepository, never()).findByHorarioAcessoId(any());
    }

    @Test
    public void getStatus_comParametroSiteId_deveLancarException_quandoUsuarioNaoPossuirCanalAtivoProprio() {
        var usuarioAutenticado = umOperadorTelevendas();
        usuarioAutenticado.setCanais(Set.of(ECanal.D2D_PROPRIO));
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticado);

        assertThatCode(() -> service.getStatus(ECanal.ATIVO_PROPRIO, 100))
            .hasMessage("Usuário não possui o canal válido.")
            .isInstanceOf(ValidacaoException.class);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, never()).findBySiteId(any());
        verify(atuacaoRepository, never()).findByHorarioAcessoId(any());
    }

    @Test
    public void isDentroHorarioPermitido_naoDeveLancarException_quandoOperadorTelevendasLogarNoHorario() {
        assertThatCode(() -> service.isDentroHorarioPermitido()).doesNotThrowAnyException();

        verify(siteService, never()).getSitesPorPermissao(any());
        verify(repository, never()).findBySiteId(any());
        verify(atuacaoRepository, never()).findByHorarioAcessoId(any());
    }

    @Test
    public void isDentroHorarioPermitido_deveLancarException_quandoOperadorTelevendasLogarForaDoHorario() {
        var usuario = umAdmin();
        usuario.setCargoCodigo(CodigoCargo.OPERACAO_TELEVENDAS);
        usuario.setCanais(Set.of(ECanal.ATIVO_PROPRIO));

        when(environment.acceptsProfiles("test")).thenReturn(true);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(
            LocalDate.of(2022, 2, 16), LocalTime.of(8, 0)));
        when(siteService.getSitesPorPermissao(any(Usuario.class)))
            .thenReturn(List.of(SelectResponse.of(101, "OPERADOR TELEVENDAS")));

        assertThatExceptionOfType(UnauthorizedUserException.class)
            .isThrownBy(() -> service.isDentroHorarioPermitido())
            .withMessage("Usuário fora do horário permitido.");

        verify(environment).acceptsProfiles("test");
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(dataHoraAtual).getDataHora();
        verify(siteService).getSitesPorPermissao(umAdmin().getUsuario());
        verify(repository, never()).findBySiteId(any());
        verify(atuacaoRepository, never()).findByHorarioAcessoId(any());
        verify(notificacaoApiService, never()).consultarStatusTabulacaoByUsuario(any());
        verify(callService, never()).consultarStatusUsoRamalByUsuarioAutenticado();
        verify(callService, never()).liberarRamalUsuarioAutenticado();
        verify(autenticacaoService, never()).getUsuarioId();
        verify(autenticacaoService, never()).logout(anyInt());
    }

    @Test
    public void isDentroHorarioPermitido_deveLancarException_quandoNaoHouverHorarioParaDataAtual() {
        var usuario = umAdmin();
        usuario.setCargoCodigo(CodigoCargo.OPERACAO_TELEVENDAS);
        usuario.setCanais(Set.of(ECanal.ATIVO_PROPRIO));

        when(environment.acceptsProfiles("test")).thenReturn(true);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(
            LocalDate.of(2022, 2, 19), LocalTime.of(8, 0)));
        when(siteService.getSitesPorPermissao(any(Usuario.class)))
            .thenReturn(List.of(SelectResponse.of(101, "OPERADOR TELEVENDAS")));

        assertThatExceptionOfType(UnauthorizedUserException.class)
            .isThrownBy(() -> service.isDentroHorarioPermitido())
            .withMessage("Usuário fora do horário permitido.");

        verify(environment).acceptsProfiles("test");
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(dataHoraAtual).getDataHora();
        verify(siteService).getSitesPorPermissao(any());
        verify(repository, never()).findBySiteId(any());
        verify(atuacaoRepository, never()).findByHorarioAcessoId(any());
    }

    @Test
    public void isDentroHorarioPermitido_comUsuario_naoDeveLancarException_quandoUsuarioInformadoEstiverDentroDoHorario() {
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.SEGUNDA)
            .horarioInicio(LocalTime.of(9, 0))
            .horarioFim(LocalTime.of(18, 0))
            .build();

        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(
            LocalDate.of(2022, 2, 14), LocalTime.of(10, 0)));
        when(siteService.getSitesPorPermissao(umOperadorTelevendas().getUsuario()))
            .thenReturn(List.of(SelectResponse.of(101, "OPERADOR TELEVENDAS")));
        when(siteService.findById(101)).thenReturn(umSite());
        when(repository.findBySiteId(100)).thenReturn(Optional.of(umHorarioAcesso()));
        when(atuacaoRepository.findByHorarioAcessoId(1)).thenReturn(List.of(horarioAtuacao));

        assertThatCode(() -> service.isDentroHorarioPermitido(umOperadorTelevendas().getUsuario()))
            .doesNotThrowAnyException();

        verify(dataHoraAtual).getDataHora();
        verify(siteService).getSitesPorPermissao(umOperadorTelevendas().getUsuario());
        verify(siteService).findById(101);
        verify(repository).findBySiteId(100);
        verify(atuacaoRepository).findByHorarioAcessoId(1);
    }

    @Test
    public void isDentroHorarioPermitido_comUsuario_deveLancarException_quandoUsuarioInformadoEstiverForaDoHorario() {
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(
            LocalDate.of(2022, 2, 16), LocalTime.of(8, 0)));
        when(siteService.getSitesPorPermissao(any(Usuario.class)))
            .thenReturn(List.of(SelectResponse.of(101, "OPERADOR TELEVENDAS")));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.isDentroHorarioPermitido(umOperadorTelevendas().getUsuario()))
            .withMessage("Usuário fora do horário permitido.");

        verify(dataHoraAtual).getDataHora();
        verify(siteService).getSitesPorPermissao(umOperadorTelevendas().getUsuario());
        verify(repository, never()).findBySiteId(any());
        verify(atuacaoRepository, never()).findByHorarioAcessoId(any());
    }

    @Test
    public void isDentroHorarioPermitido_comUsuario_deveLancarException_quandoNaoHouverHorarioParaDataAtual() {
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(
            LocalDate.of(2022, 2, 19), LocalTime.of(8, 0)));
        when(siteService.getSitesPorPermissao(umOperadorTelevendas().getUsuario()))
            .thenReturn(List.of(SelectResponse.of(101, "OPERADOR TELEVENDAS")));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.isDentroHorarioPermitido(umOperadorTelevendas().getUsuario()))
            .withMessage("Usuário fora do horário permitido.");

        verify(dataHoraAtual).getDataHora();
        verify(siteService).getSitesPorPermissao(umOperadorTelevendas().getUsuario());
        verify(repository, never()).findBySiteId(any());
        verify(atuacaoRepository, never()).findByHorarioAcessoId(any());
    }

    @Test
    public void isDentroHorarioPermitido_comUsuario_deveRetornarTrue_quandoUsuarioNaoForOperadorTelevendas() {
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(
            LocalDate.of(2022, 2, 16), LocalTime.of(10, 0)));

        assertThatCode(() -> service.isDentroHorarioPermitido(umAdmin().getUsuario()))
            .doesNotThrowAnyException();

        verify(dataHoraAtual).getDataHora();
    }
}
