package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.ColaboradorVendasService;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
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
    private ColaboradorVendasService colaboradorVendasService;
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
        when(colaboradorVendasService.getUsuariosAaFeederPorCargo(anyList(), anyList())).thenReturn(List.of(1, 2));

        service.processarPermissoesEspeciaisGerentesCoordenadores(List.of(1));

        verify(autenticacaoService, times(2)).getUsuarioAutenticado();
        verify(colaboradorVendasService, times(1)).getUsuariosAaFeederPorCargo(List.of(1), umaListaCodigoCargo());
        verify(feederService, times(1)).salvarPermissoesEspeciaisCoordenadoresGerentes(eq(List.of(1, 2)), eq(1));
    }

    @Test
    public void save_deveChamarRepository_quandoReceberLista() {
        var listaPermissoes = List.of(
            new PermissaoEspecial()
        );
        repository.save(listaPermissoes);
        verify(repository).save(eq(listaPermissoes));
    }

    @Test
    public void hasPermissaoEspecialAtiva_deveRetornarTrue_seUsuarioPossuirPermissaoEspecialAtiva() {
        when(repository.existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(eq(1), eq(1000)))
            .thenReturn(true);

        assertThat(service.hasPermissaoEspecialAtiva(1, 1000))
            .isTrue();

        verify(repository).existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(eq(1), eq(1000));
    }

    @Test
    public void hasPermissaoEspecialAtiva_deveRetornarFalse_seUsuarioNaoPossuirPermissaoEspecialAtiva() {
        when(repository.existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(eq(1), eq(1000)))
            .thenReturn(false);

        assertThat(service.hasPermissaoEspecialAtiva(1, 1000))
            .isFalse();

        verify(repository).existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(eq(1), eq(1000));
    }

    @Test
    public void deletarPermissoesEspeciaisBy_deveRemoverPermissoesDosUsuariosEChamarRepository_quandoChamado() {
        var funcionalidadesIds = List.of(1000, 2000);
        var usuariosIds = List.of(1, 2, 3);

        service.deletarPermissoesEspeciaisBy(funcionalidadesIds, usuariosIds);

        verify(repository).deletarPermissaoEspecialBy(eq(funcionalidadesIds), eq(usuariosIds));
    }

    @Test
    public void processarPermissoesEspeciaisGerentesCoordenadores_naoDeveProcessarPermissoes_seUsuarioAutenticadoNaoForXbrain() {
        var usuarioAutenticadoOperacao = umUsuarioAutenticado();
        usuarioAutenticadoOperacao.setNivelCodigo(CodigoNivel.OPERACAO.name());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticadoOperacao);

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.processarPermissoesEspeciaisGerentesCoordenadores(List.of(1)))
            .withMessageContaining("Usuário não autorizado!");

        verify(colaboradorVendasService, never()).getUsuariosAaFeederPorCargo(any(), any());
        verify(feederService, never()).salvarPermissoesEspeciaisCoordenadoresGerentes(any(), anyInt());
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
