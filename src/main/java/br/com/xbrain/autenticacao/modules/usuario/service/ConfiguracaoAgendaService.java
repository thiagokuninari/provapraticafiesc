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
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import br.com.xbrain.autenticacao.modules.usuario.predicate.ConfiguracaoAgendaPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoAgendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

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

    public Integer getQtdHorasAdicionaisAgendaByUsuario() {
        return getConfiguracaoAgendaByUsuario()
            .map(ConfiguracaoAgenda::getQtdHorasAdicionais)
            .orElse(VINTE_QUATRO_HORAS);
    }

    public void alterarSituacao(Integer id, ESituacao novaSituacao) {
        repository.findById(id)
            .map(config -> config.alterarSituacao(novaSituacao))
            .map(repository::save)
            .orElseThrow(() -> new ValidacaoException("Configuração de agenda não encontrada."));
    }

    private Optional<ConfiguracaoAgenda> getConfiguracaoAgendaByUsuario() {
        var usuario = autenticacaoService.getUsuarioAutenticado();
        var canal = autenticacaoService.getUsuarioCanal();
        var predicate = isUsuarioComConfigExistenteByNivelAndCanal(usuario, canal)
            ? new ConfiguracaoAgendaPredicate()
                .comNivel(usuario.getNivelCodigoEnum())
                .comCanal(canal)
            : getPredicateByUsuario(usuario, canal);
        return repository.findByPredicateOrderByQtdHorasDesc(predicate.build());
    }

    private ConfiguracaoAgendaPredicate getPredicateByUsuario(UsuarioAutenticado usuario, ECanal canal) {
        var predicate = new ConfiguracaoAgendaPredicate()
            .ouComSubCanais(canal, usuario.getSubCanaisEnum())
            .ouComNivel(usuario.getNivelCodigoEnum())
            .ouComCanal(canal);
        if (canal == ECanal.AGENTE_AUTORIZADO) {
            return predicate.ouComEstruturaAa(aaService.getEstruturaByUsuarioId(usuario.getId()));
        }
        return predicate;
    }

    private boolean isUsuarioComConfigExistenteByNivelAndCanal(UsuarioAutenticado usuario, ECanal canal) {
        return repository.existsByNivelAndCanalAndSituacao(usuario.getNivelCodigoEnum(), canal, ESituacao.A);
    }
}
