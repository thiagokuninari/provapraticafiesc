package br.com.xbrain.autenticacao.modules.geradorlead.service;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.geradorlead.dto.AgenteAutorizadoGeradorLeadDto;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.geradorlead.service.GeradorLeadUtil.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GeradorLeadServiceTest {

    @InjectMocks
    private GeradorLeadService service;

    @Mock
    private PermissaoEspecialRepository permissaoEspecialRepository;
    @Mock
    private UsuarioHistoricoService usuarioHistoricoService;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void atualizarPermissaoGeradorLead_deveSalvarPermissoesEspeciais_quandoUsuariosNaoPossuiremAsPermissoes() {
        var aaGeradorLead = umAgenteAutorizadoGeradorLeadDto();
        aaGeradorLead.setGeradorLead(Eboolean.V);

        service.atualizarPermissaoGeradorLead(aaGeradorLead);

        verify(permissaoEspecialRepository, times(0)).deletarPermissaoEspecialBy(anyList(), anyList());
        verify(permissaoEspecialRepository, times(1)).save(anyList());
        verify(usuarioHistoricoService, times(1)).save(anyList());
    }

    @Test
    public void atualizarPermissaoGeradorLead_naoDeveDuplicarPermissoes_quandoUsuariosJaPossuiremAsPermissoes() {
        var aaGeradorLead = umAgenteAutorizadoGeradorLeadDto();
        aaGeradorLead.setGeradorLead(Eboolean.V);

        when(permissaoEspecialRepository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(anyInt(), anyInt()))
            .thenReturn(Optional.of(new PermissaoEspecial()));

        service.atualizarPermissaoGeradorLead(aaGeradorLead);

        verify(permissaoEspecialRepository, times(0)).deletarPermissaoEspecialBy(anyList(), anyList());
        verify(permissaoEspecialRepository, times(0)).save(anyList());
        verify(usuarioHistoricoService, times(0)).save(anyList());
    }

    @Test
    public void atualizarPermissaoGeradorLead_deveRemoverPermissaoGeradorLead_quandoAaNaoForGeradorDeLead() {
        var aaNaoGeradorLead = umAgenteAutorizadoGeradorLeadDto();
        aaNaoGeradorLead.setGeradorLead(Eboolean.F);
        when(usuarioRepository.exists(anyInt())).thenReturn(true);

        service.atualizarPermissaoGeradorLead(aaNaoGeradorLead);

        verify(permissaoEspecialRepository, times(1))
            .deletarPermissaoEspecialBy(
                FUNCIONALIDADES_GERADOR_LEADS_PARA_AA,
                List.of(100, 102, 10));

        verify(permissaoEspecialRepository, times(0)).save(any(PermissaoEspecial.class));
        verify(usuarioHistoricoService, times(1)).save(anyList());
    }

    private AgenteAutorizadoGeradorLeadDto umAgenteAutorizadoGeradorLeadDto() {
        return AgenteAutorizadoGeradorLeadDto.builder()
            .colaboradoresVendasIds(Lists.newArrayList(100, 102))
            .usuarioProprietarioId(10)
            .usuarioCadastroId(999)
            .id(30)
            .build();
    }
}