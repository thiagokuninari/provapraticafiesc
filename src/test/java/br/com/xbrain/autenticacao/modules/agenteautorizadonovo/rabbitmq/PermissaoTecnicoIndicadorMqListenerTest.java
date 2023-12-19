package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.rabbitmq;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.agenteautorizado.rabbitmq.PermissaoTecnicoIndicadorMqListener;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.PermissaoTecnicoIndicadorService;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class PermissaoTecnicoIndicadorMqListenerTest {

    @InjectMocks
    private PermissaoTecnicoIndicadorMqListener listener;
    @Mock
    private PermissaoTecnicoIndicadorService service;

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveAdicionarPermissaoAosUsuarios_quandoSolicitado() {
        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        listener.atualizarPermissaoTecnicoIndicador(dto);

        verify(service, times(1)).atualizarPermissaoTecnicoIndicador(eq(dto));
    }
}
