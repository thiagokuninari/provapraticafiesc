package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.util.CnpjUtil;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalDadosAdicionaisResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalHistoricoRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.util.SolicitacaoRamalExpiracaoAdjuster;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService;
import br.com.xbrain.xbrainutils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService.ERRO_SEM_TIPO_CANAL_D2D;
import static br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService.SOLICITACAO_PENDENTE_OU_ANDAMENTO;

@Component
public class SolicitacaoRamalServiceD2d implements ISolicitacaoRamalService {

    private static final String ASSUNTO_EMAIL_CADASTRAR = "Nova Solicitação de Ramal";
    private static final String TEMPLATE_EMAIL = "solicitacao-ramal";
    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Solicitação não encontrada.");

    @Autowired
    private SubCanalService subCanalService;
    @Autowired
    private SolicitacaoRamalHistoricoRepository historicoRepository;
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

    @Override
    public SolicitacaoRamalResponse save(SolicitacaoRamalRequest request) {
        validarParametroD2d(request);

        var solicitacaoRamal = SolicitacaoRamalRequest.convertFrom(request);
        solicitacaoRamal.atualizarDataCadastro(dataHoraAtual.getDataHora());
        solicitacaoRamal.atualizarUsuario(autenticacaoService.getUsuarioId());
        solicitacaoRamal.retirarMascara();

        var solicitacaoRamalPersistida = solicitacaoRamalRepository.save(solicitacaoRamal);
        enviarEmailAposCadastro(solicitacaoRamalPersistida);

        gerarHistorico(solicitacaoRamalPersistida, null);
        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalPersistida);
    }

    private void validaSalvarD2d(Integer subCanalId) {
        if (hasSolicitacaoPendenteOuEmAdamentoBySubCanalId(subCanalId)) {
            throw SOLICITACAO_PENDENTE_OU_ANDAMENTO;
        }
    }

    private void validarParametroD2d(SolicitacaoRamalRequest request) {
        validaSalvarD2d(request.getSubCanalId());
        if (request.getCanal() == ECanal.D2D_PROPRIO &&
            request.getSubCanalId() == null) {
            throw ERRO_SEM_TIPO_CANAL_D2D;
        }
    }

    private boolean hasSolicitacaoPendenteOuEmAdamentoBySubCanalId(Integer subCanalId) {
        return solicitacaoRamalRepository.findAllBySubCanalIdAndSituacaoDiferentePendenteOuEmAndamento(subCanalId)
            .size() > 0;
    }

    private void gerarHistorico(SolicitacaoRamal solicitacaoRamal, String comentario) {
        historicoService.save(new SolicitacaoRamalHistorico().gerarHistorico(solicitacaoRamal, comentario));
    }

    private void enviarEmailAposCadastro(SolicitacaoRamal solicitacaoRamal) {
        if (!ObjectUtils.isEmpty(solicitacaoRamal)) {
            emailService.enviarEmailTemplate(
                getDestinatarios(), ASSUNTO_EMAIL_CADASTRAR, TEMPLATE_EMAIL, obterContexto(solicitacaoRamal));
        }
    }

    private List<String> getDestinatarios() {
        if (this.destinatarios.contains(",")) {
            return Arrays.asList(this.destinatarios.split(","));
        }

        return Collections.singletonList(this.destinatarios);
    }

    private LocalDateTime getDataLimite(LocalDateTime dataCadastro) {
        return LocalDateTime.from(dataCadastro.with(new SolicitacaoRamalExpiracaoAdjuster()));
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

    @Override
    public SolicitacaoRamalDadosAdicionaisResponse getDadosAdicionais(Integer subCanalId) {
        SubCanalDto subCanalDto = subCanalService.getSubCanalById(subCanalId);

        return SolicitacaoRamalDadosAdicionaisResponse.convertFrom(
            getTelefoniaPelaDiscadoraId(subCanalDto),
            getQuantidadeRamaisPeloSubCanal(ECanal.D2D_PROPRIO, subCanalId));
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
        SolicitacaoRamal solicitacaoEncontrada = findById(request.getId());
        solicitacaoEncontrada.editar(request);
        solicitacaoEncontrada.atualizarUsuario(autenticacaoService.getUsuarioId());

        solicitacaoEncontrada.retirarMascara();
        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalRepository.save(solicitacaoEncontrada));
    }

    private SolicitacaoRamal findById(Integer id) {
        return solicitacaoRamalRepository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }
}
