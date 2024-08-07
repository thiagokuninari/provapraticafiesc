package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalHistoricoRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SolicitacaoRamalHistoricoServiceTest {

    @InjectMocks
    private SolicitacaoRamalHistoricoService service;

    @Mock
    private SolicitacaoRamalHistoricoRepository repository;

    @Test
    public void save_deveSalvarSolicitacaoRamalHistorico_quandoDadosInformados() {
        var solicitacaoRamalHistorico = SolicitacaoRamalHistorico.builder()
            .id(1)
            .comentario("comentario")
            .situacao(ESituacaoSolicitacao.CONCLUIDO)
            .usuario(Usuario.builder().id(1).nome("nome").build())
            .build();

        service.save(solicitacaoRamalHistorico);

        verify(repository).save(solicitacaoRamalHistorico);
    }
}
