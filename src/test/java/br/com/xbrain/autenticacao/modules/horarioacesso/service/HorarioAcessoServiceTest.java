package br.com.xbrain.autenticacao.modules.horarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAtuacaoDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAtuacaoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import com.querydsl.core.types.Predicate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static br.com.xbrain.autenticacao.modules.horarioacesso.util.HorarioHelpers.*;
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

    @Test
    public void getHorariosAcesso_deveRetornarListaDeHorarioAcessoResponse_aoBuscarHorariosAcesso() {
        var primeiroHorario = umHorarioAcesso();
        var segundoHorario = umHorarioAcesso();
        segundoHorario.setId(2);
        segundoHorario.setDataAlteracao(LocalDateTime.of(2021, 11, 22, 15, 37, 10));

        when(repository.findAll(any(Predicate.class))).thenReturn(List.of(primeiroHorario, segundoHorario));

        var primeiraLista = umaListaHorariosAtuacao();
        var segundaLista = umaListaHorariosAtuacao();
        segundaLista.forEach(atuacao -> {
            atuacao.setId(atuacao.getId() + 3);
            atuacao.setHorarioAcesso(segundoHorario);
        });

        when(atuacaoRepository.findByHorarioAcessoId(1)).thenReturn(primeiraLista);
        when(atuacaoRepository.findByHorarioAcessoId(2)).thenReturn(segundaLista);

        assertThat(service.getHorariosAcesso(new HorarioAcessoFiltros()))
            .hasSize(2)
            .extracting("horarioAcessoId", "horarioHistoricoId", "siteNome", "dataAlteracao",
                "usuarioAlteracaoNome", "horariosAtuacao")
            .containsExactlyInAnyOrder(
                tuple(1, null, "SITE TESTE", "22/11/2021 13:53:10", "USUARIO TESTE", List.of(
                    HorarioAtuacaoDto.builder()
                        .id(1)
                        .diaSemana("Segunda-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(2)
                        .diaSemana("Quarta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(3)
                        .diaSemana("Sexta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build()
                )),
                tuple(2, null, "SITE TESTE", "22/11/2021 15:37:10", "USUARIO TESTE", List.of(
                    HorarioAtuacaoDto.builder()
                        .id(4)
                        .diaSemana("Segunda-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(5)
                        .diaSemana("Quarta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(6)
                        .diaSemana("Sexta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build())));
        
        verify(repository, times(1)).findAll(eq(new HorarioAcessoFiltros().toPredicate().build()));
        verify(atuacaoRepository, times(1)).findByHorarioAcessoId(eq(1));
        verify(atuacaoRepository, times(1)).findByHorarioAcessoId(eq(2));
    }

    @Test
    public void getHistoricos_deveRetornarListaDeHorarioAcessoResponse_aoBuscarHistoricos() {
        var primeiroHistorico = umHorarioHistorico();
        var segundoHistorico = umHorarioHistorico();
        segundoHistorico.setId(2);
        segundoHistorico.setDataAlteracao(LocalDateTime.of(2021, 11, 22, 15, 37, 10));

        when(historicoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(primeiroHistorico, segundoHistorico));

        var primeiraListaHistorico = umaListaHorariosAtuacao();
        var segundaListaHistorico = umaListaHorariosAtuacao();
        segundaListaHistorico.forEach(atuacao -> {
            atuacao.setId(atuacao.getId() + 3);
            atuacao.setHorarioHistorico(segundoHistorico);
        });

        when(atuacaoRepository.findByHorarioHistoricoId(1)).thenReturn(primeiraListaHistorico);
        when(atuacaoRepository.findByHorarioHistoricoId(2)).thenReturn(segundaListaHistorico);

        assertThat(service.getHistoricos(1))
            .hasSize(2)
            .extracting("horarioAcessoId", "horarioHistoricoId", "siteNome", "dataAlteracao",
                "usuarioAlteracaoNome", "horariosAtuacao")
            .containsExactlyInAnyOrder(
                tuple(1, 1, "SITE TESTE", "22/11/2021 13:53:10", "USUARIO TESTE", List.of(
                    HorarioAtuacaoDto.builder()
                        .id(1)
                        .diaSemana("Segunda-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(2)
                        .diaSemana("Quarta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(3)
                        .diaSemana("Sexta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build()
                )),
                tuple(1, 2, "SITE TESTE", "22/11/2021 15:37:10", "USUARIO TESTE", List.of(
                    HorarioAtuacaoDto.builder()
                        .id(4)
                        .diaSemana("Segunda-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(5)
                        .diaSemana("Quarta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build(),
                    HorarioAtuacaoDto.builder()
                        .id(6)
                        .diaSemana("Sexta-Feira")
                        .horarioInicio("09:00")
                        .horarioFim("15:00")
                        .build())));

        verify(historicoRepository, times(1)).findByHorarioAcessoId(eq(1));
        verify(atuacaoRepository, times(1)).findByHorarioHistoricoId(eq(1));
        verify(atuacaoRepository, times(1)).findByHorarioHistoricoId(eq(2));
    }

    @Test
    public void getHorarioAcesso_deveRetornarHorarioAcessoResponse_aoBuscarHorarioAcesso() {
        when(repository.findById(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(umaListaHorariosAtuacao());

        assertThat(service.getHorarioAcesso(1))
            .extracting("horarioAcessoId", "horarioHistoricoId", "siteNome", "dataAlteracao",
                "usuarioAlteracaoNome", "horariosAtuacao")
            .containsExactly(1, null, "SITE TESTE", "22/11/2021 13:53:10", "USUARIO TESTE", List.of(
                HorarioAtuacaoDto.builder()
                    .id(1)
                    .diaSemana("Segunda-Feira")
                    .horarioInicio("09:00")
                    .horarioFim("15:00")
                    .build(),
                HorarioAtuacaoDto.builder()
                    .id(2)
                    .diaSemana("Quarta-Feira")
                    .horarioInicio("09:00")
                    .horarioFim("15:00")
                    .build(),
                HorarioAtuacaoDto.builder()
                    .id(3)
                    .diaSemana("Sexta-Feira")
                    .horarioInicio("09:00")
                    .horarioFim("15:00")
                    .build()));

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

        assertThat(service.save(request))
            .extracting("id", "site", "dataAlteracao", "usuarioAlteracaoId", "usuarioAlteracaoNome")
            .containsExactlyInAnyOrder(1, Site.builder().id(100).nome("SITE TESTE").build(),
                LocalDateTime.of(2021, 11, 22, 13, 53, 10), 100, "USUARIO TESTE");
    }

    @Test
    public void save_deveRetornarHorarioAcesso_quandoEditarHorario() {
        when(repository.findById(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        
        var usuario = Usuario.builder().id(101).nome("USUARIO TESTE EDIÇÃO").build();

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().usuario(usuario).build());

        var horario = umHorarioAcesso();
        horario.setUsuarioAlteracaoId(usuario.getId());
        horario.setUsuarioAlteracaoNome(usuario.getNome());

        when(repository.save(any(HorarioAcesso.class))).thenReturn(horario);
        when(historicoRepository.save(any(HorarioHistorico.class)))
            .thenReturn(umHorarioHistorico());

        assertThatCode(() -> service.criaHorariosAcesso(
                umaListaHorariosAtuacao(),
                umHorarioAcesso(),
                umHorarioHistorico()))
            .doesNotThrowAnyException();
        
        var request = umHorarioAcessoRequest();
        request.setId(1);

        assertThat(service.save(request))
            .extracting("id", "site", "dataAlteracao", "usuarioAlteracaoId", "usuarioAlteracaoNome")
            .containsExactlyInAnyOrder(1, Site.builder().id(100).nome("SITE TESTE").build(),
                LocalDateTime.of(2021, 11, 22, 13, 53, 10), 101, "USUARIO TESTE EDIÇÃO");
    }

    @Test
    public void save_deveRetornarException_casoHorarioAcessoNaoEncontrado() {
        when(repository.findById(anyInt())).thenThrow(HORARIO_ACESSO_NAO_ENCONTRADO);

        var request = umHorarioAcessoRequest();
        request.setId(100);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(request))
            .withMessage("Horário de acesso não encontrado.");
        verify(repository, times(1)).findById(eq(100));
    }
}
