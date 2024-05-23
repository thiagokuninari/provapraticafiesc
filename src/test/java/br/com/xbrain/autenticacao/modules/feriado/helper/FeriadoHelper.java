package br.com.xbrain.autenticacao.modules.feriado.helper;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

public class FeriadoHelper {

    public static Uf umUf(Integer id) {
        return Uf.builder()
            .id(id)
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

    public static List<Uf> umaListUf() {
        return List.of(umUf(1),
            umUf(2));
    }

    public static ImportacaoFeriado umImportacaoFeriado() {
        return ImportacaoFeriado.builder()
            .id(1)
            .dataCadastro(LocalDateTime.of(2024, 9, 20, 0, 0, 0))
            .situacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO)
            .build();
    }
}
