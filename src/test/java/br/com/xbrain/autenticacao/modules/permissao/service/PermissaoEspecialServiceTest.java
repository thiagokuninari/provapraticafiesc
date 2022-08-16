package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFiltros;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_GERENTE;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PermissaoEspecialServiceTest {

    private static final Integer DEPARTAMENTO_ID = 40;
    @InjectMocks
    private PermissaoEspecialService service;

    @Mock
    private AutenticacaoService autenticacaoService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PermissaoEspecialRepository repository;

    @Test
    public void processaPermissoesEspeciaisGerentesCoordenadores_deveProcessarPermissoes_seSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuario());
        when(usuarioService.getAllByPredicate(umFiltro())).thenReturn(umaListaUsuario());

        service.processaPermissoesEspeciaisGerentesCoordenadores();

        verify(autenticacaoService, times(1)).getUsuarioAutenticado();
        verify(usuarioService, times(1)).getAllByPredicate(umFiltro());
    }

    private UsuarioFiltros umFiltro() {
        return UsuarioFiltros.builder()
            .codigosCargos(List.of(AGENTE_AUTORIZADO_GERENTE, AGENTE_AUTORIZADO_COORDENADOR))
            .departamentoId(DEPARTAMENTO_ID)
            .build();
    }

    private List<Usuario> umaListaUsuario() {
        return List.of(
            Usuario
                .builder()
                .id(1)
                .build(),
            Usuario
                .builder()
                .id(2)
                .build());
    }

    private UsuarioAutenticado umUsuario() {
        return UsuarioAutenticado.builder()
            .id(1)
            .build();
    }
}
