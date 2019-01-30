package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.util.CnpjUtil;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.*;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalHistoricoRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.util.TemplateDefaultEnviarEmailSolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolicitacaoRamalService extends TemplateDefaultEnviarEmailSolicitacaoRamal {

    @Autowired
    private SolicitacaoRamalRepository solicitacaoRamalRepository;
    @Autowired
    private SolicitacaoRamalHistoricoService historicoService;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private SolicitacaoRamalHistoricoRepository historicoRepository;
    @Autowired
    private EmailService emailService;

    private static final int DURACAO_DIA_EM_HORAS = 24;
    private static final int EXPIRACAO_EM_HORAS_SOLICITACAO_RAMAL = 72;
    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Solicitação não encontrada.");

    public List<SolicitacaoRamalHistoricoResponse> getAllHistoricoBySolicitacaoId(Integer idSolicitacao) {
        return historicoRepository.findAllBySolicitacaoRamalId(idSolicitacao)
                .stream()
                .map(SolicitacaoRamalHistoricoResponse::convertFrom)
                .collect(Collectors.toList());
    }

    public PageImpl<SolicitacaoRamalResponse> getAll(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        if (!ObjectUtils.isEmpty(filtros.getAgenteAutorizadoId())) {
            verificaPermissaoSobreOAgenteAutorizado(filtros.getAgenteAutorizadoId());
        }

        BooleanBuilder builder = filtros.toPredicate().build();

        Integer idUsuario = autenticacaoService.getUsuarioId();

        List<SolicitacaoRamal> solicitacoes = solicitacaoRamalRepository.findAllByUsuarioId(pageable, idUsuario, builder);

        return new PageImpl<>(solicitacoes.stream()
                                          .map(SolicitacaoRamalResponse::convertFrom)
                                          .collect(Collectors.toList()),
                pageable,
                solicitacoes.size());
    }

    private void verificaPermissaoSobreOAgenteAutorizado(Integer agenteAutorizadoId) {
        autenticacaoService.getUsuarioAutenticado()
                .hasPermissaoSobreOAgenteAutorizado(agenteAutorizadoId, getAgentesAutorizadosIdsDoUsuarioLogado());
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
        enviarEmailAposCadastro(solicitacaoRamalPersistida);

        gerarHistorico(solicitacaoRamalPersistida, null);

        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalPersistida);
    }

    private void gerarHistorico(SolicitacaoRamal solicitacaoRamal, String comentario) {
        historicoService.save(new SolicitacaoRamalHistorico().gerarHistorico(solicitacaoRamal, comentario));
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
        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalRepository.save(solicitacaoEncontrada));
    }

    public SolicitacaoRamalResponse atualizarStatus(SolicitacaoRamalAtualizarStatusRequest request) {
        SolicitacaoRamal solicitacaoEncontrada = findById(request.getIdSolicitacao());
        solicitacaoEncontrada.setSituacao(request.getSituacao());
        gerarHistorico(solicitacaoEncontrada, request.getObservacao());

        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalRepository.save(solicitacaoEncontrada));
    }

    public SolicitacaoRamalResponse getSolicitacaoById(Integer idSolicitacao) {
        return SolicitacaoRamalResponse.convertFrom(findById(idSolicitacao));
    }

    private SolicitacaoRamal findById(Integer id) {
        return solicitacaoRamalRepository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    private Usuario criaUsuario(int idUsuarioAutenticado) {
        return new Usuario(idUsuarioAutenticado);
    }

    public void enviarEmailAposCadastro(SolicitacaoRamal solicitacaoRamal) {
        if (!ObjectUtils.isEmpty(solicitacaoRamal)) {
            emailService.enviarEmailTemplate(
                    DESTINATARIOS, ASSUNTO_EMAIL_CADASTRAR, TEMPLATE_EMAIL, obterContexto(solicitacaoRamal));
        }
    }

    @Override
    public Context obterContexto(SolicitacaoRamal solicitacaoRamal) {
        Context context = new Context();
        context.setVariable("dataAtual", DateUtil.dateTimeToString(LocalDateTime.now()));
        context.setVariable("codigo",  solicitacaoRamal.getId());
        context.setVariable("situacao", solicitacaoRamal.getSituacao());
        context.setVariable("qtdRamais",  solicitacaoRamal.getQuantidadeRamais());
        context.setVariable("emailTi", solicitacaoRamal.getEmailTi());
        context.setVariable("telefoneTi",  solicitacaoRamal.getTelefoneTi());
        context.setVariable("cnpjAa", CnpjUtil.formataCnpj(solicitacaoRamal.getAgenteAutorizadoCnpj()));
        context.setVariable("nomeAa", solicitacaoRamal.getAgenteAutorizadoNome());
        context.setVariable("dataLimite", DateUtil.dateTimeToString(
                solicitacaoRamal.getDataCadastro().plusHours(EXPIRACAO_EM_HORAS_SOLICITACAO_RAMAL)));
        return context;
    }

    public List<SolicitacaoRamal> enviadorDeEmailParaSolicitacoesQueVaoExpirar() {
        List<SolicitacaoRamal> solicitacoesPendentesOuEmAndamentoQueNaoEnviouEmailAnteriomente =
                getAllSolicitacoesPendenteOuEmAndamentoComEmailExpiracaoFalse();

        solicitacoesPendentesOuEmAndamentoQueNaoEnviouEmailAnteriomente.forEach(solicitacao -> {
            boolean deveEnviarEmail = verificarCasoTenhaQueEnviarEmailDeExpiracao(solicitacao.getDataCadastro());

            if (deveEnviarEmail) {
                enviaEmail(solicitacao);
                atualizaFlagEnviouEmailExpiracao(solicitacao.getId());
            }
        });

        return solicitacoesPendentesOuEmAndamentoQueNaoEnviouEmailAnteriomente;
    }

    private boolean verificarCasoTenhaQueEnviarEmailDeExpiracao(LocalDateTime dataCadastro) {
        LocalDateTime dataLimite = getDataExpiracao(dataCadastro);

        return LocalDate.now().isEqual(dataLimite.toLocalDate())
                || LocalDate.now().isAfter(dataLimite.toLocalDate());
    }

    private LocalDateTime getDataExpiracao(LocalDateTime dataCadastro) {
        return dataCadastro.plusHours(DURACAO_DIA_EM_HORAS);
    }

    private void enviaEmail(SolicitacaoRamal solicitacaoRamal) {
        Context context = obterContexto(solicitacaoRamal);
        emailService.enviarEmailTemplate(DESTINATARIOS, ASSUNTO_EMAIL_EXPIRAR, TEMPLATE_EMAIL, context);
    }

    private void atualizaFlagEnviouEmailExpiracao(Integer solicitacaoId) {
        this.updateFlagEnviouEmailExpirado(solicitacaoId);
    }

    public List<SolicitacaoRamal> getAllSolicitacoesPendenteOuEmAndamentoComEmailExpiracaoFalse() {
        return solicitacaoRamalRepository.findAllBySituacaoPendenteOrEmAndamentoAndEnviouEmailExpiracaoFalse();
    }

    @Transactional
    public void updateFlagEnviouEmailExpirado(Integer solicitacaoId) {
        solicitacaoRamalRepository.updateFlagEnviouEmailExpirado(solicitacaoId);
    }

}
