package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.CnpjUtil;
import br.com.xbrain.autenticacao.modules.comum.util.Constantes;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.SocioService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalDadosAdicionaisResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.util.SolicitacaoRamalExpiracaoAdjuster;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.xbrainutils.DateUtils;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService.*;

@Component
@RequiredArgsConstructor
public class SolicitacaoRamalServiceAa implements ISolicitacaoRamalService {

    private final CallService callService;
    private final SocioService socioService;
    private final EmailService emailService;
    private final DataHoraAtual dataHoraAtual;
    private final UsuarioService usuarioService;
    private final AutenticacaoService autenticacaoService;
    private final SolicitacaoRamalService solicitacaoRamalService;
    private final AgenteAutorizadoService agenteAutorizadoService;
    private final SolicitacaoRamalHistoricoService historicoService;
    private final SolicitacaoRamalRepository solicitacaoRamalRepository;

    @Value("${app-config.email.emails-solicitacao-ramal}")
    private String destinatarios;

    public PageImpl<SolicitacaoRamalResponse> getAllGerencia(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        Page<SolicitacaoRamal> solicitacoes = solicitacaoRamalRepository
            .findAllGerenciaAa(pageable, getBuild(filtros));

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
        validarAutorizacao();
        validarParametroAa(request);

        var usuarioId = autenticacaoService.getUsuarioId();
        var agenteAutorizado = agenteAutorizadoService.getAaById(request.getAgenteAutorizadoId());
        var solicitacaoRamal = SolicitacaoRamal
            .convertFrom(request, usuarioId, dataHoraAtual.getDataHora(), agenteAutorizado);
        solicitacaoRamal.retirarMascara();

        var solicitacaoRamalPersistida = solicitacaoRamalRepository.save(solicitacaoRamal);
        enviarEmailAposCadastro(solicitacaoRamalPersistida);

        gerarHistorico(solicitacaoRamalPersistida, null);
        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalPersistida);
    }

    @Override
    public SolicitacaoRamalDadosAdicionaisResponse getDadosAdicionais(SolicitacaoRamalFiltros filtros) {
        var agenteAutorizadoResponse = agenteAutorizadoService.getAaById(filtros.getAgenteAutorizadoId());

        return SolicitacaoRamalDadosAdicionaisResponse.convertFrom(
            getTelefoniaPelaDiscadoraId(agenteAutorizadoResponse),
            getNomeSocioPrincipalAa(filtros.getAgenteAutorizadoId()),
            getQuantidadeUsuariosAtivos(filtros.getAgenteAutorizadoId()),
            getQuantidadeRamaisPeloAgenteAutorizadoId(ECanal.AGENTE_AUTORIZADO, filtros.getAgenteAutorizadoId()),
            agenteAutorizadoResponse);
    }

    @Override
    public SolicitacaoRamalResponse update(SolicitacaoRamalRequest request) {
        var solicitacaoEncontrada = solicitacaoRamalService.findById(request.getId());
        solicitacaoEncontrada.editar(request);
        solicitacaoEncontrada.setUsuario(new Usuario(autenticacaoService.getUsuarioId()));
        solicitacaoEncontrada.atualizarNomeECnpjDoAgenteAutorizado(
            agenteAutorizadoService.getAaById(solicitacaoEncontrada.getAgenteAutorizadoId()));
        solicitacaoEncontrada.retirarMascara();

        return SolicitacaoRamalResponse.convertFrom(solicitacaoRamalRepository.save(solicitacaoEncontrada));
    }

    public void verificaPermissaoSobreOAgenteAutorizado(Integer agenteAutorizadoId) {
        autenticacaoService.getUsuarioAutenticado()
            .hasPermissaoSobreOAgenteAutorizado(agenteAutorizadoId, getAgentesAutorizadosIdsDoUsuarioLogado());
    }

    private void validaSalvarAa(Integer aaId, Integer quantidadeRamais) {
        if (hasSolicitacaoPendenteOuEmAdamentoByAaId(aaId)) {
            throw SOLICITACAO_PENDENTE_OU_ANDAMENTO;
        } else if (validarQuantidade(aaId, quantidadeRamais)) {
            throw new ValidacaoException("Não é possível salvar a solicitação de ramal, pois excedeu o limite.");
        }
    }

    private boolean validarQuantidade(Integer aaId, Integer quantidadeRamais) {
        var ramais = solicitacaoRamalRepository
            .findAllByAgenteAutorizadoIdAndSituacaoEnviadoOuConcluido(aaId)
            .stream()
            .mapToInt(SolicitacaoRamal::getQuantidadeRamais)
            .sum();

        return quantidadeRamais + ramais > agenteAutorizadoService.getUsuariosAaAtivoSemVendedoresD2D(aaId).size();
    }

    private void validarParametroAa(SolicitacaoRamalRequest request) {
        if (request.getCanal() == ECanal.AGENTE_AUTORIZADO
            && request.getAgenteAutorizadoId() == null) {
            throw ERRO_SEM_AGENTE_AUTORIZADO;
        }

        validaSalvarAa(request.getAgenteAutorizadoId(), request.getQuantidadeRamais());
    }

    private void validarAutorizacao() {
        if (!autenticacaoService.getUsuarioAutenticado()
            .hasPermissao(CodigoFuncionalidade.CTR_20014)) {
            throw SEM_AUTORIZACAO;
        }
    }

    private boolean hasSolicitacaoPendenteOuEmAdamentoByAaId(Integer aaId) {
        return !solicitacaoRamalRepository.findAllByAgenteAutorizadoIdAndSituacaoPendenteOuEmAndamento(aaId).isEmpty();
    }

    private void gerarHistorico(SolicitacaoRamal solicitacaoRamal, String comentario) {
        historicoService.save(new SolicitacaoRamalHistorico().gerarHistorico(solicitacaoRamal, comentario));
    }

    private List<Integer> getAgentesAutorizadosIdsDoUsuarioLogado() {
        var usuario = usuarioService.findComplete(autenticacaoService.getUsuarioId());
        return agenteAutorizadoService.getAgentesAutorizadosPermitidos(usuario);
    }

    private void enviarEmailAposCadastro(SolicitacaoRamal solicitacaoRamal) {
        if (solicitacaoRamal != null) {
            emailService.enviarEmailTemplate(
                getDestinatarios(), ASSUNTO_EMAIL_CADASTRAR, TEMPLATE_EMAIL, obterContexto(solicitacaoRamal));
        }
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

    private List<String> getDestinatarios() {
        if (this.destinatarios.contains(Constantes.VIRGULA)) {
            return Arrays.asList(this.destinatarios.split(Constantes.VIRGULA));
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

    private String getTelefoniaPelaDiscadoraId(AgenteAutorizadoResponse agenteAutorizado) {
        if (agenteAutorizado.getDiscadoraId() != null) {
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
}
