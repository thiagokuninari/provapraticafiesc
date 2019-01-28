package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
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
import org.springframework.data.domain.Page;
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

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Solicitação não encontrada.");

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

    public PageImpl<SolicitacaoRamalResponse> getAllGerencia(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        BooleanBuilder builder = filtros.toPredicate().build();

        Page<SolicitacaoRamal> solicitacoes = solicitacaoRamalRepository.findAll(pageable, builder);

        return new PageImpl<>(
                solicitacoes.getContent()
                            .stream()
                            .map(SolicitacaoRamalResponse::convertFrom)
                            .collect(Collectors.toList()),
                pageable,
                solicitacoes.getTotalElements());
    }

    public SolicitacaoRamalResponse save(SolicitacaoRamalRequest request) {
        SolicitacaoRamal solicitacaoRamal = SolicitacaoRamalRequest.convertFrom(request);
        solicitacaoRamal.atualizarDataCadastro();
        solicitacaoRamal.atualizarUsuario(autenticacaoService.getUsuarioId());

        solicitacaoRamal.atualizarNomeECnpjDoAgenteAutorizado(
                agenteAutorizadoService.getAaById(solicitacaoRamal.getAgenteAutorizadoId())
        );

        solicitacaoRamal.retirarMascara();
        SolicitacaoRamal solicitacaoRamalPersistida = solicitacaoRamalRepository.save(solicitacaoRamal);

        gerarHistorico(solicitacaoRamalPersistida);

        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalPersistida);
    }

    private void gerarHistorico(SolicitacaoRamal solicitacaoRamal) {
        historicoService.save(new SolicitacaoRamalHistorico().gerarHistorico(solicitacaoRamal));
    }

    public void verificaPermissaoSobreOAgenteAutorizado(Integer agenteAutorizadoId) {
        autenticacaoService.getUsuarioAutenticado()
                           .hasPermissaoSobreOAgenteAutorizado(agenteAutorizadoId, getAgentesAutorizadosIdsDoUsuarioLogado());
    }

    private List<Integer> getAgentesAutorizadosIdsDoUsuarioLogado() {
        Usuario usuario = criaUsuario(autenticacaoService.getUsuarioId());
        return agenteAutorizadoService.getAgentesAutorizadosPermitidos(usuario);
    }

    public SolicitacaoRamalResponse update(SolicitacaoRamalRequest request) {
        SolicitacaoRamal solicitacaoEncontrada = findById(request.getId());
        solicitacaoEncontrada.editar(request);
        solicitacaoEncontrada.atualizarUsuario(autenticacaoService.getUsuarioId());
        solicitacaoEncontrada.atualizarNomeECnpjDoAgenteAutorizado(
                agenteAutorizadoService.getAaById(solicitacaoEncontrada.getAgenteAutorizadoId())
        );

        solicitacaoEncontrada.retirarMascara();
        SolicitacaoRamal solicitacaoRamalPersistida = solicitacaoRamalRepository.save(solicitacaoEncontrada);

        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalPersistida);
    }

    private SolicitacaoRamal findById(Integer id) {
        return solicitacaoRamalRepository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    private Usuario criaUsuario(int idUsuarioAutenticado) {
        return new Usuario(idUsuarioAutenticado);
    }

}
