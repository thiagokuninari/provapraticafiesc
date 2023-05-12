package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.rabbitmq;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.AGENTE_AUTORIZADO;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
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
    public void atualizarPermissaoTecnicoIndicador_deveAdicionarPermissaoAosUsuarios_quandoSolicitado() {
        var usuario = umUsuario(3,
            Cargo.builder().codigo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS).build(),
            Set.of(AGENTE_AUTORIZADO));

        when(usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3))).thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(eq(3), eq(PERMISSAO_TECNICO_INDICADOR_ID))).thenReturn(false);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        listener.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, times(1)).save(anyList());
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_naoDeveAdicionarPermissaoAosUsuarios_quandoUsuarioJaPossuirPermissao() {
        var usuario = umUsuario(3,
            Cargo.builder().codigo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS).build(),
            Set.of(AGENTE_AUTORIZADO));

        when(usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3))).thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(eq(3), eq(PERMISSAO_TECNICO_INDICADOR_ID))).thenReturn(true);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        listener.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, never()).save(anyList());
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveRemoverPermissaoDosUsuarios_quandoSolicitado() {
        var usuario = umUsuario(3,
            Cargo.builder().codigo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS).build(),
            Set.of(AGENTE_AUTORIZADO));

        when(usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3))).thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(eq(3), eq(PERMISSAO_TECNICO_INDICADOR_ID))).thenReturn(true);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.F);

        listener.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, times(1)).remover(eq(3), eq(PERMISSAO_TECNICO_INDICADOR_ID), eq(1));
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_naoDeveRemoverPermissaoDosUsuarios_quandoUsuarioNaoPossuirPermissao() {
        var usuario = umUsuario(3,
            Cargo.builder().codigo(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS).build(),
            Set.of(AGENTE_AUTORIZADO));

        when(usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3))).thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(eq(3), eq(PERMISSAO_TECNICO_INDICADOR_ID))).thenReturn(false);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.F);

        listener.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, never()).remover(eq(3), eq(PERMISSAO_TECNICO_INDICADOR_ID), eq(1));
    }
}
