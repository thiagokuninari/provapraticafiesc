package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.RelatorioLoginLogoutListagemFiltro;
import br.com.xbrain.autenticacao.modules.usuarioacesso.predicate.UsuarioAcessoPredicate;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelatorioLoginLogoutService {

    @Autowired
    private UsuarioAcessoRepository usuarioAcessoRepository;
    @Autowired
    private DataHoraAtual dataHoraAtualService;

    public List<LoginLogoutResponse> getLoginsLogoutsDeHoje(RelatorioLoginLogoutListagemFiltro filtro) {
        var predicate = new UsuarioAcessoPredicate(filtro.toPredicate())
            .porDataCadastro(dataHoraAtualService.getData())
            .build();
        var acessos = usuarioAcessoRepository.findAll(predicate);
        return LoginLogoutResponse.of(ImmutableList.copyOf(acessos));
    }
}
