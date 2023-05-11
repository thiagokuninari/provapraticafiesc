package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.rabbitmq;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class AgenteAutorizadoMqListenerTest {

    private static final Integer PERMISSAO_TECNICO_INDICADOR_ID = 253;

    @InjectMocks
    private AgenteAutorizadoMqListener listener;
    @Mock
    private PermissaoEspecialService permissaoEspecialService;
    @Mock
    private UsuarioService usuarioService;

    @Test
    public void adicionarPermissaoTecnicoIndicador_deveAdicionarPermissaoAosUsuarios_quandoSolicitado() {
        when(usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3))).thenReturn(List.of(
            Usuario.builder().id(3).cargo(Cargo.builder().codigo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS).build()).build()));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(eq(3), eq(PERMISSAO_TECNICO_INDICADOR_ID))).thenReturn(false);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1);

        listener.adicionarPermissaoTecnicoIndicador(dto);

        assertThatCode(() -> permissaoEspecialService.save(
                List.of(PermissaoEspecial.of(3, PERMISSAO_TECNICO_INDICADOR_ID, 1))))
            .doesNotThrowAnyException();
    }

    @Test
    public void removerPermissaoTecnicoIndicador_deveRemoverPermissaoDosUsuarios_quandoSolicitado() {
        when(usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3))).thenReturn(List.of(
            Usuario.builder().id(3).cargo(Cargo.builder().codigo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS).build()).build()));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(eq(3), eq(PERMISSAO_TECNICO_INDICADOR_ID))).thenReturn(true);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1);

        listener.removerPermissaoTecnicoIndicador(dto);

        assertThatCode(() -> permissaoEspecialService.remover(3, PERMISSAO_TECNICO_INDICADOR_ID, 1))
            .doesNotThrowAnyException();
    }
}
