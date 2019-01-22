package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<SolicitacaoRamalResponse> getAll() {
        List<SolicitacaoRamal> solicitacoes = solicitacaoRamalRepository.findAllByUsuarioId(autenticacaoService.getUsuarioId());

        return solicitacoes.stream()
                           .map(solicitacao -> SolicitacaoRamalResponse.convertFrom(solicitacao))
                           .collect(Collectors.toList());
    }

    @Transactional
    public SolicitacaoRamal save(SolicitacaoRamal solicitacaoRamal) {
        solicitacaoRamal.atualizarDataCadastro();
        SolicitacaoRamal solicitacaoRamalPersistida = solicitacaoRamalRepository.save(solicitacaoRamal);

        historicoService.save(new SolicitacaoRamalHistorico().gerarHistorico(solicitacaoRamalPersistida));

        return solicitacaoRamalPersistida;
    }

    @Transactional
    public SolicitacaoRamal update(SolicitacaoRamal solicitacaoRamal) {
        return solicitacaoRamalRepository.save(solicitacaoRamal);
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
