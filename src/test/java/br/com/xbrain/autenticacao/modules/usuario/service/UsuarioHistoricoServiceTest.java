package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}
