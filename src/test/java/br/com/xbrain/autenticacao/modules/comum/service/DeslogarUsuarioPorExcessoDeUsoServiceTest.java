package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import br.com.xbrain.autenticacao.modules.comum.repository.UsuarioParaDeslogarRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.eq;
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
    public void deslogarUsuariosInativados_deveDeslogarOsUsuariosERemoverDaBase_quandoExistirRegistrosNaTabela() {
        when(repository.findAll()).thenReturn(umaListaDeUsuariosParaDeslogar());

        service.deslogarUsuariosInativados();

        verify(autenticacaoService, times(4))
            .logout(intThat(usuarioId -> umaListaDeUsuariosParaDeslogar()
                .stream()
                .map(UsuarioParaDeslogar::getUsuarioId)
                .anyMatch(integer -> Objects.equals(integer, usuarioId))));

        verify(repository).delete(eq(umaListaDeUsuariosParaDeslogar()));
    }

    @Test
    public void deslogarUsuariosInativados_naoDeveDeslogarNenhumUsuario_quandoTabelaForVazia() {
        when(repository.findAll()).thenReturn(List.of());

        service.deslogarUsuariosInativados();

        verify(autenticacaoService, times(0)).logout(anyInt());
        verify(repository, times(0)).delete(anyList());
    }

    private List<UsuarioParaDeslogar> umaListaDeUsuariosParaDeslogar() {
        return List.of(
            new UsuarioParaDeslogar(1),
            new UsuarioParaDeslogar(2),
            new UsuarioParaDeslogar(3),
            new UsuarioParaDeslogar(4));
    }
}