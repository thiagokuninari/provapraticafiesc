package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.SocioService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.helper.SolicitacaoRamalHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SolicitacaoRamalHelper.criaSolicitacaoRamal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SolicitacaoRamalHelper.umaSolicitacaoRamal;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SolicitacaoRamalServiceAaTest {

    @InjectMocks
    private SolicitacaoRamalServiceAa service;
    @Mock
    private CallService callService;
    @Mock
    private SocioService socioService;
    @Mock
    private EmailService emailService;
    @Mock
    private DataHoraAtual dataHoraAtual;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private SolicitacaoRamalRepository repository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private AgenteAutorizadoService agenteAutorizadoService;
    @Mock
    private SolicitacaoRamalService solicitacaoRamalService;
    @Mock
    private SolicitacaoRamalHistoricoService historicoService;

    @Test
    public void getAllGerencia_deveListarSolicitacoes_seTodosOsParametrosPreenchidos() {
        var filtros = new SolicitacaoRamalFiltros();
        filtros.setAgenteAutorizadoId(1);
        filtros.setSituacao(ESituacaoSolicitacao.PENDENTE);
        filtros.setCanal(ECanal.AGENTE_AUTORIZADO);

        when(repository.findAllGerenciaAa(new PageRequest(), filtros.toPredicate().build()))
            .thenReturn(umaPageSolicitacaoRamal());

        assertThat(service.getAllGerencia(new PageRequest(), filtros))
            .extracting("id", "canal", "dataCadastro", "situacao")
            .containsExactly(
                Tuple.tuple(1, ECanal.AGENTE_AUTORIZADO,
                    LocalDateTime.of(2022, 2, 10, 10, 0, 0),
                    ESituacaoSolicitacao.PENDENTE),
                Tuple.tuple(2, ECanal.AGENTE_AUTORIZADO,
                    LocalDateTime.of(2022, 2, 10, 10, 0, 0),
                    ESituacaoSolicitacao.PENDENTE)
            );
    }

    @Test
    public void save_deveSalvarUmaSolicitacaoRamal_quandoDadosValidos() {
        ReflectionTestUtils.setField(service, "destinatarios", "teste");

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());
        when(autenticacaoService.getUsuarioId())
            .thenReturn(1);
        when(agenteAutorizadoService.getAaById(7129))
            .thenReturn(criaAa());
        when(dataHoraAtual.getDataHora())
            .thenReturn(LocalDateTime.of(2023, 11, 27, 10, 0));
        when(repository.save(any(SolicitacaoRamal.class)))
            .thenReturn(umaSolicitacaoRamal(1));
        when(agenteAutorizadoService.getUsuariosAaAtivoComVendedoresD2D(7129))
            .thenReturn(umaListaUsuarioResponse());

        service.save(criaSolicitacaoRamal(null, 7129));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService).getUsuarioId();
        verify(agenteAutorizadoService).getAaById(7129);
        verify(dataHoraAtual).getDataHora();
        verify(repository).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_quandoUsuarioAutenticadoNaoPossuirPermissao20014() {
        var usuario = umUsuarioAutenticado();
        usuario.setPermissoes(List.of());

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuario);

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() ->
                service.save(criaSolicitacaoRamal(null, 7129)))
            .withMessage("Sem autorização para fazer uma solicitação para este canal.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService, never()).getUsuarioId();
        verify(agenteAutorizadoService, never()).getAaById(7129);
        verify(dataHoraAtual, never()).getDataHora();
        verify(repository, never()).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_quandoSolicitacaoPendenteOuEmAndamento() {
        var solicitacaoRamal = SolicitacaoRamal
            .convertFrom(criaSolicitacaoRamal(null, 7129), 1,
                LocalDateTime.of(2023, 11, 27, 10, 0), criaAa());

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());
        when(repository.findAllByAgenteAutorizadoIdAndSituacaoPendenteOuEmAndamento(7129))
            .thenReturn(List.of(umaSolicitacaoRamal(1)));

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() ->
                service.save(criaSolicitacaoRamal(null, 7129)))
            .withMessage("Não é possível salvar a solicitação de ramal, pois já existe uma pendente ou em andamento.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService, never()).getUsuarioId();
        verify(agenteAutorizadoService, never()).getAaById(7129);
        verify(dataHoraAtual, never()).getDataHora();
        verify(repository, never()).save(solicitacaoRamal);
    }

    @Test
    public void save_deveLancarException_quandoSolicitacaoComAaNulo() {
        var request = criaSolicitacaoRamal(null, 7129);
        request.setAgenteAutorizadoId(null);
        request.setCanal(ECanal.AGENTE_AUTORIZADO);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() ->
                service.save(request))
            .withMessage("agenteAutorizadoId obrigatório para o cargo agente autorizado");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService, never()).getUsuarioId();
        verify(agenteAutorizadoService, never()).getAaById(7129);
        verify(dataHoraAtual, never()).getDataHora();
        verify(repository, never()).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_seCanalForAgenteAutorizadoEAgenteAutorizadoIdNaoForInformado() {
        var solicitacaoRamal = criaSolicitacaoRamal(null, null);
        solicitacaoRamal.setCanal(ECanal.AGENTE_AUTORIZADO);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("agenteAutorizadoId obrigatório para o cargo agente autorizado");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, never()).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_quandoUsuarioAutenticadoNaoTiverPermissaoCTR_20014() {
        var solicitacaoRamal = criaSolicitacaoRamal(null, null);
        solicitacaoRamal.setCanal(ECanal.AGENTE_AUTORIZADO);
        solicitacaoRamal.setAgenteAutorizadoId(1);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().id(1)
                .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_20015.getRole()))).build());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("Sem autorização para fazer uma solicitação para este canal.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void save_deveLancarException_seExcedeuLimiteDeSolicitacoesDeRamal() {
        ReflectionTestUtils.setField(service, "destinatarios", "teste");
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());
        when(agenteAutorizadoService.getUsuariosAaAtivoComVendedoresD2D(7129))
            .thenReturn(umaListaUsuarioResponse());
        when(callService.obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 7129))
            .thenReturn(umListRamalResponse());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(criaSolicitacaoRamal(null, 7129)))
            .withMessage("Não é possível salvar a solicitação de ramal, pois excedeu o limite.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(agenteAutorizadoService).getUsuariosAaAtivoComVendedoresD2D(7129);
        verify(callService).obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 7129);
    }

    @Test
    public void getDadosAdicionais_deveChamarClientPeloAgenteAutorizadoId() {
        when(agenteAutorizadoService.getAaById(1))
            .thenReturn(umAgenteAutorizado());
        when(callService.obterNomeTelefoniaPorId(1))
            .thenReturn(umaTelefonia());
        when(agenteAutorizadoService.getUsuariosAaAtivoComVendedoresD2D(1))
            .thenReturn(List.of());
        when(socioService.findSocioPrincipalByAaId(1))
            .thenReturn(umSocioPrincipal());

        service.getDadosAdicionais(umFiltrosSolicitacao(ECanal.AGENTE_AUTORIZADO, null, 1));

        verify(agenteAutorizadoService).getAaById(1);
        verify(callService).obterNomeTelefoniaPorId(1);
        verify(callService).obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 1);
        verify(socioService).findSocioPrincipalByAaId(1);
        verify(agenteAutorizadoService).getUsuariosAaAtivoComVendedoresD2D(1);
    }

    @Test
    public void update_deveAtualizarSolicitacaoAa_quandoTodosOsDadosPreenchidosCorretamente() {
        when(solicitacaoRamalService.findById(1))
            .thenReturn(umaSolicitacaoRamal(1));
        when(autenticacaoService.getUsuarioId())
            .thenReturn(1);
        when(agenteAutorizadoService.getAaById(7129))
            .thenReturn(umAgenteAutorizadoResponse());
        when(repository.save(umaSolicitacaoRamal(1)))
            .thenReturn(umaSolicitacaoRamal(1));

        var solicitacao = SolicitacaoRamalResponse.convertFrom(umaSolicitacaoRamal(1));

        assertThat(service.update(criaSolicitacaoRamal(1, 7129)))
            .isEqualTo(solicitacao);

        verify(solicitacaoRamalService).findById(1);
        verify(autenticacaoService).getUsuarioId();
        verify(agenteAutorizadoService).getAaById(7129);
        verify(repository).save(umaSolicitacaoRamal(1));
    }

    @Test
    public void verificaPermissaoSobreOAgenteAutorizado_deveVerificarPermissao_quandoAaExistir() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());
        when(autenticacaoService.getUsuarioId())
            .thenReturn(1);
        when(usuarioService.findComplete(1))
            .thenReturn(umUsuario());
        when(agenteAutorizadoService.getAgentesAutorizadosPermitidos(umUsuario()))
            .thenReturn(List.of(1));

        assertThatCode(() -> service.verificaPermissaoSobreOAgenteAutorizado(1))
            .doesNotThrowAnyException();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService).getUsuarioId();
        verify(usuarioService).findComplete(1);
        verify(agenteAutorizadoService).getAgentesAutorizadosPermitidos(umUsuario());
    }

    @Test
    public void getRamaisDisponiveis_deveRetornarRamaisDisponiveisParaUso_quandoSolicitado() {
        when(callService.obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 1))
            .thenReturn(List.of(umRamalResponse(1)));
        when(agenteAutorizadoService.getUsuariosAaAtivoComVendedoresD2D(1))
            .thenReturn(umaListaUsuarioAgenteAutorizadoResponse());

        assertThat(service.getRamaisDisponiveis(1)).isEqualTo(1);

        verify(callService).obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 1);
        verify(agenteAutorizadoService).getUsuariosAaAtivoComVendedoresD2D(1);
    }

    @Test
    public void getRamaisDisponiveis_deveRetornarZero_seUsuariosERamaisTiveremOMesmoValor() {
        when(callService.obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 1))
            .thenReturn(umListRamalResponse());
        when(agenteAutorizadoService.getUsuariosAaAtivoComVendedoresD2D(1))
            .thenReturn(umaListaUsuarioAgenteAutorizadoResponse());

        assertThat(service.getRamaisDisponiveis(1)).isEqualTo(0);

        verify(callService).obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 1);
        verify(agenteAutorizadoService).getUsuariosAaAtivoComVendedoresD2D(1);
    }

    @Test
    public void getRamaisDisponiveis_deveRetornarZero_seRamaisForMaiorQueUsuarios() {
        when(callService.obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 1))
            .thenReturn(List.of(
                umRamalResponse(1),
                umRamalResponse(2),
                umRamalResponse(3)));

        when(agenteAutorizadoService.getUsuariosAaAtivoComVendedoresD2D(1))
            .thenReturn(umaListaUsuarioAgenteAutorizadoResponse());

        assertThat(service.getRamaisDisponiveis(1)).isEqualTo(0);

        verify(callService).obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 1);
        verify(agenteAutorizadoService).getUsuariosAaAtivoComVendedoresD2D(1);
    }
}
