package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioHistoricoServiceTest {

    @InjectMocks
    private UsuarioHistoricoService service;

    @Mock
    private UsuarioHistoricoRepository repository;

    @Test
    public void save_deveSalvarListaDeUsuarioHistorico() {
        assertThatCode(() -> service.save(List.of(new UsuarioHistorico(), new UsuarioHistorico())))
            .doesNotThrowAnyException();

        verify(repository, times(1)).save(anyList());
    }

    @Test
    public void getHistoricoDoUsuario_deveRetornarHistoricoDoUsuario_quandoSolicitado() {
        when(repository.getHistoricoDoUsuario(1)).thenReturn(umaListHistoricoUsuario());

        assertThat(service.getHistoricoDoUsuario(1))
            .extracting("id", "situacao", "observacao")
            .containsExactlyInAnyOrder(
                tuple(1, "ATIVO", "teste")
            );

        verify(repository).getHistoricoDoUsuario(1);
    }

    @Test
    public void getHistoricoDoUsuario_deveRetornarListaVazia_seNaoEncontrarDados() {
        when(repository.getHistoricoDoUsuario(1)).thenReturn(Collections.emptyList());

        assertThat(service.getHistoricoDoUsuario(1))
            .isEqualTo(Collections.emptyList());

        verify(repository).getHistoricoDoUsuario(1);
    }

    private List<UsuarioHistorico> umaListHistoricoUsuario() {
        return List.of(UsuarioHistorico.builder()
            .id(1)
            .usuario(new Usuario())
            .situacao(ESituacao.A)
            .observacao("teste")
            .build());
    }
}
