package br.com.xbrain.autenticacao.modules.agenteautorizado.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class PermissaoTecnicoIndicadorServiceTest {

    private static final List<Integer> PERMISSOES_TECNICO_INDICADOR = List.of(22122, 22257, 22127, 253);

    @InjectMocks
    private PermissaoTecnicoIndicadorService service;
    @Mock
    private PermissaoEspecialService permissaoEspecialService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CargoRepository cargoRepository;
    @Captor
    private ArgumentCaptor<List<PermissaoEspecial>> permissaoEspecialCaptor;

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveAdicionarPermissao_quandoSolicitadoECargoNaoForSuperior() {
        var cargos = List.of(new Cargo(3005), new Cargo(3006));
        var usuario = umUsuario(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_VAREJO);

        when(cargoRepository.findByCodigoIn(anyList())).thenReturn(cargos);
        when(usuarioRepository.findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R))
            .thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSOES_TECNICO_INDICADOR))
            .thenReturn(false);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(cargoRepository).findByCodigoIn(anyList());
        verify(usuarioRepository).findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R);
        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(3, PERMISSOES_TECNICO_INDICADOR);
        verify(permissaoEspecialService).save(permissaoEspecialCaptor.capture());

        var permissoesSalvas = permissaoEspecialCaptor.getValue();

        assertThat(permissoesSalvas)
            .extracting(permissaoEspecial -> permissaoEspecial.getUsuario().getId(),
                permissaoEspecial -> permissaoEspecial.getFuncionalidade().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuarioCadastro().getId())
            .containsExactly(
                tuple(3, 22122, 1),
                tuple(3, 22257, 1));
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveAdicionarPermissao_quandoSolicitadoECargoForSuperior() {
        var cargos = List.of(new Cargo(3005), new Cargo(3006));
        var usuario = umUsuario(CodigoCargo.AGENTE_AUTORIZADO_SOCIO);

        when(cargoRepository.findByCodigoIn(anyList())).thenReturn(cargos);
        when(usuarioRepository.findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R))
            .thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSOES_TECNICO_INDICADOR))
            .thenReturn(false);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(cargoRepository).findByCodigoIn(anyList());
        verify(usuarioRepository).findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R);
        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(3, PERMISSOES_TECNICO_INDICADOR);
        verify(permissaoEspecialService).save(permissaoEspecialCaptor.capture());

        var permissoesSalvas = permissaoEspecialCaptor.getValue();

        assertThat(permissoesSalvas)
            .extracting(permissaoEspecial -> permissaoEspecial.getUsuario().getId(),
                permissaoEspecial -> permissaoEspecial.getFuncionalidade().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuarioCadastro().getId())
            .containsExactly(
                tuple(3, 22122, 1),
                tuple(3, 22257, 1),
                tuple(3, 22127, 1),
                tuple(3, 253, 1));
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_naoDeveAdicionarPermissao_quandoUsuarioPossuirPermissao() {
        var cargos = List.of(new Cargo(3005), new Cargo(3006));
        var usuario = Usuario.builder().id(3).cargo(new Cargo(3006)).situacao(ESituacao.I).build();

        when(cargoRepository.findByCodigoIn(anyList())).thenReturn(cargos);
        when(usuarioRepository.findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R))
            .thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSOES_TECNICO_INDICADOR))
            .thenReturn(true);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(cargoRepository).findByCodigoIn(anyList());
        verify(usuarioRepository).findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R);
        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(3, PERMISSOES_TECNICO_INDICADOR);
        verifyNoMoreInteractions(permissaoEspecialService);
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveRemoverPermissao_quandoUsuarioPossuirPermissao() {
        var cargos = List.of(new Cargo(3005), new Cargo(3006));
        var usuario = Usuario.builder().id(3).cargo(new Cargo(3006)).situacao(ESituacao.I).build();

        when(cargoRepository.findByCodigoIn(anyList())).thenReturn(cargos);
        when(usuarioRepository.findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R))
            .thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSOES_TECNICO_INDICADOR))
            .thenReturn(true);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.F);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(cargoRepository).findByCodigoIn(anyList());
        verify(usuarioRepository).findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R);
        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(3, PERMISSOES_TECNICO_INDICADOR);
        verify(permissaoEspecialService).deletarPermissoesEspeciaisBy(PERMISSOES_TECNICO_INDICADOR, List.of(3));
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_naoDeveRemoverPermissao_quandoUsuarioNaoPossuirPermissao() {
        var cargos = List.of(new Cargo(3005), new Cargo(3006));
        var usuario = Usuario.builder().id(3).cargo(new Cargo(3006)).situacao(ESituacao.I).build();

        when(cargoRepository.findByCodigoIn(anyList())).thenReturn(cargos);
        when(usuarioRepository.findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R))
            .thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSOES_TECNICO_INDICADOR))
            .thenReturn(false);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.F);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(cargoRepository).findByCodigoIn(anyList());
        verify(usuarioRepository).findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R);
        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(3, PERMISSOES_TECNICO_INDICADOR);
        verifyNoMoreInteractions(permissaoEspecialService);
    }

    @Test
    public void buscarUsuariosTabulacaoTecnicoIndicador_deveRetornarUsuarios_quandoExistirem() {
        var cargos = List.of(new Cargo(3005), new Cargo(3006));
        var usuario1 = Usuario.builder().id(2).cargo(new Cargo(3005)).situacao(ESituacao.A).build();
        var usuario2 = Usuario.builder().id(3).cargo(new Cargo(3006)).situacao(ESituacao.I).build();

        when(cargoRepository.findByCodigoIn(anyList())).thenReturn(cargos);
        when(usuarioRepository.findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R))
            .thenReturn(List.of(usuario1, usuario2));

        assertThat(service.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3)))
            .isNotEmpty();
    }

    @Test
    public void adicionarPermissaoTecnicoIndicadorParaUsuarioNovo_deveAdicionarPermissao_seUsuarioForNovoCadastro() {
        var request = UsuarioMqRequest.builder()
            .tecnicoIndicador(true)
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS)
            .build();
        var usuario = new UsuarioDto(4);
        usuario.setUsuarioCadastroId(2);

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, false);

        verify(permissaoEspecialService).save(permissaoEspecialCaptor.capture());

        var permissoesSalvas = permissaoEspecialCaptor.getValue();

        assertThat(permissoesSalvas)
            .extracting(permissaoEspecial -> permissaoEspecial.getUsuario().getId(),
                permissaoEspecial -> permissaoEspecial.getFuncionalidade().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuarioCadastro().getId())
            .containsExactly(
                tuple(4, 22122, 2),
                tuple(4, 22257, 2),
                tuple(4, 253, 2));
    }

    @Test
    public void adicionarPermissaoTecnicoIndicadorParaUsuarioNovo_naoDeveAdicionarPermissao_seAaNaoForTecnicoIndicador() {
        var request = UsuarioMqRequest.builder()
            .tecnicoIndicador(false)
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS)
            .build();
        var usuario = new UsuarioDto(4);

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, false);

        verify(permissaoEspecialService, never()).save(anyList());
    }

    @Test
    public void adicionarPermissaoTecnicoIndicadorParaUsuarioNovo_naoDeveAdicionarPermissao_seUsuarioNaoPossuirCargoValido() {
        var request = UsuarioMqRequest.builder()
            .id(4)
            .tecnicoIndicador(true)
            .cargo(CodigoCargo.BACKOFFICE_SUPERVISOR)
            .build();
        var usuario = new UsuarioDto(4);

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, false);

        verify(permissaoEspecialService, never()).save(anyList());
    }

    @Test
    public void adicionarPermissaoTecnicoIndicadorParaUsuarioNovo_naoDeveAdicionarPermissao_seUsuarioJaPossuirPermissao() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSOES_TECNICO_INDICADOR)).thenReturn(true);

        var request = UsuarioMqRequest.builder()
            .id(4)
            .tecnicoIndicador(true)
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS)
            .build();
        var usuario = new UsuarioDto(4);

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, false);

        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(4, PERMISSOES_TECNICO_INDICADOR);
        verifyNoMoreInteractions(permissaoEspecialService);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoTecnicoIndicadorParaUsuarioNovo_deveAdicionarPermissaoDesbloquearIndicacaoExterna_quandoForCargoSuperior() {
        var request = UsuarioMqRequest.builder()
            .tecnicoIndicador(true)
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR)
            .build();
        var usuario = new UsuarioDto(4);
        usuario.setUsuarioCadastroId(4444);

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, false);

        verify(permissaoEspecialService).save(permissaoEspecialCaptor.capture());

        var permissoesEspeciaisSalvas = permissaoEspecialCaptor.getValue();

        assertThat(permissoesEspeciaisSalvas)
            .extracting(permissaoEspecial -> permissaoEspecial.getUsuario().getId(),
                permissaoEspecial -> permissaoEspecial.getFuncionalidade().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuarioCadastro().getId())
            .containsExactly(tuple(4, 22122, 4444),
                tuple(4, 22257, 4444),
                tuple(4, 22127, 4444));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoTecnicoIndicadorParaUsuarioNovo_naoDeveAdicionarPermissaoDesbloquearIndicacaoExterna_quandoNaoForCargoSuperior() {
        var request = UsuarioMqRequest.builder()
            .tecnicoIndicador(true)
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_D2D)
            .build();
        var usuario = new UsuarioDto(4);
        usuario.setUsuarioCadastroId(4444);

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, false);

        verify(permissaoEspecialService).save(permissaoEspecialCaptor.capture());

        var permissoesEspeciaisSalvas = permissaoEspecialCaptor.getValue();

        assertThat(permissoesEspeciaisSalvas)
            .extracting(permissaoEspecial -> permissaoEspecial.getUsuario().getId(),
                permissaoEspecial -> permissaoEspecial.getFuncionalidade().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuarioCadastro().getId())
            .containsExactly(
                tuple(4, 22122, 4444),
                tuple(4, 22257, 4444),
                tuple(4, 253, 4444));
    }

    @Test
    public void removerPermissaoTecnicoIndicadorDoUsuario_deveRemoverPermissao_seSituacaoForRealocado() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSOES_TECNICO_INDICADOR)).thenReturn(true);

        var usuario = UsuarioDto.builder().id(4).situacao(ESituacao.R).build();

        service.removerPermissaoTecnicoIndicadorDoUsuario(usuario);

        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(4, PERMISSOES_TECNICO_INDICADOR);
        verify(permissaoEspecialService).deletarPermissoesEspeciaisBy(PERMISSOES_TECNICO_INDICADOR, List.of(4));
    }

    @Test
    public void removerPermissaoTecnicoIndicadorDoUsuario_deveRemoverPermissao_seCargoNaoForValido() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSOES_TECNICO_INDICADOR)).thenReturn(true);

        var usuario = UsuarioDto.builder()
            .id(4)
            .cargoCodigo(CodigoCargo.SUPERVISOR_OPERACAO)
            .build();

        service.removerPermissaoTecnicoIndicadorDoUsuario(usuario);

        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(4, PERMISSOES_TECNICO_INDICADOR);
        verify(permissaoEspecialService).deletarPermissoesEspeciaisBy(PERMISSOES_TECNICO_INDICADOR, List.of(4));
    }

    @Test
    public void removerPermissaoTecnicoIndicadorDoUsuario_naoDeveRemoverPermissao_seForNovoCadastro() {
        var usuario = UsuarioDto.builder()
            .id(null)
            .situacao(ESituacao.A)
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_HIBRIDO)
            .build();

        service.removerPermissaoTecnicoIndicadorDoUsuario(usuario);

        verifyZeroInteractions(permissaoEspecialService);
    }

    @Test
    public void removerPermissaoTecnicoIndicadorDoUsuario_naoDeveRemoverPermissao_seNaoPossuirPermissao() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSOES_TECNICO_INDICADOR)).thenReturn(false);

        var usuario = UsuarioDto.builder()
            .id(4)
            .situacao(ESituacao.R)
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_GERENTE)
            .build();

        service.removerPermissaoTecnicoIndicadorDoUsuario(usuario);

        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(4, PERMISSOES_TECNICO_INDICADOR);
        verifyNoMoreInteractions(permissaoEspecialService);
    }

    @Test
    public void removerPermissaoTecnicoIndicadorDoUsuario_naoDeveRemoverPermissao_sePossuirCargoValido() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSOES_TECNICO_INDICADOR)).thenReturn(true);

        var usuario = UsuarioDto.builder()
            .id(4)
            .situacao(ESituacao.A)
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_GERENTE)
            .build();

        service.removerPermissaoTecnicoIndicadorDoUsuario(usuario);

        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(4, PERMISSOES_TECNICO_INDICADOR);
        verifyNoMoreInteractions(permissaoEspecialService);
    }
}
