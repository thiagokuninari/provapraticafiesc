package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service;

import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client.EquipeTecnicaClient;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeTecnicaSupervisionadasResponse;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.helper.EquipeTecnicaHelper.umaListaEquipeTecnicaSupervisionada;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EquipeTecnicaServiceTest {

    @InjectMocks
    EquipeTecnicaService service;
    @Mock
    EquipeTecnicaClient client;

    @Test
    public void getEquipesPorSupervisor_deveRetornarListaEquipeTecnicaVazia_quandoSupervisorNaoSupervisionarEquipe() {
        doReturn(List.of()).when(client).getEquipesTecnicasPorSupervisor(1);

        assertThat(service.getEquipesPorSupervisor(1)).isEmpty();

        verify(client).getEquipesTecnicasPorSupervisor(1);
    }

    @Test
    public void getEquipesPorSupervisor_deveRetornarEquipeTecnicaPorSupervisor_quandoSupervisorSupervisionarEquipe() {
        doReturn(umaListaEquipeTecnicaSupervisionada()).when(client).getEquipesTecnicasPorSupervisor(1);

        assertThat(service.getEquipesPorSupervisor(1))
            .extracting(
                EquipeTecnicaSupervisionadasResponse::getId,
                EquipeTecnicaSupervisionadasResponse::getDescricao
            )
            .containsExactly(
                tuple(1, "EQUIPE TECNICA 1"),
                tuple(2, "EQUIPE TECNICA 2")
            );

        verify(client).getEquipesTecnicasPorSupervisor(1);
    }

    @Test
    public void getEquipesPorSupervisor_deveRetornarListaDeEquipeTecnicaVazia_quandoErroNaApi() {
        doThrow(RetryableException.class).when(client).getEquipesTecnicasPorSupervisor(1);

        assertThat(service.getEquipesPorSupervisor(1)).isEmpty();

        verify(client).getEquipesTecnicasPorSupervisor(1);
    }
}
