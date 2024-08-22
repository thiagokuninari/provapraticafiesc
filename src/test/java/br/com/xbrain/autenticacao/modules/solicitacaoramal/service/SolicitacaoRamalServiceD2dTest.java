package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService;
import feign.RetryableException;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.helper.SolicitacaoRamalHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class SolicitacaoRamalServiceD2dTest {

    @InjectMocks
    private SolicitacaoRamalServiceD2d service;
    @Mock
    private CallService callService;
    @Mock
    private EmailService emailService;
    @Mock
    private DataHoraAtual dataHoraAtual;
    @Mock
    private SubCanalService subCanalService;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private SolicitacaoRamalService solicitacaoRamalService;
    @Mock
    private SolicitacaoRamalHistoricoService historicoService;
    @Mock
    private SolicitacaoRamalRepository repository;

    @Test
    public void getAllGerencia_deveListarSolicitacoes_quandoDadosValidoss() {
        when(repository.findAllGerenciaD2d(new PageRequest(), new SolicitacaoRamalFiltros().toPredicate().build()))
            .thenReturn(umaPageSolicitacaoRamal());

        assertThat(service.getAllGerencia(new PageRequest(), new SolicitacaoRamalFiltros()))
            .extracting("id", "canal", "dataCadastro",
                "situacao").containsExactly(

                Tuple.tuple(1, ECanal.AGENTE_AUTORIZADO,
                    LocalDateTime.of(2022, 2, 10, 10, 0, 0),
                    ESituacaoSolicitacao.PENDENTE),

                Tuple.tuple(2, ECanal.AGENTE_AUTORIZADO,
                    LocalDateTime.of(2022, 2, 10, 10, 0, 0),
                    ESituacaoSolicitacao.PENDENTE)
            );

        verify(repository).findAllGerenciaD2d(new PageRequest(), new SolicitacaoRamalFiltros().toPredicate().build());
    }

    @Test
    public void save_deveSalvarUmaSolicitacaoRamal_quandoDadosValidos() {
        ReflectionTestUtils.setField(service, "destinatarios", "teste");
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoOperacao());
        when(autenticacaoService.getUsuarioId()).thenReturn(1);
        when(dataHoraAtual.getDataHora())
            .thenReturn(LocalDateTime.of(2023, 11, 13, 10, 0, 0));
        when(repository.save(umaSolicitacaoRamalCanalD2d())).thenReturn(umaSolicitacaoRamalCanalD2d(1));

        service.save(criaSolicitacaoRamal(null));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService).getUsuarioId();
        verify(dataHoraAtual).getDataHora();
        verify(repository).save(umaSolicitacaoRamalCanalD2d());
    }

    @Test
    public void save_deveSalvarUmaSolicitacaoRamalComDestinatariosSemAVirgula_quandoDadosValidos() {
        ReflectionTestUtils.setField(service, "destinatarios", ",teste");
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoOperacao());
        when(autenticacaoService.getUsuarioId()).thenReturn(1);
        when(dataHoraAtual.getDataHora())
            .thenReturn(LocalDateTime.of(2023, 11, 13, 10, 0, 0));
        when(repository.save(umaSolicitacaoRamalCanalD2d())).thenReturn(umaSolicitacaoRamalCanalD2d(1));

        service.save(criaSolicitacaoRamal(null));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService).getUsuarioId();
        verify(dataHoraAtual).getDataHora();
        verify(repository).save(umaSolicitacaoRamalCanalD2d());
    }

    @Test
    public void save_deveLancarException_quandoCanalD2dComSubCanalNull() {
        var request = criaSolicitacaoRamal(null);
        request.setSubCanalId(null);

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(request))
            .withMessage("Tipo de canal obrigatório para o canal D2D");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void save_deveLancarException_quandoCanalD2dComEquipeNull() {
        var request = criaSolicitacaoRamal(null);
        request.setEquipeId(null);

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(request))
            .withMessage("equipeId obrigatória para o canal D2D");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void save_deveLancarException_quando() {
        ReflectionTestUtils.setField(service, "destinatarios", "teste");
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoOperacao());
        when(autenticacaoService.getUsuarioId()).thenReturn(1);
        when(dataHoraAtual.getDataHora())
            .thenReturn(LocalDateTime.of(2023, 11, 13, 10, 0, 0));
        when(repository.save(umaSolicitacaoRamalCanalD2d())).thenReturn(umaSolicitacaoRamalCanalD2d(1));

        service.save(criaSolicitacaoRamal(null));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(autenticacaoService).getUsuarioId();
        verify(dataHoraAtual).getDataHora();
        verify(repository).save(umaSolicitacaoRamalCanalD2d());
    }

    @Test
    public void save_deveLancarException_seUsuarioAutenticadoNaoTiverPermissaoCTR_20015() {
        var solicitacaoRamal = criaSolicitacaoRamal(null);
        solicitacaoRamal.setCanal(ECanal.D2D_PROPRIO);
        solicitacaoRamal.setSubCanalId(1);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().id(1)
                .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_20014.getRole()))).build());

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("Sem autorização para fazer uma solicitação para este canal.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, never()).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_seJaHouverSolicitacaoPorEquipeId() {
        var solicitacaoRamal = criaSolicitacaoRamal(1);

        when(repository.findAllByPredicate(any())).thenReturn(List.of(umaSolicitacaoRamalCanalD2d(1)));

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("Não é possível salvar a solicitação de ramal, pois já existe uma pendente ou em andamento.");

        verify(repository, never()).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_quandoNaoHouverEquipeIdNaRequest() {
        var solicitacaoRamal = criaSolicitacaoRamal(1);
        solicitacaoRamal.setEquipeId(null);

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("equipeId obrigatória para o canal D2D");

        verify(repository, never()).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void getDadosAdicionais_deveChamarClientPeloSubCanalId() {
        when(subCanalService.getSubCanalById(1)).thenReturn(umSubCanal(1));
        when(callService.obterNomeTelefoniaPorId(1)).thenReturn(umaTelefonia());
        when(callService.obterRamaisParaCanal(ECanal.D2D_PROPRIO, 1)).thenReturn(List.of());

        service.getDadosAdicionais(umFiltrosSolicitacao(ECanal.D2D_PROPRIO, 1, null));

        verify(subCanalService).getSubCanalById(1);
        verify(callService).obterNomeTelefoniaPorId(1);
        verify(callService).obterRamaisParaCanal(ECanal.D2D_PROPRIO, 1);
    }

    @Test
    public void getDadosAdicionais_deveChamarClientPeloSubCanalId_quandoSubCanalCodigoNull() {
        var subCanal = umSubCanal(1);
        subCanal.setCodigo(null);
        when(subCanalService.getSubCanalById(1)).thenReturn(subCanal);
        when(callService.obterRamaisParaCanal(ECanal.D2D_PROPRIO, 1)).thenReturn(List.of());

        service.getDadosAdicionais(umFiltrosSolicitacao(ECanal.D2D_PROPRIO, 1, null));

        verify(subCanalService).getSubCanalById(1);
        verify(callService, never()).obterNomeTelefoniaPorId(1);
        verify(callService).obterRamaisParaCanal(ECanal.D2D_PROPRIO, 1);
    }

    @Test
    public void getDadosAdicionais_deveLancarException_quandoOcorrerAlgumErro() {
        when(subCanalService.getSubCanalById(1)).thenReturn(umSubCanal(1));
        when(callService.obterNomeTelefoniaPorId(1)).thenThrow(RetryableException.class);

        assertThatExceptionOfType(RetryableException.class)
            .isThrownBy(() -> service.getDadosAdicionais(umFiltrosSolicitacao(ECanal.D2D_PROPRIO, 1, null)));

        verify(subCanalService).getSubCanalById(1);
        verify(callService).obterNomeTelefoniaPorId(1);
    }

    @Test
    public void update_deveAtualizarSolicitacaoD2dSeTodosOsDadosPreenchidosCorretamente() {
        var solicitacao = umaSolicitacaoRamalCanalD2d();
        solicitacao.setId(1);

        when(solicitacaoRamalService.findById(1)).thenReturn(umaSolicitacaoRamalCanalD2d(1));
        when(repository.save(solicitacao)).thenReturn(umaSolicitacaoRamalCanalD2d(1));

        assertThat(service.update(criaSolicitacaoRamal(1)))
            .extracting("id")
            .contains(1);

        verify(solicitacaoRamalService).findById(1);
        verify(repository).save(solicitacao);
    }
}
