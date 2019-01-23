package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolicitacaoRamalService {

    @Autowired
    private SolicitacaoRamalRepository solicitacaoRamalRepository;
    @Autowired
    private SolicitacaoRamalHistoricoService historicoService;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;

    public PageImpl<SolicitacaoRamalResponse> getAll(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        BooleanBuilder builder = filtros.toPredicate().build();

        Integer idUsuario = autenticacaoService.getUsuarioId();

        List<SolicitacaoRamal> solicitacoes = solicitacaoRamalRepository.findAllByUsuarioId(pageable, idUsuario, builder);

        return new PageImpl<>(solicitacoes.stream()
                                          .map(SolicitacaoRamalResponse::convertFrom)
                                          .collect(Collectors.toList()),
                pageable,
                solicitacoes.size());
    }

    public SolicitacaoRamalResponse save(SolicitacaoRamalRequest request) {
        SolicitacaoRamal solicitacaoRamal = SolicitacaoRamalRequest.convertFrom(request);
        solicitacaoRamal.atualizarDataCadastro();

        SolicitacaoRamal solicitacaoRamalPersistida = solicitacaoRamalRepository.save(solicitacaoRamal);

        gerarHistorico(solicitacaoRamalPersistida);

        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalPersistida);
    }

    private void gerarHistorico(SolicitacaoRamal solicitacaoRamal) {
        historicoService.save(new SolicitacaoRamalHistorico().gerarHistorico(solicitacaoRamal));
    }

    public SolicitacaoRamalResponse update(SolicitacaoRamalRequest request) {
        SolicitacaoRamal solicitacaoRamal = SolicitacaoRamalRequest.convertFrom(request);
        SolicitacaoRamal solicitacaoRamal1Persistida = solicitacaoRamalRepository.save(solicitacaoRamal);

        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamal1Persistida);
    }

    public void verificaPermissaoSobreOAgenteAutorizado(Integer agenteAutorizadoId) {
        autenticacaoService.getUsuarioAutenticado()
                           .hasPermissaoSobreOAgenteAutorizado(agenteAutorizadoId, getAgentesAutorizadosIdsDoUsuarioLogado());
    }

    private List<Integer> getAgentesAutorizadosIdsDoUsuarioLogado() {
        Usuario usuario = criaUsuario(autenticacaoService.getUsuarioId());
        return agenteAutorizadoService.getAgentesAutorizadosPermitidos(usuario);
    }

    private Usuario criaUsuario(int idUsuarioAutenticado) {
        return new Usuario(idUsuarioAutenticado);
    }

}
