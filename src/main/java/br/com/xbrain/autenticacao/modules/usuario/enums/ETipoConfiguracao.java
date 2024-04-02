package br.com.xbrain.autenticacao.modules.usuario.enums;

import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.predicate.ConfiguracaoAgendaRealPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoAgendaRealRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.ConfiguracaoAgendaRealService;
import br.com.xbrain.autenticacao.modules.usuario.validationgroups.IConfiguracaoAgendaRealGroupsValidation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
@SuppressWarnings("Indentation") // https://github.com/checkstyle/checkstyle/issues/13539
public enum ETipoConfiguracao {

    CANAL(
        IConfiguracaoAgendaRealGroupsValidation.Canal.class,
        (predicate, filtros) -> predicate.comCanal(filtros.getCanal()),
        (repository, request) -> repository.existsByCanal(request.getCanal()),
        ConfiguracaoAgendaRealService::flushCacheConfigCanal),
    NIVEL(
        IConfiguracaoAgendaRealGroupsValidation.Nivel.class,
        (predicate, filtros) -> predicate.comNivel(filtros.getNivel()),
        (repository, request) -> repository.existsByNivel(request.getNivel()),
        ConfiguracaoAgendaRealService::flushCacheConfigNivel),
    ESTRUTURA(
        IConfiguracaoAgendaRealGroupsValidation.Canal.AgenteAutorizado.class,
        (predicate, filtros) -> predicate.comEstruturaAa(filtros.getEstruturaAa()),
        (repository, request) -> repository.existsByEstruturaAa(request.getEstruturaAa()),
        ConfiguracaoAgendaRealService::flushCacheConfigEstrutura),
    SUBCANAL(
        IConfiguracaoAgendaRealGroupsValidation.Canal.D2dProprio.class,
        (predicate, filtros) -> predicate.comSubCanal(filtros.getSubcanalId()),
        (repository, request) -> repository.existsBySubcanalId(request.getSubcanalId()),
        ConfiguracaoAgendaRealService::flushCacheConfigSubcanal),
    PADRAO(
        null, null, null,
        ConfiguracaoAgendaRealService::flushCacheConfigPadrao);

    private final Class<?> groupValidator;
    private final BiConsumer<ConfiguracaoAgendaRealPredicate, ConfiguracaoAgendaFiltros> predicateConsumer;
    private final BiFunction<ConfiguracaoAgendaRealRepository, ConfiguracaoAgendaRequest, Boolean> duplicateValidator;
    private final Consumer<ConfiguracaoAgendaRealService> cacheFlusher;
}
