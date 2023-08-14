package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.CnpjUtil;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.*;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalHistoricoRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.util.SolicitacaoRamalExpiracaoAdjuster;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.xbrainutils.DateUtils;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.ListUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.PENDENTE;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.CTR_2034;
import static java.util.Comparator.comparing;

@Service
@Slf4j
public class SolicitacaoRamalService {

    public static final ValidacaoException ERRO_SEM_TIPO_CANAL_D2D =
        new ValidacaoException("Tipo de canal obrigatório para o canal D2D");
    public static final ValidacaoException ERRO_SEM_AGENTE_AUTORIZADO =
        new ValidacaoException("agenteAutorizadoId obrigatório para o cargo agente autorizado");
    public static final ValidacaoException SOLICITACAO_PENDENTE_OU_ANDAMENTO = new ValidacaoException(
        "Não é possível salvar a solicitação de ramal, pois já existe uma pendente ou em andamento.");
    public static final ValidacaoException SEM_AUTORIZACAO = new ValidacaoException(
        "Sem autorização para fazer uma solicitação para este canal.");
    public static final String ASSUNTO_EMAIL_EXPIRAR = "Solicitação de Ramal irá expirar em 24h";
    public static final String ASSUNTO_EMAIL_CADASTRAR = "Nova Solicitação de Ramal";
    public static final String TEMPLATE_EMAIL = "solicitacao-ramal";
    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Solicitação não encontrada.");
    private static final String MSG_DEFAULT_PARAM_OBRIGATORIO =
        "Campo agente autorizado é obrigatório";

    @Autowired
    private SolicitacaoRamalServiceAa serviceAa;
    @Autowired
    private SolicitacaoRamalRepository solicitacaoRamalRepository;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private SolicitacaoRamalHistoricoService historicoService;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private SolicitacaoRamalHistoricoRepository historicoRepository;
    @Autowired
    private EmailService emailService;
    @Value("${app-config.email.emails-solicitacao-ramal}")
    private String destinatarios;

    private final Map<ECanal, Class<? extends ISolicitacaoRamalService>> solicitacaoRamalService = ImmutableMap.of(
        ECanal.D2D_PROPRIO, SolicitacaoRamalServiceD2d.class,
        ECanal.AGENTE_AUTORIZADO, SolicitacaoRamalServiceAa.class
    );

    public List<SolicitacaoRamalHistoricoResponse> getAllHistoricoBySolicitacaoId(Integer idSolicitacao) {
        return historicoRepository.findAllBySolicitacaoRamalId(idSolicitacao)
            .stream()
            .map(SolicitacaoRamalHistoricoResponse::convertFrom)
            .collect(Collectors.toList());
    }

    public PageImpl<SolicitacaoRamalResponse> getAll(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        validarFiltroObrigatorios(filtros);
        var solicitacoes = solicitacaoRamalRepository.findAll(pageable, filtros.toPredicate().build());

        if (solicitacoes.getContent().isEmpty()) {
            throw new NotFoundException("Nenhuma solicitação de ramal foi encontrada para a equipe selecionada.");
        }
        return new PageImpl<>(solicitacoes.getContent()
            .stream()
            .map(SolicitacaoRamalResponse::convertFrom)
            .collect(Collectors.toList()),
            pageable,
            solicitacoes.getTotalElements());
    }

    private void validarFiltroObrigatorios(SolicitacaoRamalFiltros filtros) {
        var cargo = autenticacaoService.getUsuarioAutenticado().getCargoCodigo();
        if (cargo == CodigoCargo.AGENTE_AUTORIZADO_SOCIO) {
            if (filtros.getAgenteAutorizadoId() != null) {
                serviceAa.verificaPermissaoSobreOAgenteAutorizado(filtros.getAgenteAutorizadoId());
            } else if (!autenticacaoService.getUsuarioAutenticado().hasPermissao(CTR_2034)) {
                throw new ValidacaoException(MSG_DEFAULT_PARAM_OBRIGATORIO);
            }
        }
    }

    @Transactional
    public SolicitacaoRamalResponse save(SolicitacaoRamalRequest request) {
        return getSolicitacaoRamalService(request.getCanal()).save(request);
    }

    public SolicitacaoRamalDadosAdicionaisResponse getDadosAdicionais(SolicitacaoRamalFiltros filtros) {
        return getSolicitacaoRamalService(filtros.getCanal()).getDadosAdicionais(filtros);
    }

    @Transactional
    public SolicitacaoRamalResponse update(SolicitacaoRamalRequest request) {
        return getSolicitacaoRamalService(request.getCanal()).update(request);
    }

    public Page<SolicitacaoRamalResponse> getAllGerencia(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        return getSolicitacaoRamalService(filtros.getCanal()).getAllGerencia(pageable, filtros);
    }

    private ISolicitacaoRamalService getSolicitacaoRamalService(ECanal canal) {
        return context.getBean(solicitacaoRamalService.get(canal));
    }

    private void gerarHistorico(SolicitacaoRamal solicitacaoRamal, String comentario) {
        historicoService.save(new SolicitacaoRamalHistorico().gerarHistorico(solicitacaoRamal, comentario));
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

    public SolicitacaoRamal findById(Integer id) {
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
