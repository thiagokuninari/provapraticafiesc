package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.anyInt;
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

    @Test
    public void findById_deveRetornarUsuarioResponse_quandoSolicitado() {
        when(usuarioRepository.findById(1))
            .thenReturn(Optional.of(Usuario.builder()
                .id(1)
                .nome("RENATO")
                .build()));

        assertThat(usuarioService.findById(1))
            .extracting("id", "nome")
            .containsExactly(1, "RENATO");
    }

    @Test
    public void findById_deveRetornarException_quandoNaoEncontrarUsuarioById() {
        when(usuarioRepository.findById(1))
            .thenReturn(Optional.empty());

        assertThatCode(() -> usuarioService.findById(1))
            .hasMessage("Usuário não encontrado.")
            .isInstanceOf(ValidacaoException.class);
    }
}
