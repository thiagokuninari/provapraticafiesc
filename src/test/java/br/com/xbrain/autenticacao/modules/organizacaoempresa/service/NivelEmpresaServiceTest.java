package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.NivelEmpresaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.ControllerTestHelper.doisNiveisEmpresa;
import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.ControllerTestHelper.doisNiveisEmpresaSelectResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NivelEmpresaServiceTest {

    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private NivelEmpresaRepository nivelEmpresaRepository;
    @InjectMocks
    private NivelEmpresaService service;

    @Test
    public void getAllNivelEmpresa_deveRetornarListaVazia_quandoNaoExistirNivelEmpresa() {
        when(nivelEmpresaRepository.findAll())
            .thenReturn(List.of());

        assertThat(service.getAllNivelEmpresa())
            .isEmpty();

        verify(nivelEmpresaRepository, times(1)).findAll();
    }

    @Test
    public void getAllNivelEmpresa_deveRetornarNiveisEmpresa_quandoExistirem() {
        when(nivelEmpresaRepository.findAll())
            .thenReturn(doisNiveisEmpresa());

        assertThat(service.getAllNivelEmpresa())
            .isEqualTo(doisNiveisEmpresaSelectResponse())
            .hasSize(2);

        verify(nivelEmpresaRepository, times(1)).findAll();
    }
}
