package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
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

    @InjectMocks
    private PermissaoEspecialService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private PermissaoEspecialRepository repository;
    @Mock
    private AgenteAutorizadoService agenteAutorizadoService;
    @Mock
    private FeederService feederService;

    @Test
    public void processarPermissoesEspeciaisGerentesCoordenadores_deveProcessarPermissoes_seIdNull() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());

        service.processarPermissoesEspeciaisGerentesCoordenadores(null);

        verify(autenticacaoService, times(2)).getUsuarioAutenticado();
        verify(feederService, times(1)).salvarPermissoesEspeciaisCoordenadoresGerentes(eq(List.of()), eq(1));
    }

    @Test
    public void processarPermissoesEspeciaisGerentesCoordenadores_deveProcessarPermissoes_sePassarIdPorParametro() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(agenteAutorizadoService.getUsuariosAaFeederPorCargo(anyList(), anyList())).thenReturn(List.of(1, 2));

        service.processarPermissoesEspeciaisGerentesCoordenadores(List.of(1));

        verify(autenticacaoService, times(2)).getUsuarioAutenticado();
        verify(agenteAutorizadoService, times(1)).getUsuariosAaFeederPorCargo(List.of(1), umaListaCodigoCargo());
        verify(feederService, times(1)).salvarPermissoesEspeciaisCoordenadoresGerentes(eq(List.of(1, 2)), eq(1));
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1)
            .nivelCodigo(CodigoNivel.XBRAIN.name())
            .build();
    }

    private List<CodigoCargo> umaListaCodigoCargo() {
        return List.of(AGENTE_AUTORIZADO_GERENTE, AGENTE_AUTORIZADO_COORDENADOR);
    }
}
