package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.config.CacheConfig;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CidadeService.class, CacheConfig.class})
public class CidadeServiceIT {

    @Autowired
    private CidadeService service;
    @Autowired
    private CacheManager cacheManager;
    @MockBean
    private CidadeRepository repository;
    @MockBean
    private RegionalService regionalService;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;

    @Test
    public void getCidadesDistritos_deveConsultarDoCache_quandoInformarApenasDistritosComoF() {
        var predicate = new CidadePredicate().comDistritos(Eboolean.F).build();

        service.getCidadesDistritos(Eboolean.F);
        verify(repository).findAllByPredicate(predicate);

        service.getCidadesDistritos(Eboolean.F);
        service.getCidadesDistritos(Eboolean.F);
        verifyNoMoreInteractions(repository);

        service.flushCacheCidadesDistritos();
    }

    @Test
    public void getCidadesDistritos_deveConsultarDoCache_quandoInformarApenasDistritosComoV() {
        var predicate = new CidadePredicate().comDistritos(Eboolean.V).build();
        var predicateCidadesPaiIds = new CidadePredicate().comCidadesId(List.of()).build();

        service.getCidadesDistritos(Eboolean.V);
        verify(repository).findAllByPredicate(predicate);
        verify(repository).findAllByPredicate(predicateCidadesPaiIds);

        service.getCidadesDistritos(Eboolean.V);
        service.getCidadesDistritos(Eboolean.V);
        verifyNoMoreInteractions(repository);

        service.flushCacheCidadesDistritos();
    }

    @Test
    public void getCidadesDistritos_deveConsultarDoCache_quandoInformarApenasDistritosComoNull() {
        var predicate = new CidadePredicate().comDistritos(null).build();

        doReturn(umaListaComCidadesEDistritos())
            .when(repository)
            .findAllByPredicate(predicate);

        var predicateCidadesPaiIds = new CidadePredicate().comCidadesId(umaListaApenasFkCidadeDosDistritos()).build();

        doReturn(umaListaApenasCidades())
            .when(repository)
            .findAllByPredicate(predicateCidadesPaiIds);

        service.getCidadesDistritos(null);
        verify(repository).findAllByPredicate(predicate);
        verify(repository).findAllByPredicate(predicateCidadesPaiIds);

        service.getCidadesDistritos(null);
        service.getCidadesDistritos(null);
        verifyNoMoreInteractions(repository);

        service.flushCacheCidadesDistritos();
    }
}
