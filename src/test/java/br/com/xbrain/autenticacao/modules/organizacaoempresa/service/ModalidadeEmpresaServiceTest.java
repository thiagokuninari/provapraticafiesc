package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.ModalidadeEmpresaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.ControllerTestHelper.duasModalidadesEmpresa;
import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.ControllerTestHelper.duasModalidadesEmpresaSelectResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModalidadeEmpresaServiceTest {

    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private ModalidadeEmpresaRepository modalidadeEmpresaRepository;
    @InjectMocks
    private ModalidadeEmpresaService service;

    @Test
    public void getAllModalidadeEmpresa_deveRetornarListaVazia_quandoNaoExistirModalidadeEmpresa() {
        when(modalidadeEmpresaRepository.findAll())
            .thenReturn(List.of());

        assertThat(service.getAllModalidadeEmpresa())
            .isEmpty();

        verify(modalidadeEmpresaRepository, times(1)).findAll();
    }

    @Test
    public void getAllModalidadeEmpresa_deveRetornarModalidadeEmpresa_quandoExistirem() {
        when(modalidadeEmpresaRepository.findAll())
            .thenReturn(duasModalidadesEmpresa());

        assertThat(service.getAllModalidadeEmpresa())
            .isEqualTo(duasModalidadesEmpresaSelectResponse())
            .hasSize(2);

        verify(modalidadeEmpresaRepository, times(1)).findAll();
    }
}
