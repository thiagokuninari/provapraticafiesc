package br.com.xbrain.autenticacao.modules.geradorlead.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.geradorlead.dto.AgenteAutorizadoGeradorLeadDto;
import br.com.xbrain.autenticacao.modules.geradorlead.dto.SituacaoAlteracaoGeradorLeadsDto;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.geradorlead.service.GeradorLeadUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;
    @Captor
    private ArgumentCaptor<UsuarioHistorico> usuarioHistoricoCaptor;

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

    @Test
    public void alterarSituacaoGeradorLeads_deveLancarException_quandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findComplete(1111)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.alterarSituacaoGeradorLeads(umSituacaoAlteracaoMqDto()));
    }

    @Test
    public void alterarSituacaoGeradorLeads_deveLancarException_quandoUsuarioNaoGeradorLeads() {
        when(usuarioRepository.findComplete(1111)).thenReturn(umUsuario(CodigoCargo.SUPERVISOR_OPERACAO, ESituacao.A));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.alterarSituacaoGeradorLeads(umSituacaoAlteracaoMqDto()))
            .withMessage("Usuário não Gerador de Leads.");
    }

    @Test
    public void alterarSituacaoGeradorLeads_deveInativarUsuarioEGerarHistorico_quandoMensagemParaInativar() {
        when(usuarioRepository.findComplete(1111)).thenReturn(umUsuario(CodigoCargo.GERADOR_LEADS, ESituacao.A));

        service.alterarSituacaoGeradorLeads(umSituacaoAlteracaoMqDto());

        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue())
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(1111, ESituacao.I);

        verify(usuarioHistoricoService, times(1)).save(usuarioHistoricoCaptor.capture());
        assertThat(usuarioHistoricoCaptor.getValue())
            .extracting("usuario.id", "usuarioAlteracao.id", "situacao", "dataCadastro", "observacao")
            .containsExactlyInAnyOrder(1111, 10, ESituacao.I, LocalDateTime.of(2020, 5, 11, 11, 11, 11), "TESTE");
    }

    @Test
    public void alterarSituacaoGeradorLeads_deveAtivarUsuarioEGerarHistorico_quandoMensagemParaAtivar() {
        var ativacaoMqDto = umSituacaoAlteracaoMqDto();
        ativacaoMqDto.setSituacaoAlterada(ESituacao.A);
        when(usuarioRepository.findComplete(1111)).thenReturn(umUsuario(CodigoCargo.GERADOR_LEADS, ESituacao.I));

        service.alterarSituacaoGeradorLeads(ativacaoMqDto);

        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue())
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(1111, ESituacao.A);

        verify(usuarioHistoricoService, times(1)).save(usuarioHistoricoCaptor.capture());
        assertThat(usuarioHistoricoCaptor.getValue())
            .extracting("usuario.id", "usuarioAlteracao.id", "situacao", "dataCadastro", "observacao")
            .containsExactlyInAnyOrder(1111, 10, ESituacao.A, LocalDateTime.of(2020, 5, 11, 11, 11, 11), "TESTE");

    }

    private AgenteAutorizadoGeradorLeadDto umAgenteAutorizadoGeradorLeadDto() {
        return AgenteAutorizadoGeradorLeadDto.builder()
            .colaboradoresVendasIds(Lists.newArrayList(100, 102))
            .usuarioProprietarioId(10)
            .usuarioCadastroId(999)
            .id(30)
            .build();
    }

    private SituacaoAlteracaoGeradorLeadsDto umSituacaoAlteracaoMqDto() {
        return SituacaoAlteracaoGeradorLeadsDto.builder()
            .usuarioId(1111)
            .usuarioAlteracaoId(10)
            .situacaoAlterada(ESituacao.I)
            .dataAlteracao(LocalDateTime.of(2020, 5, 11, 11, 11, 11))
            .observacao("TESTE")
            .build();
    }

    private Optional<Usuario> umUsuario(CodigoCargo codigoCargo, ESituacao situacao) {
        return Optional.of(
            Usuario.builder()
                .id(1111)
                .situacao(situacao)
                .cargo(
                    Cargo.builder()
                        .codigo(codigoCargo)
                        .build())
                .build());
    }
}
