package br.com.xbrain.autenticacao.modules.claroindico.service;

import br.com.xbrain.autenticacao.modules.claroindico.client.ClaroIndicoClient;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoService;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.helper.FileHelper.umUsuario;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO_VENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.BACKOFFICE_GERENTE_TRATAMENTO_VENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.BACKOFFICE_OPERADOR_TRATAMENTO_VENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.BACKOFFICE_SUPERVISOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.BACKOFFICE;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.BACKOFFICE_CENTRALIZADO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClaroIndicoServiceTest {

    @InjectMocks
    private ClaroIndicoService service;
    @Mock
    private ClaroIndicoClient client;
    @Mock
    private CargoService cargoService;

    @Test
    public void buscarUsuariosVinculados_deveChamarClient_quandoNaoOcorrerErroERetornarListaDeIds() {
        when(client.buscarUsuariosVinculados()).thenReturn(List.of(1, 2));

        assertThatCode(() -> service.buscarUsuariosVinculados())
            .doesNotThrowAnyException();
        assertThat(service.buscarUsuariosVinculados()).isNotEmpty();

        verify(client, atLeastOnce()).buscarUsuariosVinculados();
    }

    @Test
    public void buscarUsuariosVinculados_deveChamarClient_quandoNaoOcorrerErroERetornarListaVazia() {
        when(client.buscarUsuariosVinculados()).thenReturn(List.of());

        assertThatCode(() -> service.buscarUsuariosVinculados())
            .doesNotThrowAnyException();
        assertThat(service.buscarUsuariosVinculados()).isEmpty();

        verify(client, atLeastOnce()).buscarUsuariosVinculados();
    }

    @Test
    public void buscarUsuariosVinculados_deveLancarException_quandoOcorrerErro() {
        doThrow(new RetryableException("", null))
            .when(client).buscarUsuariosVinculados();

        assertThatCode(() -> service.buscarUsuariosVinculados())
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("Ocorreu um erro ao buscar usuários vinculados às filas de tratamento.");
    }

    @Test
    public void buscarUsuariosVinculados_deveLancarIntegracaoException_quandoOcorrerErro() {
        doThrow(new HystrixBadRequestException("", null))
            .when(client).buscarUsuariosVinculados();

        assertThatCode(() -> service.buscarUsuariosVinculados())
            .isInstanceOf(IntegracaoException.class);

        verify(client).buscarUsuariosVinculados();
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_deveChamarClient_quandoInformarIdENaoOcorrerErro() {
        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(1))
            .doesNotThrowAnyException();

        verify(client).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_deveLancarException_quandoInformarIdEOcorrerErro() {
        doThrow(new RetryableException("", null))
            .when(client).desvincularUsuarioDaFilaTratamento(anyInt());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("Ocorreu um erro ao desvincular usuário da fila de tratamento.");
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_deveLancarIntegracaoException_quandoInformarIdEOcorrerErro() {
        doThrow(new HystrixBadRequestException("", null))
            .when(client).desvincularUsuarioDaFilaTratamento(anyInt());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(1))
            .isInstanceOf(IntegracaoException.class);

        verify(client).desvincularUsuarioDaFilaTratamento(anyInt());
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_deveChamarClient_quandoInformarUsuariosENaoOcorrerErro() {
        when(cargoService.findById(anyInt()))
            .thenReturn(Cargo.builder().nivel(Nivel.builder().id(1).build()).build());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umGerenteBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_deveChamarClient_quandoHouverAlteracaoParaNivelBackoffice() {
        when(cargoService.findById(anyInt()))
            .thenReturn(Cargo.builder().nivel(Nivel.builder().id(2).build()).build());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umSupervisorBackoffice()))
            .doesNotThrowAnyException();

        verify(client).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_naoDeveChamarClient_quandoHouverAlteracaoParaNivelBkoCentralizado() {
        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umSupervisorBackoffice(), umOperadorBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client, never()).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_naoDeveChamarClient_quandoOperadorMudarCargoParaAnalista() {
        when(cargoService.findById(anyInt()))
            .thenReturn(Cargo.builder().nivel(Nivel.builder().id(1).build()).build());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umAnalistaBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client, never()).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_naoDeveChamarClient_quandoGerenteMudarCargoParaOperador() {
        when(cargoService.findById(anyInt()))
            .thenReturn(Cargo.builder().nivel(Nivel.builder().id(1).build()).build());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umGerenteBkoCentralizado(), umOperadorBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client, never()).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_naoDeveChamarClient_quandoAdicionarNovoCargoParaOUsuario() {
        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umUsuario(), umOperadorBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client, never()).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_naoDeveChamarClient_quandoNaoHouverAlteracaoDeCargo() {
        when(cargoService.findById(anyInt()))
            .thenReturn(Cargo.builder().nivel(Nivel.builder().id(1).build()).build());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umOperadorBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client, never()).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_deveLancarException_quandoInformarUsuariosEOcorrerErro() {
        when(cargoService.findById(anyInt()))
            .thenReturn(Cargo.builder().nivel(Nivel.builder().id(1).build()).build());

        doThrow(new RetryableException("", null))
            .when(client).desvincularUsuarioDaFilaTratamento(anyInt());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umGerenteBkoCentralizado()))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("Ocorreu um erro ao desvincular usuário da fila de tratamento.");
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_deveLancarIntegracaoException_quandoInformarUsuariosEOcorrerErro() {
        when(cargoService.findById(anyInt()))
            .thenReturn(Cargo.builder().nivel(Nivel.builder().id(1).build()).build());

        doThrow(new HystrixBadRequestException("", null))
            .when(client).desvincularUsuarioDaFilaTratamento(anyInt());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umGerenteBkoCentralizado()))
            .isInstanceOf(IntegracaoException.class);

        verify(client).desvincularUsuarioDaFilaTratamento(anyInt());
    }

    @Test
    public void desvincularUsuarioDaFilaTratamentoInativacao_deveChamarClient_quandoInformarIdENaoOcorrerErro() {
        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamentoInativacao(1))
            .doesNotThrowAnyException();

        verify(client).desvincularUsuarioDaFilaTratamentoInativacao(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamentoInativacao_deveLancarException_quandoInformarIdEOcorrerErro() {
        doThrow(new RetryableException("", null))
            .when(client).desvincularUsuarioDaFilaTratamentoInativacao(anyInt());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamentoInativacao(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("Ocorreu um erro ao desvincular usuário da fila de tratamento por inativação.");
    }

    @Test
    public void desvincularUsuarioDaFilaTratamentoInativacao_deveLancarIntegracaoException_quandoInformarIdEOcorrerErro() {
        doThrow(new HystrixBadRequestException("", null))
            .when(client).desvincularUsuarioDaFilaTratamentoInativacao(anyInt());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamentoInativacao(1))
            .isInstanceOf(IntegracaoException.class);

        verify(client).desvincularUsuarioDaFilaTratamentoInativacao(anyInt());
    }

    private Usuario umOperadorBkoCentralizado() {
        var usuario = umUsuario();
        usuario.setCargo(
            Cargo.builder()
                .id(115)
                .codigo(BACKOFFICE_OPERADOR_TRATAMENTO_VENDAS)
                .nivel(Nivel.builder().id(1).codigo(BACKOFFICE_CENTRALIZADO).build())
                .build());
        usuario.setOrganizacaoEmpresa(new OrganizacaoEmpresa(1));

        return usuario;
    }

    private Usuario umAnalistaBkoCentralizado() {
        var usuario = umUsuario();
        usuario.setCargo(
            Cargo.builder()
                .id(116)
                .codigo(BACKOFFICE_ANALISTA_TRATAMENTO_VENDAS)
                .nivel(Nivel.builder().id(1).codigo(BACKOFFICE_CENTRALIZADO).build())
                .build());
        usuario.setOrganizacaoEmpresa(new OrganizacaoEmpresa(1));

        return usuario;
    }

    private Usuario umGerenteBkoCentralizado() {
        var usuario = umUsuario();
        usuario.setCargo(
            Cargo.builder()
                .id(117)
                .codigo(BACKOFFICE_GERENTE_TRATAMENTO_VENDAS)
                .nivel(Nivel.builder().id(1).codigo(BACKOFFICE_CENTRALIZADO).build())
                .build());
        usuario.setOrganizacaoEmpresa(new OrganizacaoEmpresa(1));

        return usuario;
    }

    private Usuario umSupervisorBackoffice() {
        var usuario = umUsuario();
        usuario.setCargo(
            Cargo.builder()
                .id(100)
                .codigo(BACKOFFICE_SUPERVISOR)
                .nivel(Nivel.builder().id(2).codigo(BACKOFFICE).build())
                .build());
        usuario.setOrganizacaoEmpresa(new OrganizacaoEmpresa(2));

        return usuario;
    }
}
