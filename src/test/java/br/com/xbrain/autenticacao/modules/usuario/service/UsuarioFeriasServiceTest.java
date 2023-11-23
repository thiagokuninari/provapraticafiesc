package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioInativacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioFerias;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioFeriasRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioFerias;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioHelpDesk;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioFeriasServiceTest {

    @InjectMocks
    private UsuarioFeriasService service;
    @Mock
    private UsuarioFeriasRepository repository;
    @Captor
    private ArgumentCaptor<UsuarioFerias> argumentCaptorUsuarioFerias;

    @Test
    public void save_deveGerarORegistroDeFerias_quandoOMotivoDeInativacaoForFerias() {
        when(repository.save(any(UsuarioFerias.class))).thenReturn(umUsuarioFerias());

        service.save(
                umUsuarioHelpDesk(),
                UsuarioInativacaoDto
                        .builder()
                        .codigoMotivoInativacao(CodigoMotivoInativacao.FERIAS)
                        .dataInicio(LocalDate.of(2019, 1, 1))
                        .dataFim(LocalDate.of(2019, 2, 1))
                        .build());

        verify(repository, times(1)).save(argumentCaptorUsuarioFerias.capture());

        assertThat(argumentCaptorUsuarioFerias.getValue())
            .extracting("inicio", "fim")
            .contains(
                LocalDate.of(2019, 1, 1),
                LocalDate.of(2019, 2, 1));
    }

    @Test
    public void save_deveNaoGerarORegistroDeFerias_quandoOMotivoDeInativacaoNaoForFerias() {
        service.save(
                umUsuarioHelpDesk(),
                UsuarioInativacaoDto
                        .builder()
                        .codigoMotivoInativacao(CodigoMotivoInativacao.FERIAS)
                        .build());

        verify(repository, never()).save(any(UsuarioFerias.class));
    }
}
