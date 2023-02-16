package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalHistoricoRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.SolicitacaoRamalHelper.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SolicitacaoRamalServiceTest {

    @InjectMocks
    private SolicitacaoRamalService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private SolicitacaoRamalRepository repository;
    @Mock
    private SolicitacaoRamalHistoricoRepository historicoRepository;

    @Test
    public void calcularDataFinalizacao_deveSetarADataFinalizacao_seHouverRegistros() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder()
                .nivelCodigo(ENivel.XBRAIN.name()).build());

        when(repository.findAllByPredicate(any())).thenReturn(umaListaSolicitacaoRamal());

        service.calcularDataFinalizacao(new SolicitacaoRamalFiltros());

        verify(repository, times(1)).save(umaListaSolicitacaoRamal());
    }

    @Test
    public void calcularDataFinalizacao_naoDeveSetarADataFinalizacao_seNaoHouverRegistros() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder()
                .nivelCodigo(ENivel.XBRAIN.name()).build());

        when(repository.findAllByPredicate(any())).thenReturn(umaListaSolicitacaoRamalEmpty());

        service.calcularDataFinalizacao(new SolicitacaoRamalFiltros());

        verify(repository, never()).save(umaListaSolicitacaoRamalEmpty());
    }

    @Test
    public void remover_deveDeletarSolicitacaoRamal_seSolicitacaoRamalComStatusPendente() {
        when(repository.findById(1)).thenReturn(Optional.of(umaSolicitacaoRamal(ESituacaoSolicitacao.PENDENTE)));

        service.remover(1);

        verify(historicoRepository, times(1)).deleteAll(1);
        verify(repository, times(1)).delete(umaSolicitacaoRamal(ESituacaoSolicitacao.PENDENTE));
    }

    @Test(expected = NotFoundException.class)
    public void remover_deveRetornarNotFoundException_seSolicitacaoRamalNaoExistir() {
        service.remover(1000);
    }

    @Test(expected = ValidacaoException.class)
    public void remover_deveRetornarValidacaoException_seSolicitacaoRamalComStatusDiferenteDePendente() {
        when(repository.findById(1)).thenReturn(Optional.of(umaSolicitacaoRamal(ESituacaoSolicitacao.EM_ANDAMENTO)));

        service.remover(1);

        verify(historicoRepository, never()).deleteAll(1);
        verify(repository, never()).delete(umaSolicitacaoRamal(ESituacaoSolicitacao.EM_ANDAMENTO));
    }
}
