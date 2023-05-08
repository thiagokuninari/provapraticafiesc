package br.com.xbrain.autenticacao.modules.feriado.helper;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class FeriadoHelper {

    public static Uf umUf() {
        return Uf.builder()
            .id(1)
            .nome("PARANA")
            .uf("PR")
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1).nivel(ENivel.XBRAIN.name())
            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_2050.getRole())))
            .build();
    }

    public static List<FeriadoAutomacao> umaListFeriadoAutomacao() {
        return List.of(umFeriadoAutomacao("20/09/2023"),
            umFeriadoAutomacao("07/09/2023"));
    }

    public static FeriadoAutomacao umFeriadoAutomacao(String data) {
        return FeriadoAutomacao.builder()
            .nome("feriado teste")
            .dataFeriado(data)
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .build();
    }

    public static Cidade umaCidade(Integer id, String nome) {
        return Cidade.builder()
            .id(id)
            .nome(nome)
            .uf(Uf.builder().id(1).uf("PR").build())
            .build();
    }
}
