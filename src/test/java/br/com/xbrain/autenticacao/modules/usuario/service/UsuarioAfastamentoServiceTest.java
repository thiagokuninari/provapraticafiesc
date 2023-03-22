package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioInativacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioAfastamento;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioAfastamentoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.AFASTAMENTO;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioAfastamento;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioHelpDesk;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioAfastamentoServiceTest {

    @InjectMocks
    private UsuarioAfastamentoService service;
    @Mock
    private UsuarioAfastamentoRepository repository;
    @Captor
    private ArgumentCaptor<UsuarioAfastamento> argumentCaptorUsuarioAfastamento;

    @Test
    public void save_deveGerarORegistroDeAfastamemto_quandoOMotivoDeInativacaoForAfastamento() {
        when(repository.save(any(UsuarioAfastamento.class))).thenReturn(umUsuarioAfastamento());

        service.save(
            umUsuarioHelpDesk(),
            UsuarioInativacaoDto
                .builder()
                .codigoMotivoInativacao(AFASTAMENTO)
                .dataInicio(LocalDate.of(2019, 1, 1))
                .dataFim(LocalDate.of(2019, 2, 1))
                .build());

        verify(repository, times(1)).save(argumentCaptorUsuarioAfastamento.capture());

        assertThat(argumentCaptorUsuarioAfastamento.getValue())
            .extracting("inicio", "fim")
            .contains(
                    LocalDate.of(2019, 1, 1),
                    LocalDate.of(2019, 2, 1));
    }

    @Test
    public void save_deveNaoGerarORegistroDeAfastamento_quandoNaoConterDataInicialEDataFim() {
        service.save(
            umUsuarioHelpDesk(),
            UsuarioInativacaoDto
                .builder()
                .codigoMotivoInativacao(AFASTAMENTO)
                .build());

        verify(repository, never()).save(any(UsuarioAfastamento.class));
    }
}
