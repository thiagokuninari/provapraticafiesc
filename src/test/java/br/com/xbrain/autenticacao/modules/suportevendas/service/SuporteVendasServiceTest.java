package br.com.xbrain.autenticacao.modules.suportevendas.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.suportevendas.client.SuporteVendasClient;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.umCargoAnalistaOperacao;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioComCargoEOrganizacao;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SuporteVendasServiceTest {

    @InjectMocks
    private SuporteVendasService service;
    @Mock
    private SuporteVendasClient client;

    @Test
    public void desvincularGruposByUsuario_deveChamarClient_quandoNaoOcorrerErroEUsuarioSuporteVendasETiverAlteracaoNoCargo() {
        var usuarioAntigo = umUsuarioComCargoEOrganizacao(100, 100);
        var usuarioNovo = umUsuarioComCargoEOrganizacao(200, 100);

        assertThatCode(() -> service.desvincularGruposByUsuario(usuarioAntigo, usuarioNovo))
            .doesNotThrowAnyException();

        verify(client).desvincularGruposByUsuarioId(100);
    }

    @Test
    public void desvincularGruposByUsuario_deveLancarException_quandoClientRetornarErro() {
        var usuarioAntigo = umUsuarioComCargoEOrganizacao(100, 100);
        var usuarioNovo = umUsuarioComCargoEOrganizacao(100, 200);

        doThrow(new RetryableException("", null))
            .when(client).desvincularGruposByUsuarioId(anyInt());

        assertThatCode(() -> service.desvincularGruposByUsuario(usuarioAntigo, usuarioNovo))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("Ocorreu um erro ao desvincular grupo do usuário no suporte-vendas.");
    }

    @Test
    public void desvincularGruposByUsuario_naoDeveChamarClient_quandoUsuarioNaoForSuporteVendas() {
        var usuarioAntigo = umUsuarioComCargoEOrganizacao(100, 100);
        usuarioAntigo.setCargo(umCargoAnalistaOperacao());
        var usuarioNovo = umUsuarioComCargoEOrganizacao(100, 200);

        assertThatCode(() -> service.desvincularGruposByUsuario(usuarioAntigo, usuarioNovo))
            .doesNotThrowAnyException();

        verifyZeroInteractions(client);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void desvincularGruposByUsuario_naoDeveChamarClient_quandoUsuarioSuporteDeVendasENaoHouverAlteracaoNoCargoNemNaOrganizacao() {
        var usuarioAntigo = umUsuarioComCargoEOrganizacao(100, 100);
        var usuarioNovo = umUsuarioComCargoEOrganizacao(100, 100);

        assertThatCode(() -> service.desvincularGruposByUsuario(usuarioAntigo, usuarioNovo))
            .doesNotThrowAnyException();

        verifyZeroInteractions(client);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void desvincularGruposByUsuario_deveChamarClient_quandoNaoOcorrerErroEUsuarioSuporteVendasETiverAlteracaoNaOrganizacao() {
        var usuarioAntigo = umUsuarioComCargoEOrganizacao(100, 100);
        var usuarioNovo = umUsuarioComCargoEOrganizacao(100, 200);

        assertThatCode(() -> service.desvincularGruposByUsuario(usuarioAntigo, usuarioNovo))
            .doesNotThrowAnyException();

        verify(client).desvincularGruposByUsuarioId(100);
    }
}
