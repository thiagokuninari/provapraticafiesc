package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UfServiceTest {

    @InjectMocks
    private UfService ufService;
    @Mock
    private UfRepository ufRepository;

    @Test
    public void findAll_deveRetornarUmaListaUfsOrdenada_quandoSolicitado() {
        var sort = new Sort("nome");
        when(ufRepository.findAll(sort))
            .thenReturn(umaListaUf());

        assertThat(ufService.findAll(sort))
            .extracting("id", "nome")
            .containsExactly(
                Tuple.tuple(1, "PR"),
                Tuple.tuple(2, "SP")
            );

        verify(ufRepository).findAll(sort);
    }

    @Test
    public void findAll_deveRetornarUmaListaUfs_quandoSolicitado() {
        when(ufRepository.findByOrderByNomeAsc())
                .thenReturn(umaListaUf());

        assertThat(ufService.findAll())
            .extracting("value", "label")
            .containsExactly(
                Tuple.tuple(1, "PR"),
                Tuple.tuple(2, "SP")
            );
    }

    @Test
    public void findAllByRegionalId_deveRetornarUmaListaUfs_quandoSolicitado() {
        when(ufRepository.buscarEstadosPorRegional(anyInt()))
            .thenReturn(List.of(Uf.builder().id(1).nome("PR").build()));

        assertThat(ufService.findAllByRegionalId(1027))
            .extracting("value", "label")
            .containsExactly(Tuple.tuple(1, "PR"));
    }

    @Test
    public void findAllByRegionalIdComUf_deveRetornarUmaListaUfs_quandoSolicitado() {
        when(ufRepository.buscarEstadosPorRegional(anyInt()))
            .thenReturn(List.of(Uf.builder().id(1).nome("PARANA").uf("PR").build()));

        assertThat(ufService.findAllByRegionalIdComUf(1027))
            .extracting("id", "nome", "uf")
            .containsExactly(Tuple.tuple(1, "PARANA", "PR"));
    }

    private List<Uf> umaListaUf() {
        return List.of(
                Uf.builder()
                        .id(1)
                        .nome("PR")
                        .build(),
                Uf.builder()
                        .id(2)
                        .nome("SP")
                        .build()
        );
    }
}
