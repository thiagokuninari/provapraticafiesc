package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import com.querydsl.core.BooleanBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static helpers.CidadeHelper.umaListaCidade;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class CidadeServiceTest {

    @Autowired
    private CidadeService cidadeService;
    @MockBean
    private CidadeRepository cidadeRepository;

    @Test
    public void getCidadesByEstadosId_deveRetornarListaCidadeResponse_seHouverCidades() {
        Mockito.when(cidadeRepository.findAll(any(BooleanBuilder.class), any(Sort.class))).thenReturn(umaListaCidade());

        Assertions.assertThat(cidadeService.getCidadesByUfsId(eq((anyList()))))
            .hasSize(2)
            .extracting("id", "nome", "codigoIbge", "uf.id", "uf.nome", "uf.uf", "netUno")
            .containsExactly(
                tuple(1, "CIDADEUM", null, 1, "UFUM", "UF1", null),
                tuple(2, "CIDADEDOIS", null, 2, "UFDOIS", "UF2", null)
            );
    }
}
