package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoAgendaRealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.util.StreamUtils.peek;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class ConfiguracaoAgendaRealService {

    private final ConfiguracaoAgendaRealRepository repository;
    private final AutenticacaoService autenticacaoService;
    private final AgenteAutorizadoNovoService aaService;
    private final ConfiguracaoAgendaRealService self;

    public ConfiguracaoAgendaResponse salvar(ConfiguracaoAgendaRequest request) {
        request.validarNivelOperacao();
        validarConfiguracaoExistente(request);
        var configuracaoAgenda = ConfiguracaoAgendaReal.of(request, autenticacaoService.getUsuarioAutenticado());
        repository.save(configuracaoAgenda);
        flushCacheByTipoConfig(configuracaoAgenda.getTipoConfiguracao());
        return ConfiguracaoAgendaResponse.of(configuracaoAgenda);
    }

    public void atualizar(Integer id, Integer qtdHoras) {
        var config = findById(id);
        config.setQtdHorasAdicionais(qtdHoras);
        repository.save(config);
        flushCacheByTipoConfig(config.getTipoConfiguracao());
    }

    public Page<ConfiguracaoAgendaResponse> findAll(ConfiguracaoAgendaFiltros filtros, PageRequest pageable) {
        return repository.findAllByPredicate(filtros.toPredicate().build(), pageable)
            .map(ConfiguracaoAgendaResponse::of);
    }

    public void alterarSituacao(Integer id, ESituacao novaSituacao) {
        repository.findById(id)
            .map(peek(ConfiguracaoAgendaReal::validarConfiguracaoPadrao))
            .map(peek(config -> config.alterarSituacao(novaSituacao)))
            .map(repository::save)
            .map(peek(config -> flushCacheByTipoConfig(config.getTipoConfiguracao())))
            .orElseThrow(() -> new ValidacaoException("Configuração de agenda não encontrada."));
    }

    public Integer getQtdHorasAdicionaisAgendaByUsuario(Integer subcanalId, Integer aaId) {
        var usuario = autenticacaoService.getUsuarioAutenticado();
        var canal = autenticacaoService.getUsuarioCanal();
        return findQtdHorasBySubcanal(usuario, subcanalId)
            .or(() -> findQtdHorasByEstruturaAa(usuario, aaId))
            .or(() -> findQtdHorasByNivel(usuario))
            .or(() -> findQtdHorasByCanal(usuario, canal))
            .orElse(repository.getQtdHorasPadrao());
    }

    private Optional<Integer> findQtdHorasByEstruturaAa(UsuarioAutenticado usuario, Integer aaId) {
        if (aaId != null && !usuario.isOperacao() && usuario.isAgenteAutorizado()) {
            var estruturaAa = usuario.isSocioPrincipal()
                ? aaService.getEstruturaByAgenteAutorizadoId(aaId)
                : autenticacaoService.getTokenProperty("estruturaAa", String.class)
                    .or(() -> aaService.getEstruturaByAgenteAutorizadoId(aaId));
            return estruturaAa.flatMap(repository::findQtdHorasAdicionaisByEstruturaAa);
        }
        return Optional.empty();
    }

    private Optional<Integer> findQtdHorasBySubcanal(UsuarioAutenticado usuario, Integer subcanalId) {
        return subcanalId != null && usuario.isOperacao()
            ? repository.findQtdHorasAdicionaisBySubcanal(subcanalId)
            : Optional.empty();
    }

    private Optional<Integer> findQtdHorasByNivel(UsuarioAutenticado usuario) {
        return !usuario.isOperacao()
            ? repository.findQtdHorasAdicionaisByNivel(usuario.getNivelCodigoEnum())
            : Optional.empty();
    }

    private Optional<Integer> findQtdHorasByCanal(UsuarioAutenticado usuario, ECanal canal) {
        return usuario.isOperacao()
            ? repository.findQtdHorasAdicionaisByCanal(canal)
            : Optional.empty();
    }

    private ConfiguracaoAgendaReal findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> new ValidacaoException("Configuração de agenda não encontrada."));
    }

    private void validarConfiguracaoExistente(ConfiguracaoAgendaRequest request) {
        if (request.getTipoConfiguracao().getDuplicateValidator().apply(repository, request)) {
            throw new ValidacaoException("Não é possível salvar uma configuração já existente.");
        }
    }

    public void flushCacheByTipoConfig(ETipoConfiguracao tipoConfiguracao) {
        tipoConfiguracao.getCacheFlusher().accept(self);
    }

    @CacheEvict(cacheNames = "horas-adicionais-canal", allEntries = true)
    public void flushCacheConfigCanal() {
        log.info("Flush cache horas-adicionais-canal");
    }

    @CacheEvict(cacheNames = "horas-adicionais-nivel", allEntries = true)
    public void flushCacheConfigNivel() {
        log.info("Flush cache horas-adicionais-nivel");
    }

    @CacheEvict(cacheNames = "horas-adicionais-estrutura", allEntries = true)
    public void flushCacheConfigEstrutura() {
        log.info("Flush cache horas-adicionais-estrutura");
    }

    @CacheEvict(cacheNames = "horas-adicionais-subcanal", allEntries = true)
    public void flushCacheConfigSubcanal() {
        log.info("Flush cache horas-adicionais-subcanal");
    }

    @CacheEvict(cacheNames = "horas-adicionais-padrao", allEntries = true)
    public void flushCacheConfigPadrao() {
        log.info("Flush cache horas-adicionais-padrao");
    }
}
