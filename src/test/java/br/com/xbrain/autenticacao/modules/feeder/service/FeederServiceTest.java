package br.com.xbrain.autenticacao.modules.feeder.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeeder;
import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.feeder.dto.AgenteAutorizadoPermissaoFeederDto;
import br.com.xbrain.autenticacao.modules.feeder.dto.SituacaoAlteracaoUsuarioFeederDto;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.EMPRESARIAL;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.RESIDENCIAL;
import static br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.umDepartamento;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.PermissoesHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
    @Mock
    private UsuarioService usuarioService;
    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;
    @Captor
    private ArgumentCaptor<UsuarioHistorico> usuarioHistoricoCaptor;
    @Mock
    private DataHoraAtual dataHoraAtual;

    @Test
    public void removerPermissoesEspeciais_deveRemoverPermissoesQuandoHouver() {
        service.removerPermissoesEspeciais(List.of(1), FUNCIONALIDADES_FEEDER_PARA_AA);
        verify(permissaoEspecialRepository, times(1))
            .deletarPermissaoEspecialBy(eq(List.of(15000, 15005, 15012, 3046)), eq(List.of(1)));
    }

    @Test
    public void atualizarPermissaoFeeder_deveSalvarPermissoesEspeciaisConformeOCargo_quandoUsuariosNaoPossuiremAsPermissoes() {
        var aaComPermissaoFeeder = umAgenteAutorizadoFeederDto();
        aaComPermissaoFeeder.setFeeder(ETipoFeeder.RESIDENCIAL);
        aaComPermissaoFeeder.setSocioDeOutroAaComPermissaoFeeder(false);

        when(usuarioRepository.findComplete(102)).thenReturn(
            umUsuario(CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_D2D, ESituacao.A, 102));
        when(usuarioRepository.findComplete(100)).thenReturn(
            umUsuario(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_D2D, ESituacao.A, 100));

        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(102), eq(999),
            eq(List.of(20018, 20101, 15000, 15005, 15012, 3046))))
            .thenReturn(umaListaPermissoesFuncionalidadesFeederParaAa(102));
        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(100), eq(999), eq(List.of(3046))))
            .thenReturn(List.of(umaPermissaoTratarLead(100)));
        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(10), eq(999),
            eq(List.of(15000, 15005, 15012, 3046, 20018, 20101, 20100))))
            .thenReturn(umaListaPermissoesFuncionalidadesFeederParaAa(10));

        service.atualizarPermissaoFeeder(aaComPermissaoFeeder);

        var permissoes = Stream.of(
                List.of(umaPermissaoTratarLead(100)),
                umaListaPermissoesFuncionalidadesFeederParaAa(102),
                umaListaPermissoesFuncionalidadesFeederParaAa(10))
            .flatMap(List::stream)
            .collect(Collectors.toList());

        verify(permissaoEspecialRepository, times(0))
            .deletarPermissaoEspecialBy(anyList(),anyList());
        verify(usuarioHistoricoService, times(1))
            .save(anyList());
        verify(usuarioService, times(1))
            .getPermissoesEspeciaisDoUsuario(eq(100), eq(999), eq(List.of(3046)));
        verify(usuarioService, times(1))
            .getPermissoesEspeciaisDoUsuario(eq(102), eq(999), eq(List.of(20018, 20101, 15000, 15005, 15012, 3046)));
        verify(usuarioService, times(1))
            .getPermissoesEspeciaisDoUsuario(eq(10), eq(999), eq(List.of(15000, 15005, 15012, 3046, 20018, 20101, 20100)));
        verify(usuarioService, times(1))
            .salvarPermissoesEspeciais(permissoes);
    }

    @Test
    public void atualizarPermissaoFeeder_naoDeveDuplicarPermissoes_quandoUsuariosJaPossuiremAsPermissoes() {
        var aaComPermissaoFeeder = umAgenteAutorizadoFeederDto();
        aaComPermissaoFeeder.setFeeder(ETipoFeeder.RESIDENCIAL);
        aaComPermissaoFeeder.setSocioDeOutroAaComPermissaoFeeder(true);

        service.atualizarPermissaoFeeder(aaComPermissaoFeeder);

        verify(permissaoEspecialRepository, times(0)).deletarPermissaoEspecialBy(anyList(),anyList());
        verify(usuarioService, times(1)).salvarPermissoesEspeciais(anyList());
        verify(usuarioHistoricoService, times(0)).save(anyList());
    }

    @Test
    public void atualizarPermissaoFeeder_deveRemoverPermissaoFeeder_quandoAaTiverPermissaoParaFeeder() {
        var aaSemPermissaoFeeder = umAgenteAutorizadoFeederDto();
        aaSemPermissaoFeeder.setFeeder(ETipoFeeder.NAO_FEEDER);
        aaSemPermissaoFeeder.setSocioDeOutroAaComPermissaoFeeder(false);
        when(usuarioRepository.exists(anyInt())).thenReturn(true);

        service.atualizarPermissaoFeeder(aaSemPermissaoFeeder);
        var funcionalidades = new ArrayList<>(FUNCIONALIDADES_FEEDER_PARA_AA);
        funcionalidades.addAll(FUNCIONALIDADES_FEEDER_PARA_COLABORADORES_AA_RESIDENCIAL);
        funcionalidades.add(FUNCIONALIDADE_TRABALHAR_ALARME_ID);

        verify(permissaoEspecialRepository, times(1))
            .deletarPermissaoEspecialBy(
                funcionalidades,
                List.of(100, 102, 10));

        verify(permissaoEspecialRepository, times(0)).save(any(PermissaoEspecial.class));
        verify(usuarioHistoricoService, times(1)).save(anyList());
    }

    @Test
    public void atualizarPermissaoFeeder_naoDeveRemoverPermissaoDoSocio_quandoSocioTiverOutroAaComPermissaoFeeder() {
        var aaSemPermissaoFeeder = umAgenteAutorizadoFeederDto();
        aaSemPermissaoFeeder.setFeeder(ETipoFeeder.NAO_FEEDER);
        aaSemPermissaoFeeder.setSocioDeOutroAaComPermissaoFeeder(true);
        when(usuarioRepository.exists(anyInt())).thenReturn(true);

        service.atualizarPermissaoFeeder(aaSemPermissaoFeeder);

        var funcionalidades = new ArrayList<>(FUNCIONALIDADES_FEEDER_PARA_AA);
        funcionalidades.addAll(FUNCIONALIDADES_FEEDER_PARA_COLABORADORES_AA_RESIDENCIAL);
        funcionalidades.add(FUNCIONALIDADE_TRABALHAR_ALARME_ID);

        verify(permissaoEspecialRepository, times(1))
            .deletarPermissaoEspecialBy(
                funcionalidades,
                List.of(100, 102));

        verify(permissaoEspecialRepository, times(0)).save(any(PermissaoEspecial.class));
        verify(usuarioHistoricoService, times(1)).save(anyList());
    }

    @Test
    public void atualizarPermissaoFeeder_naoDeveAdicionarPermissao_quandoUsuarioLojaFuturo() {
        var aaComPermissaoFeeder = umAgenteAutorizadoLojaFuturo();
        aaComPermissaoFeeder.setFeeder(ETipoFeeder.RESIDENCIAL);
        aaComPermissaoFeeder.setSocioDeOutroAaComPermissaoFeeder(false);

        when(usuarioRepository.findComplete(1000)).thenReturn(
            umUsuario(CodigoCargo.ASSISTENTE_RELACIONAMENTO, ESituacao.A, 1000));

        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(5), eq(999),
            eq(List.of(15000, 15005, 15012, 3046, 20018, 20101, 20100))))
            .thenReturn(umaListaPermissoesFuncionalidadesFeederParaAa(5));

        service.atualizarPermissaoFeeder(aaComPermissaoFeeder);

        verify(usuarioService, times(1))
            .getPermissoesEspeciaisDoUsuario(eq(5), eq(999), eq(List.of(15000, 15005, 15012, 3046, 20018, 20101, 20100)));
    }

    @Test
    public void alterarSituacaoUsuarioFeeder_deveLancarException_quandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findComplete(1111)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.alterarSituacaoUsuarioFeeder(umSituacaoAlteracaoMqDto()));
    }

    @Test
    public void alterarSituacaoUsuarioFeeder_deveLancarException_quandoUsuarioNaoFeeder() {
        when(usuarioRepository.findComplete(1111)).thenReturn(
            umUsuario(CodigoCargo.SUPERVISOR_OPERACAO, ESituacao.A, 1111));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.alterarSituacaoUsuarioFeeder(umSituacaoAlteracaoMqDto()))
            .withMessage("Usuário não Feeder.");
    }

    @Test
    public void alterarSituacaoUsuarioFeeder_deveInativarUsuarioEGerarHistorico_quandoMensagemParaInativar() {
        when(usuarioRepository.findComplete(1111)).thenReturn(
            umUsuario(CodigoCargo.IMPORTADOR_CARGAS, ESituacao.A, 1111));

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
        when(usuarioRepository.findComplete(1111)).thenReturn(
            umUsuario(CodigoCargo.GERADOR_LEADS, ESituacao.I, 1111));

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
        when(usuarioRepository.findById(1111)).thenReturn(
            umUsuario(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_D2D, ESituacao.A, 1111));

        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(1111), eq(2222), eq(List.of(FUNCIONALIDADE_TRATAR_LEAD_ID))))
            .thenReturn(List.of(umaPermissaoTratarLead(1111)));

        service.adicionarPermissaoFeederParaUsuarioNovo(umUsuarioDto(), umUsuarioMqRequest());

        verify(usuarioService, times(1))
            .getPermissoesEspeciaisDoUsuario(eq(1111), eq(2222), eq(List.of(FUNCIONALIDADE_TRATAR_LEAD_ID)));
        verify(usuarioService, times(1))
            .salvarPermissoesEspeciais(eq(List.of(umaPermissaoTratarLead(1111))));
    }

    @Test
    public void adicionarPermissaoFeederParaUsuarioNovo_deveNaoSalvarPermissaoTratarLead_quandoAaNaoForFeeder() {
        var usuarioNovo = umUsuarioMqRequest();
        usuarioNovo.setAgenteAutorizadoFeeder(ETipoFeeder.NAO_FEEDER);

        service.adicionarPermissaoFeederParaUsuarioNovo(umUsuarioDto(), usuarioNovo);

        verify(usuarioService, never()).getPermissoesEspeciaisDoUsuario(anyInt(), anyInt(), anyList());
        verify(usuarioService, never()).salvarPermissoesEspeciais(anyList());
    }

    @Test
    public void adicionarPermissaoFeederParaUsuarioNovo_deveSalvarPermissoesBackoffice_quandoUsuarioForBackoffice() {
        var usuarioNovo = umUsuarioMqRequest();
        usuarioNovo.setCargo(CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_D2D);

        var permissoesEsperadas = new ArrayList<Integer>();
        permissoesEsperadas.addAll(FUNCIONALIDADES_FEEDER_PARA_COLABORADORES_AA_RESIDENCIAL);
        permissoesEsperadas.addAll(FUNCIONALIDADES_FEEDER_PARA_AA);

        when(usuarioRepository.findById(1111)).thenReturn(
            umUsuario(CodigoCargo.AGENTE_AUTORIZADO_BACKOFFICE_D2D, ESituacao.A, 1111));
        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(1111), eq(2222), eq(permissoesEsperadas)))
            .thenReturn(umaListaPermissoesFuncionalidadesFeederParaAa(1111));

        service.adicionarPermissaoFeederParaUsuarioNovo(umUsuarioDto(), usuarioNovo);

        verify(usuarioService, times(1))
            .getPermissoesEspeciaisDoUsuario(eq(1111), eq(2222), eq(permissoesEsperadas));
        verify(usuarioService, times(1))
            .salvarPermissoesEspeciais(eq(umaListaPermissoesFuncionalidadesFeederParaAa(1111)));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoFeederParaUsuarioNovoMso_deveSalvarPermissoesEmpresarial_quandoUsuarioMsoComTiposFeederEmpresarial() {
        when(usuarioRepository.findById(eq(150016)))
            .thenReturn(Optional.of(umUsuarioMso(Set.of(EMPRESARIAL))));

        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(150016), eq(101112),
            eq(FUNCIONALIDADES_FEEDER_PARA_MSO_EMPRESARIAL)))
            .thenReturn(umaListaPermissoesFuncionalidadesFeederParaMsoEmpresarial(150016));

        service.adicionarPermissaoFeederParaUsuarioNovoMso(umUsuarioMso(Set.of(EMPRESARIAL)));

        verify(usuarioRepository, times(1))
            .findById(eq(150016));
        verify(usuarioService, times(1))
            .getPermissoesEspeciaisDoUsuario(eq(150016), eq(101112), eq(FUNCIONALIDADES_FEEDER_PARA_MSO_EMPRESARIAL));
        verify(usuarioService, times(1))
            .salvarPermissoesEspeciais(umaListaPermissoesFuncionalidadesFeederParaMsoEmpresarial(150016));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoFeederParaUsuarioNovoMso_deveSalvarPermissoesResidencial_quandoUsuarioMsoComTiposFeederResidencial() {
        when(usuarioRepository.findById(eq(150016)))
            .thenReturn(Optional.of(umUsuarioMso(Set.of(RESIDENCIAL))));

        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(150016), eq(101112),
            eq(FUNCIONALIDADES_FEEDER_PARA_MSO_RESIDENCIAL)))
            .thenReturn(umaListaPermissoesFuncionalidadesFeederParaMsoResidencial(150016));

        service.adicionarPermissaoFeederParaUsuarioNovoMso(umUsuarioMso(Set.of(RESIDENCIAL)));

        verify(usuarioRepository, times(1))
            .findById(eq(150016));
        verify(usuarioService, times(1))
            .getPermissoesEspeciaisDoUsuario(eq(150016), eq(101112), eq(FUNCIONALIDADES_FEEDER_PARA_MSO_RESIDENCIAL));
        verify(usuarioService, times(1))
            .salvarPermissoesEspeciais(umaListaPermissoesFuncionalidadesFeederParaMsoResidencial(150016));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void adicionarPermissaoFeederParaUsuarioNovoMso_deveSalvarPermissoesResidencialEEmpresarial_quandoUsuarioMsoComTiposFeederResidencialEEmpresarial() {
        var funcionalidades = Stream.of(FUNCIONALIDADES_FEEDER_PARA_MSO_RESIDENCIAL, FUNCIONALIDADES_FEEDER_PARA_MSO_EMPRESARIAL)
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());

        var permissoes = Stream.of(
                umaListaPermissoesFuncionalidadesFeederParaMsoResidencial(150016),
                umaListaPermissoesFuncionalidadesFeederParaMsoEmpresarial(150016))
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());

        when(usuarioRepository.findById(eq(150016)))
            .thenReturn(Optional.of(umUsuarioMso(Set.of(RESIDENCIAL, EMPRESARIAL))));

        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(150016), eq(101112), eq(funcionalidades)))
            .thenReturn(permissoes);

        service.adicionarPermissaoFeederParaUsuarioNovoMso(umUsuarioMso(Set.of(RESIDENCIAL, EMPRESARIAL)));

        verify(usuarioRepository, times(1))
            .findById(eq(150016));
        verify(usuarioService, times(1))
            .getPermissoesEspeciaisDoUsuario(eq(150016), eq(101112), eq(funcionalidades));
        verify(usuarioService, times(1))
            .salvarPermissoesEspeciais(permissoes);
    }

    @Test
    public void adicionarPermissaoFeederParaUsuarioNovoMso_naoDeveSalvarPermissoes_quandoUsuarioMsoComTiposFeederVazio() {
        when(usuarioRepository.findById(eq(150016)))
            .thenReturn(Optional.of(umUsuarioMso(Set.of())));

        when(usuarioService.getPermissoesEspeciaisDoUsuario(eq(150016), eq(101112), eq(List.of())))
            .thenReturn(List.of());

        service.adicionarPermissaoFeederParaUsuarioNovoMso(umUsuarioMso(Set.of()));

        verify(usuarioRepository, times(1)).findById(eq(150016));
        verify(usuarioService, times(1)).getPermissoesEspeciaisDoUsuario(eq(150016), eq(101112), eq(List.of()));
        verify(usuarioService, times(1)).salvarPermissoesEspeciais(eq(List.of()));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void removerPermissaoFeederUsuarioAtualizadoMso_deveRemoverPermissoesResidencialEEmpresarial_quandoAtualizarUsuarioMso() {
        service.removerPermissaoFeederUsuarioAtualizadoMso(umUsuarioMso(Set.of(EMPRESARIAL, RESIDENCIAL)));

        verify(permissaoEspecialRepository, times(1))
            .deletarPermissaoEspecialBy(FUNCIONALIDADES_FEEDER_PARA_MSO_RESIDENCIAL, List.of(150016));
        verify(permissaoEspecialRepository, times(1))
            .deletarPermissaoEspecialBy(FUNCIONALIDADES_FEEDER_PARA_MSO_EMPRESARIAL, List.of(150016));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void removerPermissaoFeederUsuarioAtualizadoMso_naoDeveRemoverPermissoesResidencialEEmpresarial_quandoAtualizarUsuarioNaoMso() {
        service.removerPermissaoFeederUsuarioAtualizadoMso(umUsuarioOuvidoria());

        verify(permissaoEspecialRepository, never())
            .deletarPermissaoEspecialBy(FUNCIONALIDADES_FEEDER_PARA_MSO_RESIDENCIAL, List.of(150017));
        verify(permissaoEspecialRepository, never())
            .deletarPermissaoEspecialBy(FUNCIONALIDADES_FEEDER_PARA_MSO_EMPRESARIAL, List.of(150017));
    }

    @Test
    public void removerPermissaoFeederUsuarioAtualizadoMso_naoDeveRemoverPermissoesResidencialEEmpresarial_quandoUsuarioNovo() {
        var umUsuarioNovo = umUsuarioMso(Set.of(EMPRESARIAL, RESIDENCIAL));
        umUsuarioNovo.setId(null);

        service.removerPermissaoFeederUsuarioAtualizadoMso(umUsuarioNovo);

        verify(permissaoEspecialRepository, never())
            .deletarPermissaoEspecialBy(FUNCIONALIDADES_FEEDER_PARA_MSO_RESIDENCIAL, List.of());
        verify(permissaoEspecialRepository, never())
            .deletarPermissaoEspecialBy(FUNCIONALIDADES_FEEDER_PARA_MSO_EMPRESARIAL, List.of());
    }

    @Test
    public void adicionarPermissaoFeederParaUsuarioNovo_naoDeveAdicionarPermissao_quandoUsuarioLojaFuturo() {
        var usuarioNovo = umUsuarioMqRequest();
        usuarioNovo.setCargo(CodigoCargo.ASSISTENTE_RELACIONAMENTO);

        service.adicionarPermissaoFeederParaUsuarioNovo(umUsuarioDto(), usuarioNovo);

        verify(usuarioRepository, never()).findById(anyInt());
        verify(usuarioService, never()).getPermissoesEspeciaisDoUsuario(anyInt(), anyInt(), anyList());
        verify(usuarioService, never()).salvarPermissoesEspeciais(anyList());
    }

    @Test
    public void limparCpfEAlterarEmailUsuarioFeeder_deveLimparCpfAlterarEmailEGerarHistorico_quandoUsuarioFeederExcluido() {
        var usuarioSemCpf = umUsuarioFeeder(100).get();
        usuarioSemCpf.setCpf(null);
        usuarioSemCpf.setEmail("INATIVO_THIAGOTESTE@XBRAIN.COM.BR");

        when(usuarioRepository.findComplete(eq(100))).thenReturn(umUsuarioFeeder(100));

        service.limparCpfEAlterarEmailUsuarioFeeder(100);

        verify(usuarioRepository, times(1)).save(eq(usuarioSemCpf));
        verify(usuarioHistoricoService, times(1)).save(eq(umUsuarioHistorico()));
    }

    @Test
    public void salvarPermissoesEspeciaisCoordenadoresGerentes_deveSalvarPermissoes_seUsuarioNaoPossuirAsFuncs() {

        when(usuarioRepository.exists(1)).thenReturn(true);
        when(permissaoEspecialRepository.findByUsuario(1)).thenReturn(List.of());
        service.salvarPermissoesEspeciaisCoordenadoresGerentes(umaListaUsuariosIds(), umUsuarioLogadoId());

        verify(usuarioRepository, atLeastOnce()).exists(anyInt());
        verify(permissaoEspecialRepository, times(1)).findByUsuario(eq(1));
        verify(permissaoEspecialRepository, atLeastOnce()).save(eq(umaListaPermissoesEspeciais(15000, 15005, 15012)));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void salvarPermissoesEspeciaisCoordenadoresGerentes_deveSalvarPermissoesAindaNaoAtribuidas_seUsuarioPossuirApenasUmaFunc() {

        when(usuarioRepository.exists(1)).thenReturn(true);
        when(permissaoEspecialRepository.findByUsuario(1)).thenReturn(List.of(15000, 15012));
        service.salvarPermissoesEspeciaisCoordenadoresGerentes(umaListaUsuariosIds(), umUsuarioLogadoId());

        verify(usuarioRepository, atLeastOnce()).exists(anyInt());
        verify(permissaoEspecialRepository, times(1)).findByUsuario(eq(1));
        verify(permissaoEspecialRepository, atLeastOnce()).save(eq(umaListaPermissoesEspeciaisComUmaFuncionalidade(15005)));
    }

    @Test
    public void salvarPermissoesEspeciaisCoordenadoresGerentes_deveNaoSalvarPermissoes_seUsuarioNaoExistir() {

        when(usuarioRepository.exists(1)).thenReturn(false);
        when(permissaoEspecialRepository.findByUsuario(1)).thenReturn(List.of(15000, 15005, 15012));
        service.salvarPermissoesEspeciaisCoordenadoresGerentes(umaListaUsuariosIds(), umUsuarioLogadoId());

        verify(usuarioRepository, times(1)).exists(anyInt());
        verify(permissaoEspecialRepository, times(1)).findByUsuario(eq(1));
        verify(permissaoEspecialRepository, never()).save(any(PermissaoEspecial.class));
    }

    @Test
    public void salvarPermissoesEspeciaisCoordenadoresGerentes_deveNaoSalvarPermissoes_seUsuarioJaPossuirAsFuncionalidades() {

        when(usuarioRepository.exists(1)).thenReturn(true);
        when(permissaoEspecialRepository.findByUsuario(1)).thenReturn(List.of(15000, 15005, 15012));
        service.salvarPermissoesEspeciaisCoordenadoresGerentes(umaListaUsuariosIds(), umUsuarioLogadoId());

        verify(usuarioRepository, times(1)).exists(anyInt());
        verify(permissaoEspecialRepository, times(1)).findByUsuario(eq(1));
        verify(permissaoEspecialRepository, never()).save(any(PermissaoEspecial.class));
    }

    private List<PermissaoEspecial> umaListaPermissoesEspeciaisComUmaFuncionalidade(Integer funcionalidadeId) {
        var localDateTime = dataHoraAtual.getDataHora();
        return List.of(PermissaoEspecial.builder()
            .funcionalidade(Funcionalidade.builder().id(funcionalidadeId).build())
            .usuario(new Usuario(umaListaUsuariosIds().stream().findFirst().orElseThrow()))
            .dataCadastro(localDateTime)
            .usuarioCadastro(Usuario.builder().id(umUsuarioLogadoId()).build())
            .build());
    }

    private List<PermissaoEspecial> umaListaPermissoesEspeciais(Integer agendar, Integer descartar, Integer visualizar) {
        var localDateTime = dataHoraAtual.getDataHora();
        return List.of(
            PermissaoEspecial.builder()
                .funcionalidade(Funcionalidade.builder().id(agendar).build())
                .usuario(new Usuario(umaListaUsuariosIds().stream().findFirst().orElseThrow()))
                .dataCadastro(localDateTime)
                .usuarioCadastro(Usuario.builder().id(umUsuarioLogadoId()).build())
                .build(),
            PermissaoEspecial.builder()
                .funcionalidade(Funcionalidade.builder().id(descartar).build())
                .usuario(new Usuario(umaListaUsuariosIds().stream().findFirst().orElseThrow()))
                .dataCadastro(localDateTime)
                .usuarioCadastro(Usuario.builder().id(umUsuarioLogadoId()).build())
                .build(),
            PermissaoEspecial.builder()
                .funcionalidade(Funcionalidade.builder().id(visualizar).build())
                .usuario(new Usuario(umaListaUsuariosIds().stream().findFirst().orElseThrow()))
                .dataCadastro(localDateTime)
                .usuarioCadastro(Usuario.builder().id(umUsuarioLogadoId()).build())
                .build());
    }

    private List<Integer> umaListaUsuariosIds() {
        return List.of(1);
    }

    private int umUsuarioLogadoId() {
        return 1;
    }

    private AgenteAutorizadoPermissaoFeederDto umAgenteAutorizadoFeederDto() {
        return AgenteAutorizadoPermissaoFeederDto.builder()
            .colaboradoresVendasIds(Lists.newArrayList(100, 102))
            .usuarioProprietarioId(10)
            .usuarioCadastroId(999)
            .agenteAutorizadoId(30)
            .build();
    }

    private AgenteAutorizadoPermissaoFeederDto umAgenteAutorizadoLojaFuturo() {
        return AgenteAutorizadoPermissaoFeederDto.builder()
            .colaboradoresVendasIds(Lists.newArrayList(1000))
            .usuarioProprietarioId(5)
            .usuarioCadastroId(999)
            .agenteAutorizadoId(1)
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

    private Optional<Usuario> umUsuario(CodigoCargo codigoCargo, ESituacao situacao, Integer id) {
        return Optional.of(
            Usuario.builder()
                .id(id)
                .situacao(situacao)
                .departamento(umDepartamento(1, "Departamento"))
                .cargo(
                    Cargo.builder()
                        .codigo(codigoCargo)
                        .build())
                .build());
    }

    private Optional<Usuario> umUsuarioFeeder(Integer id) {
        return Optional.of(
            Usuario.builder()
                .id(id)
                .email("THIAGOTESTE@XBRAIN.COM.BR")
                .cpf("25248663865")
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
            .agenteAutorizadoFeeder(ETipoFeeder.RESIDENCIAL)
            .agenteAutorizadoId(111)
            .usuarioCadastroId(2222)
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_D2D)
            .build();
    }

    private UsuarioHistorico umUsuarioHistorico() {
        return UsuarioHistorico.builder()
            .usuario(new Usuario(100))
            .observacao("Usuário excluído. CPF e Email alterados automaticamente")
            .situacao(ESituacao.I)
            .build();
    }

    private static Usuario umUsuarioMso(Set<ETipoFeederMso> tiposFeeder) {
        return Usuario.builder()
            .id(150016)
            .nome("MSO FEEDER")
            .cpf("873.616.099-70")
            .email("MSO.FEEDER@MSO.COM.BR")
            .usuarioCadastro(umUsuarioCadastro())
            .usuariosHierarquia(new HashSet<>())
            .cargo(Cargo
                .builder()
                .quantidadeSuperior(50)
                .nivel(Nivel
                    .builder()
                    .id(2)
                    .build())
                .build())
            .tiposFeeder(tiposFeeder)
            .situacao(ESituacao.A)
            .build();
    }

    private static Usuario umUsuarioOuvidoria() {
        return Usuario.builder()
            .id(150017)
            .nome("OUVIDORIA NAO FEEDER")
            .cpf("876.466.334-53")
            .email("OUVIDORIA.NAOFEEDER@OUVIDORIA.COM.BR")
            .usuarioCadastro(umUsuarioCadastro())
            .usuariosHierarquia(new HashSet<>())
            .cargo(Cargo
                .builder()
                .quantidadeSuperior(50)
                .nivel(Nivel
                    .builder()
                    .id(15)
                    .build())
                .build())
            .tiposFeeder(Set.of())
            .situacao(ESituacao.A)
            .build();
    }

    private static Usuario umUsuarioCadastro() {
        return Usuario.builder()
            .id(101112)
            .nome("COLABORADOR SUPORTE")
            .build();
    }
}
