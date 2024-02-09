package br.com.xbrain.autenticacao.modules.usuario.enums;

import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import br.com.xbrain.autenticacao.modules.usuario.predicate.ConfiguracaoAgendaPredicate;
import br.com.xbrain.autenticacao.modules.usuario.validationgroups.IConfiguracaoAgendaGroupsValidation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
@SuppressWarnings("Indentation") // https://github.com/checkstyle/checkstyle/issues/13539
public enum ETipoConfiguracao {

    CANAL(
        IConfiguracaoAgendaGroupsValidation.Canal.class,
        (model, request) -> model.setCanal(request.getCanal()),
        (predicate, filtros) -> predicate.comCanal(filtros.getCanal())),
    NIVEL(
        IConfiguracaoAgendaGroupsValidation.Nivel.class,
        (model, request) -> model.setNivel(request.getNivel()),
        (predicate, filtros) -> predicate.comNivel(filtros.getNivel())),
    ESTRUTURA(
        IConfiguracaoAgendaGroupsValidation.Canal.AgenteAutorizado.class,
        (model, request) -> model.setEstruturaAa(request.getEstruturaAa()),
        (predicate, filtros) -> predicate.comEstruturaAa(filtros.getEstruturaAa())),
    SUBCANAL(
        IConfiguracaoAgendaGroupsValidation.Canal.D2dProprio.class,
        (model, request) -> model.setSubcanal(request.getSubcanal()),
        (predicate, filtros) -> predicate.comSubCanal(filtros.getSubcanal()));

    private final Class<?> groupValidator;
    private final BiConsumer<ConfiguracaoAgenda, ConfiguracaoAgendaRequest> modelConsumer;
    private final BiConsumer<ConfiguracaoAgendaPredicate, ConfiguracaoAgendaFiltros> predicateConsumer;
}
