package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UfServiceTest {

    @InjectMocks
    private UfService ufService;
    @Mock
    private UfRepository ufRepository;

    @Test
    public void findAll_umaListaUfs_quandoSolicitado() {
        when(ufRepository.findByOrderByNomeAsc())
            .thenReturn(umaListaUf());

        assertThat(ufService.findAll())
            .extracting("value", "label")
            .containsExactly(
                Tuple.tuple(1, "PR"),
                Tuple.tuple(2, "SP")
            );
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