package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutCsv;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.enums.ERelatorioLoginLogoutSort;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutListagemFiltro;
import br.com.xbrain.autenticacao.modules.usuarioacesso.predicate.UsuarioAcessoPredicate;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import br.com.xbrain.xbrainutils.CsvUtils;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

import static br.com.xbrain.autenticacao.modules.comum.enums.RelatorioNome.LOGIN_LOGOUT_CSV;

@Service
public class RelatorioLoginLogoutService {

    @Autowired
    private UsuarioAcessoRepository usuarioAcessoRepository;
    @Autowired
    private DataHoraAtual dataHoraAtualService;

    public Page<LoginLogoutResponse> getLoginsLogoutsDeHoje(RelatorioLoginLogoutListagemFiltro filtro, PageRequest pageRequest) {
        var predicate = new UsuarioAcessoPredicate(filtro.toPredicate())
            .porDataCadastro(dataHoraAtualService.getData())
            .build();
        var acessos = usuarioAcessoRepository.findAll(predicate);
        var loginLogoutResponses = LoginLogoutResponse.of(ImmutableList.copyOf(acessos));
        return ERelatorioLoginLogoutSort.getPage(loginLogoutResponses, pageRequest);
    }

    public void getCsv(HttpServletResponse response) {
        var predicate = new UsuarioAcessoPredicate()
            .porDataCadastro(dataHoraAtualService.getData())
            .build();
        var acessos = usuarioAcessoRepository.findAll(predicate);
        var csvs = LoginLogoutCsv.of(ImmutableList.copyOf(acessos));
        if (!CsvUtils.setCsvNoHttpResponse(
            LoginLogoutCsv.getCsv(csvs),
            CsvUtils.createFileName(LOGIN_LOGOUT_CSV.name()),
            response)) {
            throw new ValidacaoException("Falha ao tentar baixar relatório de usuários!");
        }
    }
}
