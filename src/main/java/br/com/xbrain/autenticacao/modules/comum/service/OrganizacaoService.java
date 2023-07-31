package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.OrganizacaoResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.filtros.OrganizacaoFiltros;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.repository.OrganizacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrganizacaoService {

    private static final ValidacaoException NAO_ENCONTRADO = new ValidacaoException("Organização não encontrada.");

    private final OrganizacaoRepository repository;
    private final AutenticacaoService autenticacaoService;

    public List<Organizacao> getAllSelect(OrganizacaoFiltros filtros) {
        return repository.findByPredicate(getFiltros(filtros).toPredicate());
    }

    private OrganizacaoFiltros getFiltros(OrganizacaoFiltros filtros) {
        filtros = Objects.isNull(filtros) ? new OrganizacaoFiltros() : filtros;
        var usuario = autenticacaoService.getUsuarioAutenticado();
        if (usuario.isBackoffice()) {
            filtros.setOrganizacaoId(usuario.getOrganizacaoId());
        }
        return filtros;
    }

    public OrganizacaoResponse getById(Integer id) {
        return OrganizacaoResponse.of(repository.findById(id).orElseThrow(() -> NAO_ENCONTRADO));
    }
}
