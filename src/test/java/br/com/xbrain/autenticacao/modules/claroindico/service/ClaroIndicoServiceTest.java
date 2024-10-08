package br.com.xbrain.autenticacao.modules.claroindico.service;

import br.com.xbrain.autenticacao.modules.claroindico.client.ClaroIndicoClient;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static br.com.xbrain.autenticacao.modules.comum.helper.FileHelper.umUsuario;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO_VENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.BACKOFFICE_GERENTE_TRATAMENTO_VENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.BACKOFFICE_OPERADOR_TRATAMENTO_VENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.BACKOFFICE_CENTRALIZADO;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClaroIndicoServiceTest {

    @InjectMocks
    private ClaroIndicoService service;
    @Mock
    private ClaroIndicoClient client;

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
        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umGerenteBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_naDeveChamarClient_quandoOperadorMudarCargoParaAnalista() {
        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umAnalistaBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client, never()).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_naDeveChamarClient_quandoGerenteMudarCargoParaOperador() {
        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umGerenteBkoCentralizado(), umOperadorBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client, never()).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_naDeveChamarClient_quandoAdicionarNovoCargoParaOUsuario() {
        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umUsuario(), umOperadorBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client, never()).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_naDeveChamarClient_quandoNaoHouverAlteracaoDeCargo() {
        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umOperadorBkoCentralizado()))
            .doesNotThrowAnyException();

        verify(client, never()).desvincularUsuarioDaFilaTratamento(1);
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_deveLancarException_quandoInformarUsuariosEOcorrerErro() {
        doThrow(new RetryableException("", null))
            .when(client).desvincularUsuarioDaFilaTratamento(anyInt());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umGerenteBkoCentralizado()))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("Ocorreu um erro ao desvincular usuário da fila de tratamento.");
    }

    @Test
    public void desvincularUsuarioDaFilaTratamento_deveLancarIntegracaoException_quandoInformarUsuariosEOcorrerErro() {
        doThrow(new HystrixBadRequestException("", null))
            .when(client).desvincularUsuarioDaFilaTratamento(anyInt());

        assertThatCode(() -> service.desvincularUsuarioDaFilaTratamento(
                umOperadorBkoCentralizado(), umGerenteBkoCentralizado()))
            .isInstanceOf(IntegracaoException.class);

        verify(client).desvincularUsuarioDaFilaTratamento(anyInt());
    }

    private Usuario umOperadorBkoCentralizado() {
        var usuarioAntigo = umUsuario();
        usuarioAntigo.setCargo(
            Cargo.builder()
                .id(115)
                .codigo(BACKOFFICE_OPERADOR_TRATAMENTO_VENDAS)
                .nivel(Nivel.builder().codigo(BACKOFFICE_CENTRALIZADO).build())
                .build());
        usuarioAntigo.setOrganizacaoEmpresa(new OrganizacaoEmpresa(1));

        return usuarioAntigo;
    }

    private Usuario umAnalistaBkoCentralizado() {
        var usuarioAntigo = umUsuario();
        usuarioAntigo.setCargo(
            Cargo.builder()
                .id(116)
                .codigo(BACKOFFICE_ANALISTA_TRATAMENTO_VENDAS)
                .nivel(Nivel.builder().codigo(BACKOFFICE_CENTRALIZADO).build())
                .build());
        usuarioAntigo.setOrganizacaoEmpresa(new OrganizacaoEmpresa(1));

        return usuarioAntigo;
    }

    private Usuario umGerenteBkoCentralizado() {
        var usuarioAtualizado = umUsuario();
        usuarioAtualizado.setCargo(
            Cargo.builder()
                .id(117)
                .codigo(BACKOFFICE_GERENTE_TRATAMENTO_VENDAS)
                .nivel(Nivel.builder().codigo(BACKOFFICE_CENTRALIZADO).build())
                .build());
        usuarioAtualizado.setOrganizacaoEmpresa(new OrganizacaoEmpresa(1));

        return usuarioAtualizado;
    }
}
