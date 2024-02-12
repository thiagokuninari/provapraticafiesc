package br.com.xbrain.autenticacao.modules.usuario.enums;

import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import br.com.xbrain.autenticacao.modules.usuario.predicate.ConfiguracaoAgendaRealPredicate;
import br.com.xbrain.autenticacao.modules.usuario.validationgroups.IConfiguracaoAgendaRealGroupsValidation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
@SuppressWarnings("Indentation") // https://github.com/checkstyle/checkstyle/issues/13539
public enum ETipoConfiguracao {

    CANAL(
        IConfiguracaoAgendaRealGroupsValidation.Canal.class,
        (model, request) -> model.setCanal(request.getCanal()),
        (predicate, filtros) -> predicate.comCanal(filtros.getCanal()),
        (response, model) -> response.setCanal(model.getCanal().getDescricao())),
    NIVEL(
        IConfiguracaoAgendaRealGroupsValidation.Nivel.class,
        (model, request) -> model.setNivel(request.getNivel()),
        (predicate, filtros) -> predicate.comNivel(filtros.getNivel()),
        (response, model) -> response.setNivel(model.getNivel())),
    ESTRUTURA(
        IConfiguracaoAgendaRealGroupsValidation.Canal.AgenteAutorizado.class,
        (model, request) -> model.setEstruturaAa(request.getEstruturaAa()),
        (predicate, filtros) -> predicate.comEstruturaAa(filtros.getEstruturaAa()),
        (response, model) -> response.setEstruturaAa(model.getEstruturaAa())),
    SUBCANAL(
        IConfiguracaoAgendaRealGroupsValidation.Canal.D2dProprio.class,
        (model, request) -> model.setSubcanalId(request.getSubcanalId()),
        (predicate, filtros) -> predicate.comSubCanal(filtros.getSubcanalId()),
        (response, model) -> response.setSubcanal(ETipoCanal.valueOf(model.getSubcanalId()).getDescricao()));

    private final Class<?> groupValidator;
    private final BiConsumer<ConfiguracaoAgendaReal, ConfiguracaoAgendaRequest> modelConsumer;
    private final BiConsumer<ConfiguracaoAgendaRealPredicate, ConfiguracaoAgendaFiltros> predicateConsumer;
    private final BiConsumer<ConfiguracaoAgendaResponse, ConfiguracaoAgendaReal> responseConsumer;
}
