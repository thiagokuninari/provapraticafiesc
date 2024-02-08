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
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoAgendaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.util.StreamUtils.peek;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguracaoAgendaService {

    private static final int VINTE_QUATRO_HORAS = 24;

    private final ConfiguracaoAgendaRepository repository;
    private final AutenticacaoService autenticacaoService;
    private final AgenteAutorizadoNovoService aaService;

    public ConfiguracaoAgendaResponse salvar(ConfiguracaoAgendaRequest request) {
        var configuracaoAgenda = ConfiguracaoAgenda.of(request);
        repository.save(configuracaoAgenda);
        return ConfiguracaoAgendaResponse.of(configuracaoAgenda);
    }

    public Page<ConfiguracaoAgendaResponse> findAll(ConfiguracaoAgendaFiltros filtros, PageRequest pageable) {
        return repository.findAllByPredicate(filtros.toPredicate().build(), pageable)
            .map(ConfiguracaoAgendaResponse::of);
    }

    public void alterarSituacao(Integer id, ESituacao novaSituacao) {
        repository.findById(id)
            .map(peek(config -> config.alterarSituacao(novaSituacao)))
            .map(repository::save)
            .orElseThrow(() -> new ValidacaoException("Configuração de agenda não encontrada."));
    }

    @Transactional(readOnly = true)
    public Integer getQtdHorasAdicionaisAgendaByUsuario(ETipoCanal subcanal) {
        var usuario = autenticacaoService.getUsuarioAutenticado();
        var canal = autenticacaoService.getUsuarioCanal();
        return findQtdHorasBySubcanal(subcanal)
            .or(() -> findQtdHorasByEstruturaAa(usuario, canal))
            .or(() -> findQtdHorasByNivel(usuario))
            .or(() -> findQtdHorasByCanal(canal))
            .orElse(VINTE_QUATRO_HORAS);
    }

    private Optional<Integer> findQtdHorasByEstruturaAa(UsuarioAutenticado usuario, ECanal canal) {
        return canal == ECanal.AGENTE_AUTORIZADO && !usuario.isOperacao()
            ? repository.findQtdHorasAdicionaisByEstruturaAa(aaService.getEstruturaByUsuarioId(usuario.getId()))
            : Optional.empty();
    }

    private Optional<Integer> findQtdHorasBySubcanal(ETipoCanal subcanal) {
        return subcanal != null
            ? repository.findQtdHorasAdicionaisBySubcanal(subcanal)
            : Optional.empty();
    }

    private Optional<Integer> findQtdHorasByNivel(UsuarioAutenticado usuario) {
        return !usuario.isOperacao()
            ? repository.findQtdHorasAdicionaisByNivel(usuario.getNivelCodigoEnum())
            : Optional.empty();
    }

    private Optional<Integer> findQtdHorasByCanal(ECanal canal) {
        return repository.findQtdHorasAdicionaisByCanal(canal);
    }
}
