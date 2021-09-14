package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import br.com.xbrain.autenticacao.modules.comum.repository.UsuarioParaDeslogarRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.MotivoInativacaoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.helper.DeslogarUsuarioPorExcessoDeUsoHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
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
    @MockBean
    private UsuarioRepository usuarioRepository;
    @MockBean
    private MotivoInativacaoRepository motivoInativacaoRepository;

    @Before
    public void setup() {
        when(motivoInativacaoRepository.findByCodigo(any())).thenReturn(Optional.of(MotivoInativacao
            .builder()
            .id(7)
            .codigo(CodigoMotivoInativacao.INATIVADO_SIMULACOES)
            .descricao("INATIVADO POR REALIZAR MUITAS SIMULAÇÕES")
            .situacao(ESituacao.A)
            .build()));
    }

    @Test
    public void deslogarUsuariosInativados_deveDeslogarsInativarAtualizarParaDeslogado_quandoExistirNaTabelaComUsuarioAtivo() {
        when(repository.findAllByDeslogado(any())).thenReturn(umaListaDeUsuariosParaDeslogar());
        when(usuarioRepository.findById(anyInt())).thenReturn(umUsuarioComSituacao(ESituacao.A));

        service.deslogarUsuariosInativados();

        verify(autenticacaoService, times(4))
            .logout(intThat(usuarioId -> umaListaDeUsuariosParaDeslogar()
                .stream()
                .map(UsuarioParaDeslogar::getUsuarioId)
                .anyMatch(integer -> Objects.equals(integer, usuarioId))));

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(repository, times(4)).save(any(UsuarioParaDeslogar.class));
    }

    @Test
    public void deslogarUsuariosInativados_deveDeslogarAtualizarParaDeslogado_quandoExistirNaTabelaComUsuarioInativo() {
        when(repository.findAllByDeslogado(any())).thenReturn(umaListaDeUsuariosParaDeslogar());
        when(usuarioRepository.findById(anyInt())).thenReturn(umUsuarioComSituacao(ESituacao.I));

        service.deslogarUsuariosInativados();

        verify(autenticacaoService, times(4))
            .logout(intThat(usuarioId -> umaListaDeUsuariosParaDeslogar()
                .stream()
                .map(UsuarioParaDeslogar::getUsuarioId)
                .anyMatch(integer -> Objects.equals(integer, usuarioId))));

        verify(usuarioRepository, times(0)).save(any(Usuario.class));
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
        when(repository.findAll()).thenReturn(umaListaDeUsuariosParaDeslogar());

        service.deslogarUsuariosInativados();

        verify(autenticacaoService, times(0)).logout(anyInt());
        verify(repository, times(0)).save(any(UsuarioParaDeslogar.class));
    }

    @Test
    public void validarUsuarioBloqueadoPorExcessoDeUso_deveRetornarBloqueioTrue_seExistirUsuarioBloqueado() {
        when(repository.findFirstByUsuarioIdOrderByDataCadastroDesc(123)).thenReturn(umUsuarioParaDeslogarBloqueado());

        assertThat(service.validarUsuarioBloqueadoPorExcessoDeUso(123))
            .extracting("usuarioId", "bloqueado")
            .containsExactly(123, true);

        verify(repository, times(1)).findFirstByUsuarioIdOrderByDataCadastroDesc(eq(123));
    }

    @Test
    public void validarUsuarioBloqueadoPorExcessoDeUso_deveRetornarFalse_seUsuarioNaoBloqueado() {
        when(repository.findFirstByUsuarioIdOrderByDataCadastroDesc(123)).thenReturn(umUsuarioParaDeslogarNaoBloqueado());

        assertThat(service.validarUsuarioBloqueadoPorExcessoDeUso(123))
            .extracting("usuarioId", "bloqueado")
            .containsExactly(123, false);

        verify(repository, times(1)).findFirstByUsuarioIdOrderByDataCadastroDesc(eq(123));
    }

    @Test
    public void validarUsuarioBloqueadoPorExcessoDeUso_deveRetornarFalse_seNaoEncontrarUsuarioNaLista() {
        when(repository.findFirstByUsuarioIdOrderByDataCadastroDesc(123)).thenReturn(Optional.empty());

        assertThat(service.validarUsuarioBloqueadoPorExcessoDeUso(123))
            .extracting("usuarioId", "bloqueado")
            .containsExactly(123, false);

        verify(repository, times(1)).findFirstByUsuarioIdOrderByDataCadastroDesc(eq(123));
    }

    @Test
    public void atualizarSituacaoUsuarioBloqueado_deveSalvarUsuario_seUsuarioPresenteListaDeBloqueados() {
        when(repository.findFirstByUsuarioIdOrderByDataCadastroDesc(123))
            .thenReturn(umUsuarioParaDeslogarBloqueado());

        service.atualizarSituacaoUsuarioBloqueado(123);

        verify(repository, times(1)).findFirstByUsuarioIdOrderByDataCadastroDesc(123);
        verify(repository, times(1)).save(eq(umUsuarioParaDeslogarSituacaoAlterada()));
    }

    @Test
    public void atualizarSituacaoUsuarioBloqueado_naoDeveSalvarUsuario_seUsuarioNaoEncontrado() {
        when(repository.findFirstByUsuarioIdOrderByDataCadastroDesc(123))
            .thenReturn(Optional.empty());

        service.atualizarSituacaoUsuarioBloqueado(123);

        verify(repository, times(1)).findFirstByUsuarioIdOrderByDataCadastroDesc(123);
        verify(repository, never()).save(any(UsuarioParaDeslogar.class));
    }
}
