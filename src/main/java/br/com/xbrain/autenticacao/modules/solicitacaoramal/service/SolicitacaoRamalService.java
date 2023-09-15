package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.CnpjUtil;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.SocioService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.*;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalHistoricoRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.util.SolicitacaoRamalExpiracaoAdjuster;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.xbrainutils.DateUtils;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.ListUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.PENDENTE;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.CTR_2034;
import static java.util.Comparator.comparing;

@Service
@Slf4j
public class SolicitacaoRamalService {

    private static final String ASSUNTO_EMAIL_CADASTRAR = "Nova Solicitação de Ramal";
    private static final String ASSUNTO_EMAIL_EXPIRAR = "Solicitação de Ramal irá expirar em 24h";
    private static final String TEMPLATE_EMAIL = "solicitacao-ramal";
    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Solicitação não encontrada.");
    private static final String MSG_DEFAULT_PARAM_AA_ID_OBRIGATORIO = "É necessário enviar o parâmetro agente autorizado id.";
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private SocioService socioService;
    @Autowired
    private CallService callService;
    @Autowired
    private SolicitacaoRamalRepository solicitacaoRamalRepository;
    @Autowired
    private SolicitacaoRamalHistoricoService historicoService;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
    @Autowired
    private SolicitacaoRamalHistoricoRepository historicoRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private DataHoraAtual dataHoraAtual;
    @Value("${app-config.email.emails-solicitacao-ramal}")
    private String destinatarios;

    public List<SolicitacaoRamalHistoricoResponse> getAllHistoricoBySolicitacaoId(Integer idSolicitacao) {
        return historicoRepository.findAllBySolicitacaoRamalId(idSolicitacao)
                .stream()
                .map(SolicitacaoRamalHistoricoResponse::convertFrom)
                .collect(Collectors.toList());
    }

    public PageImpl<SolicitacaoRamalResponse> getAllGerencia(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        Page<SolicitacaoRamal> solicitacoes = solicitacaoRamalRepository.findAllGerencia(pageable, getBuild(filtros));

        return new PageImpl<>(solicitacoes.getContent()
                .stream()
                .map(SolicitacaoRamalResponse::convertFrom)
                .collect(Collectors.toList()),
                pageable,
                solicitacoes.getTotalElements());
    }

    public PageImpl<SolicitacaoRamalResponse> getAll(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        validarFiltroAgenteAutorizadoId(filtros);

        Page<SolicitacaoRamal> solicitacoes = solicitacaoRamalRepository.findAll(pageable, getBuild(filtros));

        return new PageImpl<>(solicitacoes.getContent()
                .stream()
                .map(SolicitacaoRamalResponse::convertFrom)
                .collect(Collectors.toList()),
                pageable,
                solicitacoes.getTotalElements());
    }

    private void validarFiltroAgenteAutorizadoId(SolicitacaoRamalFiltros filtros) {
        if (!ObjectUtils.isEmpty(filtros.getAgenteAutorizadoId())) {
            verificaPermissaoSobreOAgenteAutorizado(filtros.getAgenteAutorizadoId());
        } else if (!autenticacaoService.getUsuarioAutenticado().hasPermissao(CTR_2034)) {
            throw new ValidacaoException(MSG_DEFAULT_PARAM_AA_ID_OBRIGATORIO);
        }
    }

    private void verificaPermissaoSobreOAgenteAutorizado(Integer agenteAutorizadoId) {
        autenticacaoService.getUsuarioAutenticado()
                .hasPermissaoSobreOAgenteAutorizado(agenteAutorizadoId, getAgentesAutorizadosIdsDoUsuarioLogado());
    }

    public PageImpl<SolicitacaoRamalResponse> getAllDetalhar(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        hasFiltroAgenteAutorizadoId(filtros);

        Page<SolicitacaoRamal> solicitacoes = solicitacaoRamalRepository.findAll(pageable, getBuild(filtros));

        return new PageImpl<>(solicitacoes.getContent()
                .stream()
                .map(SolicitacaoRamalResponse::convertFrom)
                .collect(Collectors.toList()),
                pageable,
                solicitacoes.getTotalElements());
    }

    private void hasFiltroAgenteAutorizadoId(SolicitacaoRamalFiltros filtros) {
        if (ObjectUtils.isEmpty(filtros.getAgenteAutorizadoId())) {
            throw new ValidacaoException(MSG_DEFAULT_PARAM_AA_ID_OBRIGATORIO);
        }
    }

    private BooleanBuilder getBuild(SolicitacaoRamalFiltros filtros) {
        return filtros.toPredicate().build();
    }

    public SolicitacaoRamalResponse save(SolicitacaoRamalRequest request) {
        validaSalvar(request.getAgenteAutorizadoId());

        SolicitacaoRamal solicitacaoRamal = SolicitacaoRamalRequest.convertFrom(request);
        solicitacaoRamal.atualizarDataCadastro(dataHoraAtual.getDataHora());
        solicitacaoRamal.atualizarUsuario(autenticacaoService.getUsuarioId());

        solicitacaoRamal.atualizarNomeECnpjDoAgenteAutorizado(
                agenteAutorizadoNovoService.getAaById(solicitacaoRamal.getAgenteAutorizadoId())
        );

        solicitacaoRamal.retirarMascara();
        SolicitacaoRamal solicitacaoRamalPersistida = solicitacaoRamalRepository.save(solicitacaoRamal);
        enviarEmailAposCadastro(solicitacaoRamalPersistida);

        gerarHistorico(solicitacaoRamalPersistida, null);

        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalPersistida);
    }

    private void validaSalvar(Integer aaId) {
        if (validarQuantidade(aaId)) {
            throw new ValidacaoException("Não é possível salvar a solicitação de ramal, pois excedeu o limite.");
        } else if (hasSolicitacaoPendenteOuEmAdamentoByAaId(aaId)) {
            throw new ValidacaoException(
                    "Não é possível salvar a solicitação de ramal, pois já existe uma pendente ou em andamento.");
        }
    }

    private boolean hasSolicitacaoPendenteOuEmAdamentoByAaId(Integer aaId) {
        return solicitacaoRamalRepository.findAllByAgenteAutorizadoIdAndSituacaoDiferentePendenteOuEmAndamento(aaId)
                .size() > 0;
    }

    private boolean validarQuantidade(Integer aaId) {
        return solicitacaoRamalRepository.findAllByAgenteAutorizadoIdAndSituacaoEnviadoOuConcluido(aaId)
            .size() >= agenteAutorizadoNovoService.getUsuariosAaAtivoSemVendedoresD2D(aaId).size() ;
    }

    private void gerarHistorico(SolicitacaoRamal solicitacaoRamal, String comentario) {
        historicoService.save(new SolicitacaoRamalHistorico().gerarHistorico(solicitacaoRamal, comentario));
    }

    private List<Integer> getAgentesAutorizadosIdsDoUsuarioLogado() {
        Usuario usuario = usuarioService.findComplete(autenticacaoService.getUsuarioId());
        return agenteAutorizadoNovoService.getAgentesAutorizadosPermitidos(usuario);
    }

    public SolicitacaoRamalResponse update(SolicitacaoRamalRequest request) {
        SolicitacaoRamal solicitacaoEncontrada = findById(request.getId());
        solicitacaoEncontrada.editar(request);
        solicitacaoEncontrada.atualizarUsuario(autenticacaoService.getUsuarioId());
        solicitacaoEncontrada.atualizarNomeECnpjDoAgenteAutorizado(
                agenteAutorizadoNovoService.getAaById(solicitacaoEncontrada.getAgenteAutorizadoId())
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

    public List<SolicitacaoRamalColaboradorResponse> getColaboradoresBySolicitacaoId(Integer solicitacaoId) {
        SolicitacaoRamal solicitacaoRamal = solicitacaoRamalRepository.findBySolicitacaoId(solicitacaoId)
                .orElseThrow(() -> EX_NAO_ENCONTRADO);

        return solicitacaoRamal.getUsuariosSolicitados()
                .stream()
                .map(SolicitacaoRamalColaboradorResponse::convertFrom)
                .sorted(comparing(SolicitacaoRamalColaboradorResponse::getNome))
                .collect(Collectors.toList());
    }

    private void enviarEmailAposCadastro(SolicitacaoRamal solicitacaoRamal) {
        if (!ObjectUtils.isEmpty(solicitacaoRamal)) {
            emailService.enviarEmailTemplate(
                    getDestinatarios(), ASSUNTO_EMAIL_CADASTRAR, TEMPLATE_EMAIL, obterContexto(solicitacaoRamal));
        }
    }

    private Context obterContexto(SolicitacaoRamal solicitacaoRamal) {
        Context context = new Context();
        context.setVariable("dataAtual", DateUtils.parseLocalDateTimeToString(LocalDateTime.now()));
        context.setVariable("codigo", solicitacaoRamal.getId());
        context.setVariable("situacao", solicitacaoRamal.getSituacao());
        context.setVariable("tipoImplantacao", solicitacaoRamal.getTipoImplantacao().getDescricao());
        context.setVariable("melhorDataImplantacao", DateUtils.parseLocalDateToString(
            solicitacaoRamal.getMelhorDataImplantacao()));
        context.setVariable("melhorHoraImplantacao", solicitacaoRamal.getMelhorHorarioImplantacao());
        context.setVariable("qtdRamais", solicitacaoRamal.getQuantidadeRamais());
        context.setVariable("emailTi", solicitacaoRamal.getEmailTi());
        context.setVariable("telefoneTi", solicitacaoRamal.getTelefoneTi());
        context.setVariable("cnpjAa", CnpjUtil.formataCnpj(solicitacaoRamal.getAgenteAutorizadoCnpj()));
        context.setVariable("nomeAa", solicitacaoRamal.getAgenteAutorizadoNome());
        context.setVariable("dataLimite", DateUtils.parseLocalDateTimeToString(
                getDataLimite(solicitacaoRamal.getDataCadastro())));
        context.setVariable("colaboradoresIds", getColaboradoresIds(solicitacaoRamal.getUsuariosSolicitados()));
        return context;
    }

    private List<Integer> getColaboradoresIds(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(Usuario::getId)
                .collect(Collectors.toList());
    }

    public List<SolicitacaoRamal> enviarEmailSolicitacoesQueVaoExpirar() {
        List<SolicitacaoRamal> solicitacoesPendentesOuEmAndamentoQueNaoEnviouEmailAnteriomente =
                getAllSolicitacoesPendenteOuEmAndamentoComEmailExpiracaoFalse();

        solicitacoesPendentesOuEmAndamentoQueNaoEnviouEmailAnteriomente.forEach(solicitacao -> {
            boolean deveEnviarEmail = verificarCasoTenhaQueEnviarEmailDeExpiracao(solicitacao.getDataCadastro());

            if (deveEnviarEmail) {
                enviaEmail(solicitacao);
                updateFlagDataEnviouEmailExpiracao(solicitacao.getId());
            }
        });

        return solicitacoesPendentesOuEmAndamentoQueNaoEnviouEmailAnteriomente;
    }

    private boolean verificarCasoTenhaQueEnviarEmailDeExpiracao(LocalDateTime dataCadastro) {
        LocalDateTime dataLimite = getDataLimite(dataCadastro);

        final int umDiaEmHoras = 24;
        return LocalDateTime.now().isEqual(dataLimite.minusHours(umDiaEmHoras))
                || LocalDateTime.now().isAfter(dataLimite.minusHours(umDiaEmHoras));
    }

    private LocalDateTime getDataLimite(LocalDateTime dataCadastro) {
        return LocalDateTime.from(dataCadastro.with(new SolicitacaoRamalExpiracaoAdjuster()));
    }

    private void enviaEmail(SolicitacaoRamal solicitacaoRamal) {
        Context context = obterContexto(solicitacaoRamal);
        emailService.enviarEmailTemplate(getDestinatarios(), ASSUNTO_EMAIL_EXPIRAR, TEMPLATE_EMAIL, context);
    }

    public List<SolicitacaoRamal> getAllSolicitacoesPendenteOuEmAndamentoComEmailExpiracaoFalse() {
        return solicitacaoRamalRepository.findAllBySituacaoAndDataEnviadoEmailExpiracaoIsNull();
    }

    private void updateFlagDataEnviouEmailExpiracao(Integer solicitacaoId) {
        solicitacaoRamalRepository.updateFlagDataEnviadoEmailExpiracao(LocalDateTime.now(), solicitacaoId);
    }

    private List<String> getDestinatarios() {
        if (this.destinatarios.contains(",")) {
            return Arrays.asList(this.destinatarios.split(","));
        }

        return Collections.singletonList(this.destinatarios);
    }

    public SolicitacaoRamalDadosAdicionaisAaResponse getDadosAgenteAutorizado(Integer agenteAutorizadoId) {
        AgenteAutorizadoResponse agenteAutorizadoResponse = agenteAutorizadoNovoService.getAaById(agenteAutorizadoId);

        return SolicitacaoRamalDadosAdicionaisAaResponse.convertFrom(
                getTelefoniaPelaDiscadoraId(agenteAutorizadoResponse),
                getNomeSocioPrincipalAa(agenteAutorizadoId),
                getQuantidadeUsuariosAtivos(agenteAutorizadoId),
                getQuantidadeRamaisPeloAgenteAutorizadoId(agenteAutorizadoId),
                agenteAutorizadoResponse);
    }

    private String getTelefoniaPelaDiscadoraId(AgenteAutorizadoResponse agenteAutorizado) {
        if (!ObjectUtils.isEmpty(agenteAutorizado.getDiscadoraId())) {
            return callService.obterNomeTelefoniaPorId(agenteAutorizado.getDiscadoraId()).getNome();
        }

        return "";
    }

    private long getQuantidadeRamaisPeloAgenteAutorizadoId(Integer agenteAutorizadoId) {
        return callService.obterRamaisParaAgenteAutorizado(agenteAutorizadoId).size();
    }

    private String getNomeSocioPrincipalAa(Integer agenteAutorizadoId) {
        return socioService.findSocioPrincipalByAaId(agenteAutorizadoId).getNome();
    }

    private long getQuantidadeUsuariosAtivos(Integer agenteAutorizadoId) {
        return agenteAutorizadoService.getUsuariosAaAtivoComVendedoresD2D(agenteAutorizadoId).size();
    }

    @Transactional
    public void remover(Integer solicitacaoId) {
        SolicitacaoRamal solicitacaoRamal = findById(solicitacaoId);

        validaSituacaoPendente(solicitacaoRamal.getSituacao());

        historicoRepository.deleteAll(solicitacaoId);
        solicitacaoRamalRepository.delete(solicitacaoRamal);
    }

    private void validaSituacaoPendente(ESituacaoSolicitacao situacao) {
        if (!situacao.equals(PENDENTE)) {
            throw new ValidacaoException("Só é possível excluir solicitações com status pendente!");
        }
    }

    @Transactional
    public void calcularDataFinalizacao(SolicitacaoRamalFiltros filtros) {
        autenticacaoService.getUsuarioAutenticado().validarAdministrador();

        var solicitacoes = solicitacaoRamalRepository
            .findAllByPredicate(filtros
                .toPredicate()
                .comDataFinalizacaoNula()
                .build());

        if (!ListUtils.isEmpty(solicitacoes)) {
            log.info("Solicitação Ramal: Iniciando calculo de datas de finalização");
            solicitacoes.forEach(SolicitacaoRamal::calcularDataFinalizacao);

            solicitacaoRamalRepository.save(solicitacoes);
            log.info("Solicitação Ramal: Foram atualizadas {} solicitações", solicitacoes.size());
        }
    }
}
