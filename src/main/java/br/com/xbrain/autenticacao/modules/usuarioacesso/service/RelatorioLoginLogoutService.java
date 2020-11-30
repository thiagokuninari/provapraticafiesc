package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutCsv;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import br.com.xbrain.xbrainutils.CsvUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
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
    private NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Page<LoginLogoutResponse> getLoginsLogoutsDeHoje(PageRequest pageRequest, ECanal canal, Integer agenteAutorizadoId) {
        return notificacaoUsuarioAcessoService
            .getLoginsLogoutsDeHoje(getUsuariosIdsComNivelDeAcesso(canal, agenteAutorizadoId), pageRequest)
            .toSpringPage(pageRequest);
    }

    public void getCsv(
        RelatorioLoginLogoutCsvFiltro filtro,
        HttpServletResponse response,
        ECanal canal,
        Integer agenteAutorizadoId) {
        var usuariosIdsPermitidos = getUsuariosIdsComNivelDeAcesso(canal, agenteAutorizadoId);
        var csvs = notificacaoUsuarioAcessoService.getCsv(filtro, usuariosIdsPermitidos);
        if (!CsvUtils.setCsvNoHttpResponse(
            LoginLogoutCsv.getCsv(csvs),
            CsvUtils.createFileName(LOGIN_LOGOUT_CSV.name()),
            response)) {
            throw new ValidacaoException("Falha ao tentar baixar relatório de usuários!");
        }
    }

    public List<UsuarioNomeResponse> getColaboradores(ECanal canal, Integer agenteAutorizadoId) {
        var usuariosIdsPermitidos = getUsuariosIdsComNivelDeAcesso(canal, agenteAutorizadoId);
        var predicate = new UsuarioPredicate()
            .comIds(notificacaoUsuarioAcessoService.getUsuariosIdsByIds(usuariosIdsPermitidos))
            .comSituacoes(Set.of(ESituacao.A, ESituacao.I, ESituacao.R))
            .build();
        var order = QUsuario.usuario.nome.upper().asc();
        return usuarioRepository.findAllUsuariosNomeComSituacao(predicate, order);
    }

    public Optional<List<Integer>> getUsuariosIdsComNivelDeAcesso(ECanal canal, Integer agenteAutorizadoId) {
        var usuarioAutenticado = getUsuarioAutenticado();
        if (Objects.nonNull(agenteAutorizadoId)) {
            usuarioAutenticado.validarPermissaoSobreOAgenteAutorizado(agenteAutorizadoId);
        }

        var predicate = new UsuarioPredicate()
            .comCanais(usuarioAutenticado.getUsuario().getCanais())
            .comCanal(canal)
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService)
            .filtrarPermitidosRelatorioLoginLogout(canal);
        if (Objects.nonNull(agenteAutorizadoId)) {
            predicate.comIds(agenteAutorizadoService.getUsuariosIdsByAaId(agenteAutorizadoId, true));
        }

        return Optional.of(usuarioRepository.findAllIds(predicate.build()));
    }

    private UsuarioAutenticado getUsuarioAutenticado() {
        return autenticacaoService.getUsuarioAutenticado();
    }
}
