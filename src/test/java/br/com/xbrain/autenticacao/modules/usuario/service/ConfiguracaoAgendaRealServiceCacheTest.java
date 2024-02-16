package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.Application;
import br.com.xbrain.autenticacao.config.CacheConfig;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoAgendaRealRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@MockBeans({
    @MockBean(AutenticacaoService.class),
    @MockBean(AgenteAutorizadoNovoService.class)
})
@ContextConfiguration(classes = {
    CacheConfig.class,
    Application.class,
    ConfiguracaoAgendaRealService.class,
    ConfiguracaoAgendaRealRepository.class})
@Sql(scripts = "classpath:/configuracao-agenda-test.sql")
public class ConfiguracaoAgendaRealServiceCacheTest {

    @Autowired
    CacheManager cacheManager;
    @Autowired
    private ConfiguracaoAgendaRealService service;
    @Autowired
    private ConfiguracaoAgendaRealRepository repository;

    @Before
    public void setup() {
        service.flushCacheConfigCanal();
        service.flushCacheConfigNivel();
        service.flushCacheConfigEstrutura();
        service.flushCacheConfigSubcanal();
        service.flushCacheConfigPadrao();

        ReflectionTestUtils.setField(service, "self", service);
    }

    @Test
    public void findQtdHorasAdicionaisByCanal_deveBuscarESalvarNoCache_quandoNaoExistir() {
        assertThat(cacheManager.getCache("horas-adicionais-canal")
            .get(ECanal.AGENTE_AUTORIZADO, Integer.class))
            .isNull();

        repository.findQtdHorasAdicionaisByCanal(ECanal.AGENTE_AUTORIZADO);

        assertThat(cacheManager.getCache("horas-adicionais-canal")
            .get(ECanal.AGENTE_AUTORIZADO, Integer.class))
            .isEqualTo(15);
    }

    @Test
    public void flushCacheByTipoConfig_deveLimparCacheDeCanais_quandoSolicitado() {
        repository.findQtdHorasAdicionaisByCanal(ECanal.AGENTE_AUTORIZADO);
        service.flushCacheByTipoConfig(ETipoConfiguracao.CANAL);

        assertThat(cacheManager.getCache("horas-adicionais-canal")
            .get(ECanal.AGENTE_AUTORIZADO, Integer.class))
            .isNull();
    }

    @Test
    public void findQtdHorasAdicionaisByNivel_deveBuscarESalvarNoCache_quandoNaoExistir() {
        assertThat(cacheManager.getCache("horas-adicionais-nivel")
            .get(CodigoNivel.RECEPTIVO, Integer.class))
            .isNull();

        repository.findQtdHorasAdicionaisByNivel(CodigoNivel.RECEPTIVO);

        assertThat(cacheManager.getCache("horas-adicionais-nivel")
            .get(CodigoNivel.RECEPTIVO, Integer.class))
            .isEqualTo(20);
    }

    @Test
    public void flushCacheByTipoConfig_deveLimparCacheDeNiveis_quandoSolicitado() {
        repository.findQtdHorasAdicionaisByNivel(CodigoNivel.RECEPTIVO);
        service.flushCacheByTipoConfig(ETipoConfiguracao.NIVEL);

        assertThat(cacheManager.getCache("horas-adicionais-nivel")
            .get(CodigoNivel.RECEPTIVO, Integer.class))
            .isNull();
    }

    @Test
    public void findQtdHorasAdicionaisByEstrutura_deveBuscarESalvarNoCache_quandoNaoExistir() {
        assertThat(cacheManager.getCache("horas-adicionais-estrutura")
            .get("AGENTE_AUTORIZADO", Integer.class))
            .isNull();

        repository.findQtdHorasAdicionaisByEstruturaAa("AGENTE_AUTORIZADO");

        assertThat(cacheManager.getCache("horas-adicionais-estrutura")
            .get("AGENTE_AUTORIZADO", Integer.class))
            .isEqualTo(25);
    }

    @Test
    public void flushCacheByTipoConfig_deveLimparCacheDeEstruturas_quandoSolicitado() {
        repository.findQtdHorasAdicionaisByEstruturaAa("AGENTE_AUTORIZADO");
        service.flushCacheByTipoConfig(ETipoConfiguracao.ESTRUTURA);

        assertThat(cacheManager.getCache("horas-adicionais-estrutura")
            .get("AGENTE_AUTORIZADO", Integer.class))
            .isNull();
    }

    @Test
    public void findQtdHorasAdicionaisBySubcanal_deveBuscarESalvarNoCache_quandoNaoExistir() {
        assertThat(cacheManager.getCache("horas-adicionais-subcanal")
            .get(1, Integer.class))
            .isNull();

        repository.findQtdHorasAdicionaisBySubcanal(1);

        assertThat(cacheManager.getCache("horas-adicionais-subcanal")
            .get(1, Integer.class))
            .isEqualTo(30);
    }

    @Test
    public void flushCacheByTipoConfig_deveLimparCacheDeSubcanais_quandoSolicitado() {
        repository.findQtdHorasAdicionaisBySubcanal(1);
        service.flushCacheByTipoConfig(ETipoConfiguracao.SUBCANAL);

        assertThat(cacheManager.getCache("horas-adicionais-subcanal")
            .get(1, Integer.class))
            .isNull();
    }

    @Test
    public void getQtdHorasPadrao_deveBuscarESalvarNoCache_quandoNaoExistir() {
        assertThat(cacheManager.getCache("horas-adicionais-padrao")
            .get("DEFAULT", Integer.class))
            .isNull();

        repository.getQtdHorasPadrao();

        assertThat(cacheManager.getCache("horas-adicionais-padrao")
            .get("DEFAULT", Integer.class))
            .isEqualTo(24);
    }

    @Test
    public void flushCacheByTipoConfig_deveLimparCacheDefault_quandoSolicitado() {
        repository.getQtdHorasPadrao();
        service.flushCacheByTipoConfig(ETipoConfiguracao.PADRAO);

        assertThat(cacheManager.getCache("horas-adicionais-padrao")
            .get("DEFAULT", Integer.class))
            .isNull();
    }
}
