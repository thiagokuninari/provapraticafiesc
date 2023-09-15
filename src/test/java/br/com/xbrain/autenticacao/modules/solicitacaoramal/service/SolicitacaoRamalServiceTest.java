package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalHistoricoRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.helper.SolicitacaoRamalHelper.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
public class SolicitacaoRamalServiceTest {

    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private SolicitacaoRamalRepository repository;
    @Autowired
    private SolicitacaoRamalService service;
    @MockBean
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
    @MockBean
    private SolicitacaoRamal solicitacaoRamal;
    @MockBean
    private SolicitacaoRamalHistoricoService solicitacaoRamalHistoricoService;
    @MockBean
    private SolicitacaoRamalHistoricoRepository solicitacaoRamalHistoricoRepository;
    @MockBean
    private SolicitacaoRamalHistorico solicitacaoRamalHistorico;

    @Test
    public void calcularDataFinalizacao_quandoHouverRegistros() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder()
                .nivelCodigo(ENivel.XBRAIN.name()).build());

        when(repository.findAllByPredicate(any())).thenReturn(umaListaSolicitacaoRamal());

        service.calcularDataFinalizacao(new SolicitacaoRamalFiltros());

        verify(repository, times(1)).save(umaListaSolicitacaoRamal());
    }

    @Test
    public void calcularDataFinalizacao_quandoNaoHouverRegistros() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder()
                .nivelCodigo(ENivel.XBRAIN.name()).build());

        when(repository.findAllByPredicate(any())).thenReturn(umaListaSolicitacaoRamalEmpty());

        service.calcularDataFinalizacao(new SolicitacaoRamalFiltros());

        verify(repository, never()).save(umaListaSolicitacaoRamalEmpty());
    }

    @Test
    public void save_validacaoException_quandoTentarSalvarSolicitacaoHavendoUmaEmPendenteOuEmAndamento() {
        when(repository.findAllByAgenteAutorizadoIdAndSituacaoDiferentePendenteOuEmAndamento(eq(1) ))
            .thenReturn(List.of(umaOutraSolicitacaoRamal(1)));
        when(agenteAutorizadoNovoService.getUsuariosAaAtivoSemVendedoresD2D(1))
            .thenReturn(List.of(umUsuarioAgenteAutorizadoResponse(1)));
        when(agenteAutorizadoNovoService.getAaById(1)).thenReturn(umAgenteAutorizadoResponse());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umaSolicitacaoRamalRequest()))
            .withMessage("Não é possível salvar a solicitação de ramal, pois já existe uma pendente ou em andamento.");

        verify(repository, never()).save(umaSolicitacaoRamal(1));
    }

    @Test
    public void save_validacaoException_quandoValidarQuantidadeTrue() {
        when(repository.findAllByAgenteAutorizadoIdAndSituacaoEnviadoOuConcluido(eq(1) ))
            .thenReturn(umaListaSolicitacaoRamal());
        when(agenteAutorizadoNovoService.getUsuariosAaAtivoSemVendedoresD2D(1))
            .thenReturn(List.of(umUsuarioAgenteAutorizadoResponse(1)));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umaSolicitacaoRamalRequest()))
            .withMessage("Não é possível salvar a solicitação de ramal, pois excedeu o limite.");

        verify(repository, never()).save(umaSolicitacaoRamal(1));
    }

    @Test
    public void save_salvarSolicitacaoRamal_quandoSolicitado() {
        when(repository.findAllByAgenteAutorizadoIdAndSituacaoEnviadoOuConcluido(eq(1) ))
            .thenReturn(umaListaSolicitacaoRamalEmpty());
        when(agenteAutorizadoNovoService.getUsuariosAaAtivoSemVendedoresD2D(1))
            .thenReturn(List.of(umUsuarioAgenteAutorizadoResponse(1)));
        when(agenteAutorizadoNovoService.getAaById(1)).thenReturn(umAgenteAutorizadoResponse());

        when(service.enviarEmailSolicitacoesQueVaoExpirar()).thenReturn(umaListaSolicitacaoRamal());
        when(repository.save(umaSolicitacaoRamal(1))).thenReturn(umaSolicitacaoRamal(1));

        when(solicitacaoRamalHistorico.gerarHistorico(umaSolicitacaoRamal(1), null)).thenReturn(umaSolicitacaoRamalHistorico());

        assertThat(service.save(umaSolicitacaoRamalRequest()))
            .extracting("id", "agenteAutorizadoId", "tipoImplantacao")
            .contains(1, 1, ETipoImplantacao.ESCRITORIO.getDescricao());

        verify(repository, times(1)).save(umaSolicitacaoRamal(1));
    }
}
