package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
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
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone.*;
import static org.assertj.core.api.Assertions.*;
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

    @Test
    public void getAllAtivos_listaComTresSites_quandoBuscarSitesAtivos() {
        when(siteRepository.findBySituacaoAtiva())
            .thenReturn(umListaSites());

        assertThat(service.getAllAtivos())
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "Site Brando Big"),
                tuple(2, "Site Dinossauro do Acre"),
                tuple(3, "Site Amazonia Queimada")
            );
    }

    @Test
    public void getAllSupervisoresBySiteId_umaListaComDoisSupervisores_quandoBuscarSupervisoresPeloSiteId() {
        var site = umSite(1, "TESTE SITE", BRT);
        site.setSupervisores(umaListaSupervisores());

        when(siteRepository.findById(1))
            .thenReturn(Optional.of(site));

        assertThat(service.getAllSupervisoresBySiteId(1))
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "RENATO"),
                tuple(2, "MARIA")
            );
    }

    private Set<Usuario> umaListaSupervisores() {
        return Set.of(
            Usuario.builder()
                .id(1)
                .nome("RENATO")
                .build(),
            Usuario.builder()
                .id(2)
                .nome("MARIA")
                .build()
        );
    }

    @Test
    public void getSitesByEstadoId_umaListaComTresSites_quandoBuscarSitesPeloEstadoId() {
        when(siteRepository.findByEstadoId(1))
            .thenReturn(umListaSites());

        assertThat(service.getSitesByEstadoId(1))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "Site Brando Big"),
                tuple(2, "Site Dinossauro do Acre"),
                tuple(3, "Site Amazonia Queimada")
            );
    }
}
