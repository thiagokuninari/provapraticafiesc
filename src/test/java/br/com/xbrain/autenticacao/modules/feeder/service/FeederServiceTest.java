package br.com.xbrain.autenticacao.modules.feeder.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.feeder.dto.AgenteAutorizadoPermissaoFeederDto;
import br.com.xbrain.autenticacao.modules.feeder.dto.SituacaoAlteracaoUsuarioFeederDto;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
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

import static br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeederServiceTest {

    @InjectMocks
    private FeederService service;

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
    @Captor
    private ArgumentCaptor<List<PermissaoEspecial>> permissaoEspecialCaptor;

    @Test
    public void atualizarPermissaoFeeder_deveSalvarPermissoesEspeciais_quandoUsuariosNaoPossuiremAsPermissoes() {
        var aaComPermissaoFeeder = umAgenteAutorizadoFeederDto();
        aaComPermissaoFeeder.setFeeder(Eboolean.V);
        aaComPermissaoFeeder.setSocioDeOutroAaComPermissaoFeeder(false);

        service.atualizarPermissaoFeeder(aaComPermissaoFeeder);

        verify(permissaoEspecialRepository, times(0)).deletarPermissaoEspecialBy(anyList(), anyList());
        verify(permissaoEspecialRepository, times(1)).save(anyList());
        verify(usuarioHistoricoService, times(1)).save(anyList());
    }

    @Test
    public void atualizarPermissaoFeeder_naoDeveDuplicarPermissoes_quandoUsuariosJaPossuiremAsPermissoes() {
        var aaComPermissaoFeeder = umAgenteAutorizadoFeederDto();
        aaComPermissaoFeeder.setFeeder(Eboolean.V);
        aaComPermissaoFeeder.setSocioDeOutroAaComPermissaoFeeder(true);

        when(permissaoEspecialRepository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(anyInt(), anyInt()))
            .thenReturn(Optional.of(new PermissaoEspecial()));

        service.atualizarPermissaoFeeder(aaComPermissaoFeeder);

        verify(permissaoEspecialRepository, times(0)).deletarPermissaoEspecialBy(anyList(), anyList());
        verify(permissaoEspecialRepository, times(0)).save(anyList());
        verify(usuarioHistoricoService, times(0)).save(anyList());
    }

    @Test
    public void atualizarPermissaoFeeder_deveRemoverPermissaoFeeder_quandoAaTiverPermissaoParaFeeder() {
        var aaSemPermissaoFeeder = umAgenteAutorizadoFeederDto();
        aaSemPermissaoFeeder.setFeeder(Eboolean.F);
        aaSemPermissaoFeeder.setSocioDeOutroAaComPermissaoFeeder(false);
        when(usuarioRepository.exists(anyInt())).thenReturn(true);

        service.atualizarPermissaoFeeder(aaSemPermissaoFeeder);

        verify(permissaoEspecialRepository, times(1))
            .deletarPermissaoEspecialBy(
                FUNCIONALIDADES_FEEDER_PARA_AA,
                List.of(100, 102, 10));

        verify(permissaoEspecialRepository, times(0)).save(any(PermissaoEspecial.class));
        verify(usuarioHistoricoService, times(1)).save(anyList());
    }

    @Test
    public void atualizarPermissaoFeeder_naoDeveRemoverPermissaoDoSocio_quandoSocioTiverOutroAaComPermissaoFeeder() {
        var aaSemPermissaoFeeder = umAgenteAutorizadoFeederDto();
        aaSemPermissaoFeeder.setFeeder(Eboolean.F);
        aaSemPermissaoFeeder.setSocioDeOutroAaComPermissaoFeeder(true);
        when(usuarioRepository.exists(anyInt())).thenReturn(true);

        service.atualizarPermissaoFeeder(aaSemPermissaoFeeder);

        verify(permissaoEspecialRepository, times(1))
            .deletarPermissaoEspecialBy(
                FUNCIONALIDADES_FEEDER_PARA_AA,
                List.of(100, 102));

        verify(permissaoEspecialRepository, times(0)).save(any(PermissaoEspecial.class));
        verify(usuarioHistoricoService, times(1)).save(anyList());
    }

    @Test
    public void alterarSituacaoUsuarioFeeder_deveLancarException_quandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findComplete(1111)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.alterarSituacaoUsuarioFeeder(umSituacaoAlteracaoMqDto()));
    }

    @Test
    public void alterarSituacaoUsuarioFeeder_deveLancarException_quandoUsuarioNaoFeeder() {
        when(usuarioRepository.findComplete(1111)).thenReturn(umUsuario(CodigoCargo.SUPERVISOR_OPERACAO, ESituacao.A));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.alterarSituacaoUsuarioFeeder(umSituacaoAlteracaoMqDto()))
            .withMessage("Usuário não Feeder.");
    }

    @Test
    public void alterarSituacaoUsuarioFeeder_deveInativarUsuarioEGerarHistorico_quandoMensagemParaInativar() {
        when(usuarioRepository.findComplete(1111)).thenReturn(umUsuario(CodigoCargo.IMPORTADOR_CARGAS, ESituacao.A));

        service.alterarSituacaoUsuarioFeeder(umSituacaoAlteracaoMqDto());

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
    public void alterarSituacaoUsuarioFeeder_deveAtivarUsuarioEGerarHistorico_quandoMensagemParaAtivar() {
        var ativacaoMqDto = umSituacaoAlteracaoMqDto();
        ativacaoMqDto.setSituacaoAlterada(ESituacao.A);
        when(usuarioRepository.findComplete(1111)).thenReturn(umUsuario(CodigoCargo.GERADOR_LEADS, ESituacao.I));

        service.alterarSituacaoUsuarioFeeder(ativacaoMqDto);

        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue())
            .extracting("id", "situacao")
            .containsExactlyInAnyOrder(1111, ESituacao.A);

        verify(usuarioHistoricoService, times(1)).save(usuarioHistoricoCaptor.capture());
        assertThat(usuarioHistoricoCaptor.getValue())
            .extracting("usuario.id", "usuarioAlteracao.id", "situacao", "dataCadastro", "observacao")
            .containsExactlyInAnyOrder(1111, 10, ESituacao.A, LocalDateTime.of(2020, 5, 11, 11, 11, 11), "TESTE");
    }

    @Test
    public void adicionarPermissaoFeederParaUsuarioNovo_deveSalvarPermissaoTratarLead_quandoAgenteAutorizadoForFeeder() {
        when(usuarioRepository.findById(1111)).thenReturn(umUsuario(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_D2D, ESituacao.A));
        when(permissaoEspecialRepository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(anyInt(), anyInt()))
            .thenReturn(Optional.empty());

        service.adicionarPermissaoFeederParaUsuarioNovo(umUsuarioDto(), umUsuarioMqRequest());

        verify(permissaoEspecialRepository, times(1)).save(permissaoEspecialCaptor.capture());
        assertThat(permissaoEspecialCaptor.getValue())
            .hasSize(1)
            .extracting("usuario.id", "funcionalidade.id")
            .containsExactlyInAnyOrder(tuple(1111, 3046));
    }

    @Test
    public void adicionarPermissaoFeederParaUsuarioNovo_deveNaoSalvarPermissaoTratarLead_quandoAaNaoForFeeder() {
        var usuarioNovo = umUsuarioMqRequest();
        usuarioNovo.setAgenteAutorizadoFeeder(Eboolean.F);

        service.adicionarPermissaoFeederParaUsuarioNovo(umUsuarioDto(), usuarioNovo);

        verify(permissaoEspecialRepository, never()).save(anyList());
    }

    private AgenteAutorizadoPermissaoFeederDto umAgenteAutorizadoFeederDto() {
        return AgenteAutorizadoPermissaoFeederDto.builder()
            .colaboradoresVendasIds(Lists.newArrayList(100, 102))
            .usuarioProprietarioId(10)
            .usuarioCadastroId(999)
            .agenteAutorizadoId(30)
            .build();
    }

    private SituacaoAlteracaoUsuarioFeederDto umSituacaoAlteracaoMqDto() {
        return SituacaoAlteracaoUsuarioFeederDto.builder()
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

    private UsuarioDto umUsuarioDto() {
        return UsuarioDto.builder()
            .id(1111)
            .agenteAutorizadoId(111)
            .usuarioCadastroId(2222)
            .build();
    }

    private UsuarioMqRequest umUsuarioMqRequest() {
        return UsuarioMqRequest.builder()
            .agenteAutorizadoFeeder(Eboolean.V)
            .agenteAutorizadoId(111)
            .build();
    }
}
