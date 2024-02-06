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
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import br.com.xbrain.autenticacao.modules.usuario.predicate.ConfiguracaoAgendaPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoAgendaRepository;
import jdk.jfr.Label;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfiguracaoAgendaService {

    private static final int VINTE_QUATRO_HORAS = 24;

    private final ConfiguracaoAgendaRepository repository;
    private final AutenticacaoService autenticacaoService;
    private final AgenteAutorizadoNovoService aaService;

    public ConfiguracaoAgendaResponse salvar(ConfiguracaoAgendaRequest request) {
        var configuracaoAgenda = ConfiguracaoAgenda.of(request);
        configuracaoAgenda.setSituacao(ESituacao.A);
        repository.save(configuracaoAgenda);
        return ConfiguracaoAgendaResponse.of(configuracaoAgenda);
    }

    public Page<ConfiguracaoAgendaResponse> findAll(ConfiguracaoAgendaFiltros filtros, PageRequest pageable) {
        return repository.findAllByPredicate(filtros.toPredicate().build(), pageable)
            .map(ConfiguracaoAgendaResponse::of);
    }

    public void alterarSituacao(Integer id, ESituacao novaSituacao) {
        repository.findById(id)
            .map(config -> config.alterarSituacao(novaSituacao))
            .map(repository::save)
            .orElseThrow(() -> new ValidacaoException("Configuração de agenda não encontrada."));
    }

    @Transactional(readOnly = true)
    public Integer getQtdHorasAdicionaisAgendaByUsuario(ETipoCanal subcanal) {
        return getConfiguracaoAgendaByUsuario(subcanal)
            .map(ConfiguracaoAgenda::getQtdHorasAdicionais)
            .orElse(VINTE_QUATRO_HORAS);
    }

    private Optional<ConfiguracaoAgenda> getConfiguracaoAgendaByUsuario(ETipoCanal subcanal) {
        var usuario = autenticacaoService.getUsuarioAutenticado();
        var canal = autenticacaoService.getUsuarioCanal();
        var configuracao = findBySubcanal(subcanal)
            .orElse(findByEstruturaAa(usuario, canal)
                .orElse(findByNivel(usuario)
                    .orElse(findByCanal(canal)
                        .orElse(null))));
        return Optional.ofNullable(configuracao);
    }

    public Optional<ConfiguracaoAgenda> findBySubcanal(ETipoCanal subcanal) {
        if (subcanal != null) {
            return repository.findFirstBySubcanalAndSituacaoOrderByQtdHorasAdicionaisDesc(subcanal, ESituacao.A);
        }
        return Optional.empty();
    }

    private Optional<ConfiguracaoAgenda> findByEstruturaAa(UsuarioAutenticado usuario, ECanal canal) {
        if (canal == ECanal.AGENTE_AUTORIZADO) {
            var estruturaAa = aaService.getEstruturaByUsuarioId(usuario.getId());
            return repository.findFirstByEstruturaAaAndSituacaoOrderByQtdHorasAdicionaisDesc(estruturaAa, ESituacao.A);
        }
        return Optional.empty();
    }

    private Optional<ConfiguracaoAgenda> findByNivel(UsuarioAutenticado usuario) {
        if (!usuario.isOperacao()) {
            return repository.findFirstByNivelAndSituacaoOrderByQtdHorasAdicionaisDesc(usuario.getNivelCodigoEnum(), ESituacao.A);
        }
        return Optional.empty();
    }

    private Optional<ConfiguracaoAgenda> findByCanal(ECanal canal) {
        return repository.findFirstByCanalAndSituacaoOrderByQtdHorasAdicionaisDesc(canal, ESituacao.A);
    }
}
