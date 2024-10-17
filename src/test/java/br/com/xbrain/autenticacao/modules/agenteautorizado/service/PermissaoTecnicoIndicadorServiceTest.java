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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class PermissaoTecnicoIndicadorServiceTest {

    private static final Integer PERMISSAO_TECNICO_INDICADOR = 253;

    @InjectMocks
    private PermissaoTecnicoIndicadorService service;
    @Mock
    private PermissaoEspecialService permissaoEspecialService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CargoRepository cargoRepository;

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveAdicionarPermissao_quandoSolicitado() {
        var cargos = List.of(new Cargo(3005), new Cargo(3006));
        var usuario = Usuario.builder().id(3).cargo(new Cargo(3006)).situacao(ESituacao.I).build();

        when(cargoRepository.findByCodigoIn(anyList())).thenReturn(cargos);
        when(usuarioRepository.findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R))
            .thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSAO_TECNICO_INDICADOR))
            .thenReturn(false);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, times(1)).save(anyList());
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_naoDeveAdicionarPermissao_quandoUsuarioPossuirPermissao() {
        var cargos = List.of(new Cargo(3005), new Cargo(3006));
        var usuario = Usuario.builder().id(3).cargo(new Cargo(3006)).situacao(ESituacao.I).build();

        when(cargoRepository.findByCodigoIn(anyList())).thenReturn(cargos);
        when(usuarioRepository.findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R))
            .thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSAO_TECNICO_INDICADOR))
            .thenReturn(true);

        var permissoes = List.of(PermissaoEspecial.of(3, PERMISSAO_TECNICO_INDICADOR, 1));

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, never()).save(eq(permissoes));
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveRemoverPermissao_quandoUsuarioPossuirPermissao() {
        var cargos = List.of(new Cargo(3005), new Cargo(3006));
        var usuario = Usuario.builder().id(3).cargo(new Cargo(3006)).situacao(ESituacao.I).build();

        when(cargoRepository.findByCodigoIn(anyList())).thenReturn(cargos);
        when(usuarioRepository.findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R))
            .thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSAO_TECNICO_INDICADOR))
            .thenReturn(true);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.F);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, times(1))
            .deletarPermissoesEspeciaisBy(eq(List.of(PERMISSAO_TECNICO_INDICADOR)), eq(List.of(3)));
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_naoDeveRemoverPermissao_quandoUsuarioNaoPossuirPermissao() {
        var cargos = List.of(new Cargo(3005), new Cargo(3006));
        var usuario = Usuario.builder().id(3).cargo(new Cargo(3006)).situacao(ESituacao.I).build();

        when(cargoRepository.findByCodigoIn(anyList())).thenReturn(cargos);
        when(usuarioRepository.findByIdInAndCargoInAndSituacaoNot(List.of(1, 2, 3), cargos, ESituacao.R))
            .thenReturn(List.of(usuario));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSAO_TECNICO_INDICADOR))
            .thenReturn(false);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.F);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, never())
            .deletarPermissoesEspeciaisBy(eq(List.of(PERMISSAO_TECNICO_INDICADOR)), eq(List.of(3)));
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

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, false);

        verify(permissaoEspecialService, times(1)).save(anyList());
    }

    @Test
    public void adicionarPermissaoTecnicoIndicadorParaUsuarioNovo_deveAdicionarPermissao_seUsuarioRemanejadoForTrue() {
        var request = UsuarioMqRequest.builder()
            .tecnicoIndicador(true)
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS)
            .build();
        var usuario = new UsuarioDto(4);

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, true);

        verify(permissaoEspecialService, times(1)).save(anyList());
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
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_ASSISTENTE)
            .build();
        var usuario = new UsuarioDto(4);

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, false);

        verify(permissaoEspecialService, never()).save(anyList());
    }

    @Test
    public void adicionarPermissaoTecnicoIndicadorParaUsuarioNovo_naoDeveAdicionarPermissao_seUsuarioJaPossuirPermissao() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSAO_TECNICO_INDICADOR)).thenReturn(true);

        var request = UsuarioMqRequest.builder()
            .id(4)
            .tecnicoIndicador(true)
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS)
            .build();
        var usuario = new UsuarioDto(4);

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, false);

        verify(permissaoEspecialService, never()).save(anyList());
        verify(permissaoEspecialService, times(1)).hasPermissaoEspecialAtiva(4, PERMISSAO_TECNICO_INDICADOR);
    }

    @Test
    public void adicionarPermissaoTecnicoIndicadorParaUsuarioNovo_naoDeveAdicionarPermissao_seUsuarioNaoPossuirPermissao() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSAO_TECNICO_INDICADOR)).thenReturn(false);

        var request = UsuarioMqRequest.builder()
            .id(4)
            .tecnicoIndicador(true)
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS)
            .build();
        var usuario = new UsuarioDto(4);

        service.adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuario, request, false);

        verify(permissaoEspecialService).save(anyList());
        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(4, PERMISSAO_TECNICO_INDICADOR);
    }

    @Test
    public void removerPermissaoTecnicoIndicadorDoUsuario_deveRemoverPermissao_seSituacaoForRealocado() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSAO_TECNICO_INDICADOR)).thenReturn(true);

        var usuario = UsuarioDto.builder().id(4).situacao(ESituacao.R).build();

        service.removerPermissaoTecnicoIndicadorDoUsuario(usuario);

        verify(permissaoEspecialService, times(1))
            .deletarPermissoesEspeciaisBy(eq(List.of(PERMISSAO_TECNICO_INDICADOR)), eq(List.of(4)));
        verify(permissaoEspecialService, times(1))
            .hasPermissaoEspecialAtiva(eq(4), eq(PERMISSAO_TECNICO_INDICADOR));
    }

    @Test
    public void removerPermissaoTecnicoIndicadorDoUsuario_deveRemoverPermissao_seCargoNaoForValido() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSAO_TECNICO_INDICADOR)).thenReturn(true);

        var usuario = UsuarioDto.builder()
            .id(4)
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_ASSISTENTE)
            .build();

        service.removerPermissaoTecnicoIndicadorDoUsuario(usuario);

        verify(permissaoEspecialService, times(1))
            .deletarPermissoesEspeciaisBy(eq(List.of(PERMISSAO_TECNICO_INDICADOR)), eq(List.of(4)));
    }

    @Test
    public void removerPermissaoTecnicoIndicadorDoUsuario_naoDeveRemoverPermissao_seForNovoCadastro() {
        var usuario = UsuarioDto.builder()
            .id(null)
            .situacao(ESituacao.A)
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_HIBRIDO)
            .build();

        service.removerPermissaoTecnicoIndicadorDoUsuario(usuario);

        verify(permissaoEspecialService, never())
            .deletarPermissoesEspeciaisBy(eq(List.of(PERMISSAO_TECNICO_INDICADOR)), eq(List.of(4)));
    }

    @Test
    public void removerPermissaoTecnicoIndicadorDoUsuario_naoDeveRemoverPermissao_seNaoPossuirPermissao() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSAO_TECNICO_INDICADOR)).thenReturn(false);

        var usuario = UsuarioDto.builder()
            .id(4)
            .situacao(ESituacao.R)
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_GERENTE)
            .build();

        service.removerPermissaoTecnicoIndicadorDoUsuario(usuario);

        verify(permissaoEspecialService, never())
            .deletarPermissoesEspeciaisBy(eq(List.of(PERMISSAO_TECNICO_INDICADOR)), eq(List.of(4)));
    }

    @Test
    public void removerPermissaoTecnicoIndicadorDoUsuario_naoDeveRemoverPermissao_sePossuirCargoValido() {
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(4, PERMISSAO_TECNICO_INDICADOR)).thenReturn(true);

        var usuario = UsuarioDto.builder()
            .id(4)
            .situacao(ESituacao.A)
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_GERENTE)
            .build();

        service.removerPermissaoTecnicoIndicadorDoUsuario(usuario);

        verify(permissaoEspecialService, never())
            .deletarPermissoesEspeciaisBy(eq(List.of(PERMISSAO_TECNICO_INDICADOR)), eq(List.of(4)));
    }
}
