package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.util.CnpjUtil;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.SocioService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalDadosAdicionaisResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.util.SolicitacaoRamalExpiracaoAdjuster;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
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

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService.ERRO_SEM_AGENTE_AUTORIZADO;
import static br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService.SOLICITACAO_PENDENTE_OU_ANDAMENTO;

@Component
public class SolicitacaoRamalServiceAa implements ISolicitacaoRamalService {

    private static final String ASSUNTO_EMAIL_CADASTRAR = "Nova Solicitação de Ramal";
    private static final String TEMPLATE_EMAIL = "solicitacao-ramal";

    @Autowired
    private SolicitacaoRamalRepository solicitacaoRamalRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private SolicitacaoRamalService solicitacaoRamalService;
    @Autowired
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private DataHoraAtual dataHoraAtual;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private SolicitacaoRamalHistoricoService historicoService;
    @Autowired
    private CallService callService;
    @Autowired
    private SocioService socioService;
    @Value("${app-config.email.emails-solicitacao-ramal}")
    private String destinatarios;

    @Override
    public SolicitacaoRamalResponse save(SolicitacaoRamalRequest request) {
        validarParametroAa(request);

        var solicitacaoRamal = SolicitacaoRamalRequest.convertFrom(request);
        solicitacaoRamal.atualizarDataCadastro(dataHoraAtual.getDataHora());
        solicitacaoRamal.atualizarUsuario(autenticacaoService.getUsuarioId());
        solicitacaoRamal.atualizarNomeECnpjDoAgenteAutorizado(
            agenteAutorizadoNovoService.getAaById(solicitacaoRamal.getAgenteAutorizadoId())
        );

        solicitacaoRamal.retirarMascara();
        var solicitacaoRamalPersistida = solicitacaoRamalRepository.save(solicitacaoRamal);
        enviarEmailAposCadastro(solicitacaoRamalPersistida);

        gerarHistorico(solicitacaoRamalPersistida, null);
        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalPersistida);
    }

    private void validaSalvarAa(Integer aaId) {
        if (hasSolicitacaoPendenteOuEmAdamentoByAaId(aaId)) {
            throw SOLICITACAO_PENDENTE_OU_ANDAMENTO;
        }
    }

    private void validarParametroAa(SolicitacaoRamalRequest request) {
        validaSalvarAa(request.getAgenteAutorizadoId());
        if (request.getCanal() == ECanal.AGENTE_AUTORIZADO
            && request.getAgenteAutorizadoId() == null) {
            throw ERRO_SEM_AGENTE_AUTORIZADO;
        }
    }

    private boolean hasSolicitacaoPendenteOuEmAdamentoByAaId(Integer aaId) {
        return solicitacaoRamalRepository.findAllByAgenteAutorizadoIdAndSituacaoDiferentePendenteOuEmAndamento(aaId)
            .size() > 0;
    }

    private void gerarHistorico(SolicitacaoRamal solicitacaoRamal, String comentario) {
        historicoService.save(new SolicitacaoRamalHistorico().gerarHistorico(solicitacaoRamal, comentario));
    }

    private List<Integer> getAgentesAutorizadosIdsDoUsuarioLogado() {
        Usuario usuario = usuarioService.findComplete(autenticacaoService.getUsuarioId());
        return agenteAutorizadoNovoService.getAgentesAutorizadosPermitidos(usuario);
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

    private List<String> getDestinatarios() {
        if (this.destinatarios.contains(",")) {
            return Arrays.asList(this.destinatarios.split(","));
        }

        return Collections.singletonList(this.destinatarios);
    }

    private List<Integer> getColaboradoresIds(List<Usuario> usuarios) {
        return usuarios.stream()
            .map(Usuario::getId)
            .collect(Collectors.toList());
    }

    private LocalDateTime getDataLimite(LocalDateTime dataCadastro) {
        return LocalDateTime.from(dataCadastro.with(new SolicitacaoRamalExpiracaoAdjuster()));
    }

    @Override
    public SolicitacaoRamalDadosAdicionaisResponse getDadosAdicionais(Integer agenteAutorizadoId) {
        AgenteAutorizadoResponse agenteAutorizadoResponse = agenteAutorizadoNovoService.getAaById(agenteAutorizadoId);

        return SolicitacaoRamalDadosAdicionaisResponse.convertFrom(
            getTelefoniaPelaDiscadoraId(agenteAutorizadoResponse),
            getNomeSocioPrincipalAa(agenteAutorizadoId),
            getQuantidadeUsuariosAtivos(agenteAutorizadoId),
            getQuantidadeRamaisPeloAgenteAutorizadoId(ECanal.AGENTE_AUTORIZADO, agenteAutorizadoId),
            agenteAutorizadoResponse);
    }

    private String getTelefoniaPelaDiscadoraId(AgenteAutorizadoResponse agenteAutorizado) {
        if (!ObjectUtils.isEmpty(agenteAutorizado.getDiscadoraId())) {
            return callService.obterNomeTelefoniaPorId(agenteAutorizado.getDiscadoraId()).getNome();
        }

        return "";
    }

    private long getQuantidadeRamaisPeloAgenteAutorizadoId(ECanal canal, Integer agenteAutorizadoId) {
        return callService.obterRamaisParaCanal(canal, agenteAutorizadoId).size();
    }

    private String getNomeSocioPrincipalAa(Integer agenteAutorizadoId) {
        return socioService.findSocioPrincipalByAaId(agenteAutorizadoId).getNome();
    }

    private long getQuantidadeUsuariosAtivos(Integer agenteAutorizadoId) {
        return agenteAutorizadoService.getUsuariosAaAtivoComVendedoresD2D(agenteAutorizadoId).size();
    }

    @Override
    public SolicitacaoRamalResponse update(SolicitacaoRamalRequest request) {
        SolicitacaoRamal solicitacaoEncontrada = solicitacaoRamalService.findById(request.getId());
        solicitacaoEncontrada.editar(request);
        solicitacaoEncontrada.atualizarUsuario(autenticacaoService.getUsuarioId());
        solicitacaoEncontrada.atualizarNomeECnpjDoAgenteAutorizado(
            agenteAutorizadoNovoService.getAaById(solicitacaoEncontrada.getAgenteAutorizadoId())
        );

        solicitacaoEncontrada.retirarMascara();
        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalRepository.save(solicitacaoEncontrada));
    }

    public void verificaPermissaoSobreOAgenteAutorizado(Integer agenteAutorizadoId) {
        autenticacaoService.getUsuarioAutenticado()
            .hasPermissaoSobreOAgenteAutorizado(agenteAutorizadoId, getAgentesAutorizadosIdsDoUsuarioLogado());
    }

}
