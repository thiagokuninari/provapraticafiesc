package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.util.CnpjUtil;
import br.com.xbrain.autenticacao.modules.comum.util.Constantes;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalDadosAdicionaisResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate.SolicitacaoRamalPredicate;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.util.SolicitacaoRamalExpiracaoAdjuster;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService;
import br.com.xbrain.xbrainutils.DateUtils;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.EM_ANDAMENTO;
import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.PENDENTE;
import static br.com.xbrain.autenticacao.modules.solicitacaoramal.model.QSolicitacaoRamal.solicitacaoRamal;
import static br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService.*;

@Component
public class SolicitacaoRamalServiceD2d implements ISolicitacaoRamalService {

    @Autowired
    private SubCanalService subCanalService;
    @Autowired
    private SolicitacaoRamalService solicitacaoRamalService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private CallService callService;
    @Autowired
    private SolicitacaoRamalRepository solicitacaoRamalRepository;
    @Autowired
    private SolicitacaoRamalHistoricoService historicoService;
    @Autowired
    private DataHoraAtual dataHoraAtual;
    @Value("${app-config.email.emails-solicitacao-ramal}")
    private String destinatarios;

    public Page<SolicitacaoRamalResponse> getAllGerencia(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        var solicitacoes = solicitacaoRamalRepository
            .findAllGerenciaD2d(pageable, getBuild(filtros));

        return new PageImpl<>(solicitacoes.getContent()
            .stream()
            .map(SolicitacaoRamalResponse::convertFrom)
            .collect(Collectors.toList()),
            pageable,
            solicitacoes.getTotalElements());
    }

    private BooleanBuilder getBuild(SolicitacaoRamalFiltros filtros) {
        return filtros.toPredicate().build();
    }

    @Override
    public SolicitacaoRamalResponse save(SolicitacaoRamalRequest request) {
        validarParametroD2d(request);
        validarAutorizacao();

        var usuarioId = autenticacaoService.getUsuarioId();
        var solicitacaoRamal = SolicitacaoRamal.convertFrom(request, usuarioId, dataHoraAtual.getDataHora());
        solicitacaoRamal.retirarMascara();

        var solicitacaoRamalPersistida = solicitacaoRamalRepository.save(solicitacaoRamal);
        enviarEmailAposCadastro(solicitacaoRamalPersistida);

        gerarHistorico(solicitacaoRamalPersistida, null);
        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalPersistida);
    }

    private void validaSalvarD2d(SolicitacaoRamalRequest request) {
        if (hasSolicitacaoPendenteOuEmAndamentoByEquipeDoUsuario(request)) {
            throw SOLICITACAO_PENDENTE_OU_ANDAMENTO;
        }
    }

    private void validarParametroD2d(SolicitacaoRamalRequest request) {
        validaSalvarD2d(request);
        if (request.getCanal() == ECanal.D2D_PROPRIO
            && request.getSubCanalId() == null) {
            throw ERRO_SEM_TIPO_CANAL_D2D;
        }
    }

    private void validarAutorizacao() {
        if (!autenticacaoService.getUsuarioAutenticado()
            .hasPermissao(CodigoFuncionalidade.CTR_20015)) {
            throw SEM_AUTORIZACAO;
        }
    }

    private boolean hasSolicitacaoPendenteOuEmAndamentoByEquipeDoUsuario(SolicitacaoRamalRequest request) {
        var predicate = new SolicitacaoRamalPredicate()
            .comSubCanalId(request.getSubCanalId())
            .comEquipeId(request.getEquipeId()).build()
            .and(solicitacaoRamal.situacao.eq(PENDENTE)
                .or(solicitacaoRamal.situacao.eq(EM_ANDAMENTO)));

        return !solicitacaoRamalRepository.findAllByPredicate(predicate).isEmpty();
    }

    private void gerarHistorico(SolicitacaoRamal solicitacaoRamal, String comentario) {
        historicoService.save(new SolicitacaoRamalHistorico().gerarHistorico(solicitacaoRamal, comentario));
    }

    private void enviarEmailAposCadastro(SolicitacaoRamal solicitacaoRamal) {
        if (solicitacaoRamal != null) {
            emailService.enviarEmailTemplate(
                getDestinatarios(), ASSUNTO_EMAIL_CADASTRAR, TEMPLATE_EMAIL, obterContexto(solicitacaoRamal));
        }
    }

    private List<String> getDestinatarios() {
        if (this.destinatarios.contains(Constantes.VIRGULA)) {
            return Arrays.asList(this.destinatarios.split(Constantes.VIRGULA));
        }

        return Collections.singletonList(this.destinatarios);
    }

    private LocalDateTime getDataLimite(LocalDateTime dataCadastro) {
        return LocalDateTime.from(dataCadastro.with(new SolicitacaoRamalExpiracaoAdjuster()));
    }

    private Context obterContexto(SolicitacaoRamal solicitacaoRamal) {
        var context = new Context();
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

    @Override
    public SolicitacaoRamalDadosAdicionaisResponse getDadosAdicionais(SolicitacaoRamalFiltros filtros) {
        var subCanalDto = subCanalService.getSubCanalById(filtros.getSubCanalId());

        return SolicitacaoRamalDadosAdicionaisResponse.convertFrom(
            getTelefoniaPelaDiscadoraId(subCanalDto),
            getQuantidadeRamaisPeloSubCanal(ECanal.D2D_PROPRIO, filtros.getSubCanalId()));
    }

    private Integer getQuantidadeRamaisPeloSubCanal(ECanal canal, Integer subCanalId) {
        return callService.obterRamaisParaCanal(canal, subCanalId).size();
    }

    private String getTelefoniaPelaDiscadoraId(SubCanalDto subCanalDto) {
        if (!ObjectUtils.isEmpty(subCanalDto.getCodigo())) {
            return callService.obterNomeTelefoniaPorId(subCanalDto.getId()).getNome();
        }

        return "";
    }

    @Override
    public SolicitacaoRamalResponse update(SolicitacaoRamalRequest request) {
        var solicitacaoEncontrada = solicitacaoRamalService.findById(request.getId());
        solicitacaoEncontrada.editar(request);
        solicitacaoEncontrada.setUsuario(new Usuario(autenticacaoService.getUsuarioId()));
        solicitacaoEncontrada.retirarMascara();
        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalRepository.save(solicitacaoEncontrada));
    }
}
