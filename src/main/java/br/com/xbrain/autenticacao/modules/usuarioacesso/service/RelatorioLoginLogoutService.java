package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutCsv;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import br.com.xbrain.xbrainutils.CsvUtils;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.RelatorioNome.LOGIN_LOGOUT_CSV;

@Service
public class RelatorioLoginLogoutService {

    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Page<LoginLogoutResponse> getLoginsLogoutsDeHoje(PageRequest pageRequest) {
        return notificacaoUsuarioAcessoService
            .getLoginsLogoutsDeHoje(getUsuariosIdsComNivelDeAcesso(), pageRequest)
            .toSpringPage(pageRequest);
    }

    public void getCsv(RelatorioLoginLogoutCsvFiltro filtro, HttpServletResponse response) {
        var csvs = notificacaoUsuarioAcessoService.getCsv(filtro, getUsuariosIdsComNivelDeAcesso());
        if (!CsvUtils.setCsvNoHttpResponse(
            LoginLogoutCsv.getCsv(csvs),
            CsvUtils.createFileName(LOGIN_LOGOUT_CSV.name()),
            response)) {
            throw new ValidacaoException("Falha ao tentar baixar relatório de usuários!");
        }
    }

    public List<UsuarioNomeResponse> getColaboradores(boolean buscarInativos) {
        var idsUsuarios = notificacaoUsuarioAcessoService.getUsuariosIdsByIds(getUsuariosIdsComNivelDeAcesso());
        idsUsuarios = getUsuariosIdsPorBuscarInativos(idsUsuarios, buscarInativos);
        return usuarioRepository.findUsuariosIdENomeComSituacaoNaoAtivoPorUsuariosIds(idsUsuarios);
    }

    public List<Integer> getUsuariosIdsPorBuscarInativos(List<Integer> usuariosIds, boolean buscarInativos) {
        var situacoes = buscarInativos
            ? Set.of(ESituacao.A, ESituacao.I, ESituacao.R)
            : Set.of(ESituacao.A);
        var predicate = new UsuarioPredicate()
            .comIdsObrigatorio(usuariosIds)
            .comSituacoes(situacoes)
            .build();
        return usuarioRepository.findAllIds(predicate);
    }

    public Optional<List<Integer>> getUsuariosIdsComNivelDeAcesso() {
        var usuarioAutenticado = getUsuarioAutenticado();

        if (usuarioAutenticado.isMsoOrXbrain()) {
            return Optional.empty();
        }
        if (usuarioAutenticado.isAgenteAutorizado()) {
            return getUsuariosIdsComNivelDeAcessoDoParceiros();
        }
        if (usuarioAutenticado.isAssistenteOperacao()) {
            return getUsuariosIdsComNivelDeAcessoDoParceiros();
        }
        if (usuarioAutenticado.isOperacao() && usuarioAutenticado.isExecutivoOuExecutivoHunter()) {
            return getUsuariosIdsComNivelDeAcessoDoParceiros();
        }
        return Optional.of(usuarioService.getIdDosUsuariosSubordinados(usuarioAutenticado.getId(), true));
    }

    private Optional<List<Integer>> getUsuariosIdsComNivelDeAcessoDoParceiros() {
        var usuariosIds = agenteAutorizadoService.getIdsUsuariosSubordinados(true);
        return Optional.of(ImmutableList.copyOf(usuariosIds));
    }

    private UsuarioAutenticado getUsuarioAutenticado() {
        return autenticacaoService.getUsuarioAutenticado();
    }
}
