package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoRamalHelper {

    public static SolicitacaoRamal umaSolicitacaoRamal(Integer id) {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setId(id);
        solicitacaoRamal.setDataCadastro(LocalDateTime.of(2022,02,10,10,00,00));
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.ESCRITORIO);

        return solicitacaoRamal;
    }

    public static SolicitacaoRamal umaSolicitacaoRamal(ESituacaoSolicitacao situacao) {
        var solicitacaoRamal = new SolicitacaoRamal(
            1,
            1,
            "JoãoAA",
            "25280843000110",
            situacao,
            35,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now(),
            new Usuario(106));
        solicitacaoRamal.setUsuariosSolicitados(List.of(
            new Usuario(106),
            new Usuario(226),
            new Usuario(230),
            new Usuario(101)
        ));
        solicitacaoRamal.setEmailTi("joaoaa@hotmail.com");
        solicitacaoRamal.setTelefoneTi("(43) 3322-44444");
        solicitacaoRamal.setMelhorHorarioImplantacao(LocalTime.of(07, 29, 59));
        solicitacaoRamal.setMelhorDataImplantacao(LocalDate.of(2023, 02, 16));
        solicitacaoRamal.setDataFinalizacao(LocalDateTime.now());
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.HOME_OFFICE);
        solicitacaoRamal.setEnviouEmailExpiracao(Eboolean.F);
        return solicitacaoRamal;
    }

    public static List<SolicitacaoRamal> umaListaSolicitacaoRamal() {
        return List.of(
            umaSolicitacaoRamal(1),
            umaSolicitacaoRamal(2),
            umaSolicitacaoRamal(3)
        );
    }

    public static List<SolicitacaoRamal> umaListaSolicitacaoRamalEmpty() {
        List<SolicitacaoRamal> lista = new ArrayList<>();
        return lista;
    }
}
