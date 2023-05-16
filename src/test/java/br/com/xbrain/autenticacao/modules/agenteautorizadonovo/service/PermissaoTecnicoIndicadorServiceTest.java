package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

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
    private UsuarioService usuarioService;
    @Mock
    private CargoRepository cargoRepository;

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveAdicionarPermissao_quandoSolicitado() {
        when(usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3)))
            .thenReturn(List.of(new Usuario(3)));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSAO_TECNICO_INDICADOR))
            .thenReturn(false);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, times(1)).save(anyList());
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_naoDeveAdicionarPermissao_quandoUsuarioPossuirPermissao() {
        when(usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3)))
            .thenReturn(List.of(new Usuario(3)));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSAO_TECNICO_INDICADOR))
            .thenReturn(true);

        var permissoes = List.of(PermissaoEspecial.of(3, PERMISSAO_TECNICO_INDICADOR, 1));

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.V);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, never()).save(eq(permissoes));
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_deveRemoverPermissao_quandoUsuarioPossuirPermissao() {
        when(usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3)))
            .thenReturn(List.of(new Usuario(3)));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSAO_TECNICO_INDICADOR))
            .thenReturn(true);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.F);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, times(1))
            .deletarPermissoesEspeciaisBy(eq(List.of(PERMISSAO_TECNICO_INDICADOR)), eq(List.of(3)));
    }

    @Test
    public void atualizarPermissaoTecnicoIndicador_naoDeveRemoverPermissao_quandoUsuarioNaoPossuirPermissao() {
        when(usuarioService.buscarUsuariosTabulacaoTecnicoIndicador(List.of(1, 2, 3)))
            .thenReturn(List.of(new Usuario(3)));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(3, PERMISSAO_TECNICO_INDICADOR))
            .thenReturn(false);

        var dto = new PermissaoTecnicoIndicadorDto(1, List.of(1, 2, 3), 1, Eboolean.F);

        service.atualizarPermissaoTecnicoIndicador(dto);

        verify(permissaoEspecialService, never())
            .deletarPermissoesEspeciaisBy(eq(List.of(PERMISSAO_TECNICO_INDICADOR)), eq(List.of(3)));
    }
}
