package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static br.com.xbrain.autenticacao.modules.permissao.helpers.PermissaoEspecialHelper.umDtoNovoSocioPrincipal;
import static br.com.xbrain.autenticacao.modules.permissao.helpers.PermissaoEspecialHelper.umaListaPermissoesEspeciaisFuncFeederEAcompIndTecVend;
import static br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService.FUNC_FEEDER_E_ACOMP_INDICACOES_TECNICO_VENDEDOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PermissaoEspecialServiceTest {

    @InjectMocks
    private PermissaoEspecialService service;
    @Mock
    private PermissaoEspecialRepository repository;
    @Captor
    private ArgumentCaptor<PermissaoEspecial> permissaoEspecialCaptor;

    @Test
    public void atualizarPermissoesEspeciaisNovoSocioPrincipal_deveAtualizarAsPermissoesDoNovoSocio_quandoSolicitado() {
        doReturn(umaListaPermissoesEspeciaisFuncFeederEAcompIndTecVend())
            .when(repository)
            .findAllByFuncionalidadeIdInAndUsuarioIdAndDataBaixaIsNull(FUNC_FEEDER_E_ACOMP_INDICACOES_TECNICO_VENDEDOR, 32);

        assertThatCode(() -> service
            .atualizarPermissoesEspeciaisNovoSocioPrincipal(umDtoNovoSocioPrincipal(32)))
            .doesNotThrowAnyException();

        verify(repository)
            .findAllByFuncionalidadeIdInAndUsuarioIdAndDataBaixaIsNull(FUNC_FEEDER_E_ACOMP_INDICACOES_TECNICO_VENDEDOR, 32);
        verify(repository, times(5))
            .save(permissaoEspecialCaptor.capture());

        assertThat(permissaoEspecialCaptor.getAllValues())
            .extracting("funcionalidade.id")
            .containsExactlyInAnyOrder(3046, 15000, 15005, 15012, 16101);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void atualizarPermissoesEspeciaisNovoSocioPrincipal_naoDeveAtualizarAsPermissoesDoSocio_seAntigosSociosPrincipaisForVazio() {
        assertThatCode(() -> service
            .atualizarPermissoesEspeciaisNovoSocioPrincipal(umDtoNovoSocioPrincipal()))
            .doesNotThrowAnyException();

        verifyZeroInteractions(repository);
    }
}
