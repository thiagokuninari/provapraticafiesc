package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService = new UsuarioService();
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void getSubclustersUsuario_deveConverterORetornoEmSelectResponse_conformeListaDeSubclusters() {
        when(usuarioRepository.getSubclustersUsuario(anyInt()))
                .thenReturn(List.of(
                        SubCluster.of(1, "TESTE1"),
                        SubCluster.of(2, "TESTE2")));

        assertThat(usuarioService.getSubclusterUsuario(1))
                .extracting("value", "label")
                .containsExactly(
                        tuple(1, "TESTE1"),
                        tuple(2, "TESTE2"));
    }
}
