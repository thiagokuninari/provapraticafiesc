package br.com.xbrain.autenticacao.modules.horarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAtuacaoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;

import static br.com.xbrain.autenticacao.modules.horarioacesso.helper.HorarioHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class HorarioAcessoServiceTest {

    public static final ValidacaoException HORARIO_ACESSO_NAO_ENCONTRADO =
        new ValidacaoException("Horário de acesso não encontrado.");

    @Autowired
    private HorarioAcessoService service;
    @MockBean
    private HorarioAcessoRepository repository;
    @MockBean
    private HorarioAtuacaoRepository atuacaoRepository;
    @MockBean
    private HorarioHistoricoRepository historicoRepository;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private SiteService siteService;
    @MockBean
    private DataHoraAtual dataHoraAtual;

    @Test
    public void getHorariosAcesso_deveRetornarListaDeHorarioAcessoResponse_aoBuscarHorariosAcesso() {
        when(repository.findAll(any(Predicate.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(umHorarioAcesso())));
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(umaListaHorariosAtuacao());

        var pageable = new PageRequest(0, 10, "horarioAcessoId", "asc");

        assertThat(service.getHorariosAcesso(pageable, new HorarioAcessoFiltros()))
            .isEqualTo(new PageImpl<>(List.of(umHorarioAcessoResponse())));

        verify(repository, times(1)).findAll(any(Predicate.class), eq(pageable));
        verify(atuacaoRepository, times(1)).findByHorarioAcessoId(eq(1));
    }

    @Test
    public void getHistoricos_deveRetornarListaDeHorarioAcessoResponse_aoBuscarHistoricos() {
        when(historicoRepository.findByHorarioAcessoId(anyInt(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(umHorarioHistorico())));
        when(atuacaoRepository.findByHorarioHistoricoId(anyInt())).thenReturn(umaListaHorariosAtuacao());

        var pageable = new PageRequest(0, 10, "horarioHistoricoId", "asc");

        assertThat(service.getHistoricos(pageable, 1))
            .isEqualTo(new PageImpl<>(List.of(umHorarioHistoricoResponse())));
        
        verify(historicoRepository, times(1)).findByHorarioAcessoId(eq(1), eq(pageable));
        verify(atuacaoRepository, times(1)).findByHorarioHistoricoId(eq(1));
    }

    @Test
    public void getHorarioAcesso_deveRetornarHorarioAcessoResponse_aoBuscarHorarioAcesso() {
        when(repository.findById(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(umaListaHorariosAtuacao());

        assertThat(service.getHorarioAcesso(1)).isEqualTo(umHorarioAcessoResponse());

        verify(repository, times(1)).findById(eq(1));
        verify(atuacaoRepository, times(1)).findByHorarioAcessoId(eq(1));
    }

    @Test
    public void getHorarioAcesso_deveRetornarException_quandoNaoEncontrarHorarioAcesso() {
        when(repository.findById(anyInt())).thenThrow(HORARIO_ACESSO_NAO_ENCONTRADO);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> repository.findById(100))
            .withMessage("Horário de acesso não encontrado.");

        verify(repository, times(1)).findById(eq(100));
    }

    @Test
    public void save_deveRetornarHorarioAcesso_quandoSalvarNovoHorario() {
        var usuario = Usuario.builder().id(100).nome("USUARIO TESTE").build();
        
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().usuario(usuario).build());
        when(repository.save(any(HorarioAcesso.class))).thenReturn(umHorarioAcesso());
        when(historicoRepository.save(any(HorarioHistorico.class)))
            .thenReturn(umHorarioHistorico());

        assertThatCode(() -> service.criaHorariosAcesso(
                umaListaHorariosAtuacao(), umHorarioAcesso(), umHorarioHistorico()))
            .doesNotThrowAnyException();
        
        var request = umHorarioAcessoRequest();
        request.setId(null);

        assertThat(service.save(request))
            .extracting("id", "site", "dataAlteracao", "usuarioAlteracaoId", "usuarioAlteracaoNome")
            .containsExactlyInAnyOrder(1, Site.builder().id(100).nome("SITE TESTE").build(),
                LocalDateTime.of(2021, 11, 22, 13, 53, 10), 100, "USUARIO TESTE");
    }

    @Test
    public void save_deveRetornarHorarioAcesso_quandoEditarHorario() {
        var usuario = Usuario.builder().id(101).nome("USUARIO TESTE EDIÇÃO").build();

        when(repository.findById(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().usuario(usuario).build());

        var horario = umHorarioAcesso();
        horario.setUsuarioAlteracaoId(usuario.getId());
        horario.setUsuarioAlteracaoNome(usuario.getNome());

        when(repository.save(any(HorarioAcesso.class))).thenReturn(horario);
        when(historicoRepository.save(any(HorarioHistorico.class)))
            .thenReturn(umHorarioHistorico());

        assertThatCode(() -> service.criaHorariosAcesso(
                umaListaHorariosAtuacao(), umHorarioAcesso(), umHorarioHistorico()))
            .doesNotThrowAnyException();
        
        var request = umHorarioAcessoRequest();

        assertThat(service.save(request))
            .extracting("id", "site", "dataAlteracao", "usuarioAlteracaoId", "usuarioAlteracaoNome")
            .containsExactlyInAnyOrder(1, Site.builder().id(100).nome("SITE TESTE").build(),
                LocalDateTime.of(2021, 11, 22, 13, 53, 10), 101, "USUARIO TESTE EDIÇÃO");
    }

    @Test
    public void save_deveRetornarException_casoHorarioAcessoNaoEncontrado() {
        when(repository.findById(anyInt())).thenThrow(HORARIO_ACESSO_NAO_ENCONTRADO);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umHorarioAcessoRequest()))
            .withMessage("Horário de acesso não encontrado.");

        verify(repository, times(1)).findById(eq(1));
    }

    @Test
    public void save_deveRetornarException_casoSiteJaPossuiHorarioAcesso() {
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));

        var request = umHorarioAcessoRequest();
        request.setId(null);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(request))
            .withMessage("Site já possui horário de acesso cadastrado.");

        verify(repository, times(1)).findBySiteId(eq(100));
    }

    @Test
    public void getStatus_deveRetornarTrue_quandoHorarioAtualEstiverDentroDoHorarioPermitido() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().usuario(umOperadorTelevendas()).build());
        when(siteService.getSitesPorPermissao(any(Usuario.class)))
            .thenReturn(List.of(SelectResponse.of(100, "SITE TEST")));
        when(siteService.findById(anyInt())).thenReturn(Site.builder().id(100).build());
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.valueOf(dataHoraAtual.getDataHora()))
            .horarioInicio(LocalTime.of(9,0))
            .horarioFim(LocalTime.of(18,0))
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));

        assertThat(service.getStatus()).isTrue();

        verify(autenticacaoService, times(1)).getUsuarioAutenticado();
        verify(siteService, times(1)).getSitesPorPermissao(eq(umOperadorTelevendas()));
        verify(siteService, times(1)).findById(eq(100));
        verify(repository, times(1)).findBySiteId(eq(100));
        verify(atuacaoRepository, times(1)).findByHorarioAcessoId(eq(1));
    }

    @Test
    public void getStatus_deveRetornarFalse_quandoHorarioAtualNaoEstiverDentroDoHorarioPermitido() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().usuario(umOperadorTelevendas()).build());
        when(siteService.getSitesPorPermissao(any(Usuario.class)))
            .thenReturn(List.of(SelectResponse.of(100, "SITE TEST")));
        when(siteService.findById(anyInt())).thenReturn(Site.builder().id(100).build());
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.valueOf(dataHoraAtual.getDataHora()))
            .horarioInicio(LocalTime.of(9,0))
            .horarioFim(LocalTime.of(9,1))
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));

        assertThat(service.getStatus()).isFalse();

        verify(autenticacaoService, times(1)).getUsuarioAutenticado();
        verify(siteService, times(1)).getSitesPorPermissao(eq(umOperadorTelevendas()));
        verify(siteService, times(1)).findById(eq(100));
        verify(repository, times(1)).findBySiteId(eq(100));
        verify(atuacaoRepository, times(1)).findByHorarioAcessoId(eq(1));
    }

    @Test
    public void getStatus_deveRetornarFalse_quandoHorarioAtualNaoSeEncaixarEmNenhumDiaPermitido() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().usuario(umOperadorTelevendas()).build());
        when(siteService.getSitesPorPermissao(any(Usuario.class)))
            .thenReturn(List.of(SelectResponse.of(100, "SITE TEST")));
        when(siteService.findById(anyInt())).thenReturn(Site.builder().id(100).build());
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        
        var horarioAtuacao = HorarioAtuacao.builder()
            .horarioAcesso(umHorarioAcesso())
            .diaSemana(EDiaSemana.DOMINGO)
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));

        assertThat(service.getStatus()).isFalse();

        verify(autenticacaoService, times(1)).getUsuarioAutenticado();
        verify(siteService, times(1)).getSitesPorPermissao(eq(umOperadorTelevendas()));
        verify(siteService, times(1)).findById(eq(100));
        verify(repository, times(1)).findBySiteId(eq(100));
        verify(atuacaoRepository, times(1)).findByHorarioAcessoId(eq(1));
    }

    @Test
    public void getStatus_comParametroSiteId_deveRetornarTrue_quandoHorarioAtualEstiverDentroDoHorarioPermitido() {
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.valueOf(LocalDateTime.now()))
            .horarioInicio(LocalTime.of(9,0))
            .horarioFim(LocalTime.of(18,0))
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));

        assertThat(service.getStatus(100)).isTrue();

        verify(repository, times(1)).findBySiteId(eq(100));
        verify(atuacaoRepository, times(1)).findByHorarioAcessoId(eq(1));
    }

    @Test
    public void getStatus_comParametroSiteId_deveRetornarFalse_quandoHorarioAtualNaoEstiverDentroDoHorarioPermitido() {
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.valueOf(dataHoraAtual.getDataHora()))
            .horarioInicio(LocalTime.of(9,0))
            .horarioFim(LocalTime.of(9,1))
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));

        assertThat(service.getStatus(100)).isFalse();

        verify(repository, times(1)).findBySiteId(eq(100));
        verify(atuacaoRepository, times(1)).findByHorarioAcessoId(eq(1));
    }

    @Test
    public void getStatus_comParametroSiteId_deveRetornarFalse_quandoHorarioAtualNaoSeEncaixarEmNenhumDiaPermitido() {
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        
        var horarioAtuacao = HorarioAtuacao.builder()
            .horarioAcesso(umHorarioAcesso())
            .diaSemana(EDiaSemana.DOMINGO)
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));

        assertThat(service.getStatus(100)).isFalse();

        verify(repository, times(1)).findBySiteId(eq(100));
        verify(atuacaoRepository, times(1)).findByHorarioAcessoId(eq(1));
    }
}
