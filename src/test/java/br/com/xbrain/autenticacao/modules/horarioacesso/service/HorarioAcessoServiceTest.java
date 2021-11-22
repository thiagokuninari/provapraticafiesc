package br.com.xbrain.autenticacao.modules.horarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAtuacaoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioHistoricoRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static br.com.xbrain.autenticacao.modules.horarioacesso.util.HorarioHelpers.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class HorarioAcessoServiceTest {

    @Autowired
    private HorarioAcessoService service;
    @MockBean
    private HorarioAcessoRepository repository;
    @MockBean
    private HorarioAtuacaoRepository atuacaoRepository;
    @MockBean
    private HorarioHistoricoRepository historicoRepository;

    @Test
    public void getHistoricos_deveRetornarHorarioAcessoResponse_aoBuscarHistoricos() {
        var primeiroHistorico = umHorarioHistorico();
        var segundoHistorico = umHorarioHistorico();
        segundoHistorico.setId(2);
        segundoHistorico.setDataAlteracao(LocalDateTime.of(2021, 11, 22, 15, 37, 10));

        when(historicoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(primeiroHistorico, segundoHistorico));

        var primeiraListaHistorico = umaListaHorariosAtuacao();
        var segundaListaHistorico = umaListaHorariosAtuacao();
        segundaListaHistorico.add(HorarioAtuacao.builder()
            .id(7)
            .diaSemana(EDiaSemana.SABADO)
            .horarioAcesso(umHorarioAcesso())
            .horarioHistorico(umHorarioHistorico())
            .horarioInicio(LocalTime.of(8, 0))
            .horarioFim(LocalTime.of(12, 0))
            .build());
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
                tuple(1, 1, "SITE TESTE"),
                tuple(1, 2, "SITE TESTE"));
    }

    @Test
    public void getHorarioAcesso_deveRetornarException() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> repository.findById(100))
            .withMessage("Horário de acesos não encontrado.");
        verify(repository, times(1)).findById(eq(100));
    }
}
