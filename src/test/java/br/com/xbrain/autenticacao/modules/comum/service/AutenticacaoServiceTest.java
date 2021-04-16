package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AutenticacaoServiceTest {

    private static final Integer LISTA_MAIOR_1000 = 2000;
    private static final Integer LISTA_MENOR_1000 = 999;

    @InjectMocks
    private AutenticacaoService autenticacaoService;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void logout_deveDeslogarVariosUsuarios_quandoListaMenor1000Ids() {
        var listaIds = gerarLista(false);
        when(usuarioRepository.findByIdIn(anyList())).thenReturn(gerarListaUsuario(listaIds));
        autenticacaoService.logout(listaIds);
        verify(usuarioRepository, times(1)).findByIdIn(anyList());
    }

    @Test
    public void logout_deveDeslogarVariosUsuarios_quandoListaMaior1000Ids() {
        var listaIds = gerarLista(true);
        when(usuarioRepository.findByIdIn(anyList())).thenReturn(gerarListaUsuario(listaIds));
        autenticacaoService.logout(listaIds);
        verify(usuarioRepository, times(3)).findByIdIn(anyList());
    }

    @Test
    public void logout_deveNaoFazerNada_quandoListaNulaOuVazia() {
        autenticacaoService.logout(List.of());
        verify(usuarioRepository, times(0)).findByIdIn(anyList());
    }

    private static List<Integer> gerarLista(boolean maiorQueMil) {
        return IntStream.rangeClosed(0, maiorQueMil ? LISTA_MAIOR_1000 : LISTA_MENOR_1000).boxed().collect(Collectors.toList());
    }

    private static List<Usuario> gerarListaUsuario(List<Integer> ids) {
        return ids.stream().map(id -> new Usuario(id, "TESTE@TESTE.COM")).collect(Collectors.toList());
    }
}
