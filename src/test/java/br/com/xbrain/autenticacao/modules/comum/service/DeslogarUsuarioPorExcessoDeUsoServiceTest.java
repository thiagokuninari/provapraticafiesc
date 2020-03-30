package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import br.com.xbrain.autenticacao.modules.comum.repository.UsuarioParaDeslogarRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import({DeslogarUsuarioPorExcessoDeUsoService.class})
public class DeslogarUsuarioPorExcessoDeUsoServiceTest {

    @Autowired
    private DeslogarUsuarioPorExcessoDeUsoService service;
    @MockBean
    private UsuarioParaDeslogarRepository repository;
    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    public void deslogarUsuariosInativados_deveDeslogarOsUsuariosEAtualizarParaDeslogado_quandoExistirRegistrosNaTabela() {
        when(repository.findAllByDeslogado(any())).thenReturn(umaListaDeUsuariosParaDeslogar());

        service.deslogarUsuariosInativados();

        verify(autenticacaoService, times(4))
            .logout(intThat(usuarioId -> umaListaDeUsuariosParaDeslogar()
                .stream()
                .map(UsuarioParaDeslogar::getUsuarioId)
                .anyMatch(integer -> Objects.equals(integer, usuarioId))));

        verify(repository, times(4)).save(any(UsuarioParaDeslogar.class));
    }

    @Test
    public void deslogarUsuariosInativados_naoDeveDeslogarNenhumUsuario_quandoTabelaForVazia() {
        when(repository.findAll()).thenReturn(List.of());

        service.deslogarUsuariosInativados();

        verify(autenticacaoService, times(0)).logout(anyInt());
        verify(repository, times(0)).save(any(UsuarioParaDeslogar.class));
    }

    @Test
    public void deslogarUsuariosInativados_naoDeveDeslogarNenhumUsuario_quandoTodosEstiveremDeslogados() {
        when(repository.findAll()).thenReturn(umaListaDeUsuariosDeslogados());

        service.deslogarUsuariosInativados();

        verify(autenticacaoService, times(0)).logout(anyInt());
        verify(repository, times(0)).save(any(UsuarioParaDeslogar.class));
    }

    @Test
    public void validarUsuarioBloqueadoPorExcessoDeUso_deveRetornarUsuario_seExistirUsuarioBloqueado() {
        when(repository.findByUsuarioId(anyInt())).thenReturn(Optional.of(umaListaDeUsuariosParaDeslogar().get(0)));

        var usuario = service.validarUsuarioBloqueadoPorExcessoDeUso(1);

        assertThat(usuario).isNotNull();
        assertThat(usuario.getUsuarioId()).isEqualTo(1);
        assertThat(usuario.isBloqueado()).isTrue();

        verify(repository, times(1)).findByUsuarioId(anyInt());
    }

    @Test
    public void validarUsuarioBloqueadoPorExcessoDeUso_deveLancarException_seNaoHouverUsuarioBloqueado() {
        when(repository.findByUsuarioId(anyInt())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.validarUsuarioBloqueadoPorExcessoDeUso(1))
            .withMessage("Não há bloqueios para este usuário.");

        verify(repository, times(1)).findByUsuarioId(anyInt());
    }

    private List<UsuarioParaDeslogar> umaListaDeUsuariosParaDeslogar() {
        return List.of(
            new UsuarioParaDeslogar(1, LocalDateTime.now(), Eboolean.F),
            new UsuarioParaDeslogar(2, LocalDateTime.now(), Eboolean.F),
            new UsuarioParaDeslogar(3, LocalDateTime.now(), Eboolean.F),
            new UsuarioParaDeslogar(4, LocalDateTime.now(), Eboolean.F));
    }

    private List<UsuarioParaDeslogar> umaListaDeUsuariosDeslogados() {
        return List.of(
            new UsuarioParaDeslogar(1, LocalDateTime.now(), Eboolean.V),
            new UsuarioParaDeslogar(2, LocalDateTime.now(), Eboolean.V),
            new UsuarioParaDeslogar(3, LocalDateTime.now(), Eboolean.V),
            new UsuarioParaDeslogar(4, LocalDateTime.now(), Eboolean.V));
    }
}
