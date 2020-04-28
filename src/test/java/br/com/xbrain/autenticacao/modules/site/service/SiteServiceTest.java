package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class SiteServiceTest {

    @InjectMocks
    private SiteService service;
    @Mock
    private SiteRepository siteRepository;
    @Mock
    private UfRepository ufRepository;
    @Mock
    private CidadeRepository cidadeRepository;

    @Test
    public void findById_notFoundException_quandoNaoExistirSiteCadastrado() {
        when(siteRepository.findById(Mockito.any()))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.findById(1))
            .withMessageContaining("Site não encontrado.");

        verify(siteRepository, atLeastOnce()).findById(eq(1));
    }

    @Test
    public void findById_deveRetornarUmSite_quandoBuscarPorId() {
        when(siteRepository.findById(Mockito.any()))
            .thenReturn(Optional.of(umSite(1, "nome", BRT)));

        Assertions.assertThat(service.findById(1))
            .extracting("id", "nome", "timeZone")
            .containsExactly(1, "Nome", BRT);

        verify(siteRepository, atLeastOnce()).findById(eq(1));
    }

    public List<Site> umListaSites() {
        return List.of(
            umSite(1, "Site Brando Big", BRT),
            umSite(2, "Site Dinossauro do Acre", ACT),
            umSite(3, "Site Amazonia Queimada", AMT)
        );
    }

    private Site umSite(Integer id, String nome, ETimeZone timeZone) {
        return Site.builder()
            .id(id)
            .nome(nome)
            .timeZone(timeZone)
            .build();
    }
}
