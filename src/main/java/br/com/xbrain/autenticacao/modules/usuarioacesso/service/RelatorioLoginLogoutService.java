package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutCsv;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioAcessoColaboradorResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutCsvFiltro;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import br.com.xbrain.xbrainutils.CsvUtils;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
    private UsuarioAcessoRepository usuarioAcessoRepository;

    public Page<LoginLogoutResponse> getLoginsLogoutsDeHoje(PageRequest pageRequest) {
        var usuariosIds = usuarioService.getIdDosUsuariosSubordinados(getUsuarioAutenticadoId(), true);
        return notificacaoUsuarioAcessoService.getLoginsLogoutsDeHoje(usuariosIds, pageRequest).toSpringPage(pageRequest);
    }

    public void getCsv(RelatorioLoginLogoutCsvFiltro filtro, HttpServletResponse response) {
        var predicate = filtro.toPredicate();
        var acessos = usuarioAcessoRepository.findAll(predicate);
        var csvs = LoginLogoutCsv.of(ImmutableList.copyOf(acessos));
        if (!CsvUtils.setCsvNoHttpResponse(
            LoginLogoutCsv.getCsv(csvs),
            CsvUtils.createFileName(LOGIN_LOGOUT_CSV.name()),
            response)) {
            throw new ValidacaoException("Falha ao tentar baixar relatório de usuários!");
        }
    }

    public List<UsuarioAcessoColaboradorResponse> getColaboradores() {
        return usuarioAcessoRepository.findAllColaboradores(new BooleanBuilder());
    }

    private Integer getUsuarioAutenticadoId() {
        return autenticacaoService.getUsuarioId();
    }
}
