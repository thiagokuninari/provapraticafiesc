package br.com.xbrain.autenticacao.modules.solicitacaoramal.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.model.FeriadoSingleton;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SolicitacaoRamalTest {

    @Before
    public void setUp() {
        FeriadoSingleton.getInstance().setFeriadosNacionais(Sets.newHashSet(LocalDate.of(2022, 01, 21)));
    }

    @Test
    public void atualizarDataCadastro_deveSetarDataFinalizacaoAcrescidaDe72HorasUteis() {
        var dataAtual = LocalDateTime.of(2022, 01, 20, 12, 00, 00);

        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.atualizarDataCadastro(dataAtual);

        assertThat(solicitacaoRamal)
            .extracting("dataCadastro", "dataFinalizacao", "situacao", "enviouEmailExpiracao")
            .containsExactlyInAnyOrder(dataAtual,
                LocalDateTime.of(2022, 01, 25, 12, 00, 00),
                ESituacaoSolicitacao.PENDENTE, Eboolean.F);
    }

    @Test
    public void retirarMascaras_deveRemoverAsMascarasDeTelefoneECnpjDoAa() {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setTelefoneTi("(43) 3333-2222");
        solicitacaoRamal.setAgenteAutorizadoCnpj("12.123.123/0001-00");

        solicitacaoRamal.retirarMascara();
        assertThat(solicitacaoRamal)
            .extracting("telefoneTi", "agenteAutorizadoCnpj")
            .containsExactlyInAnyOrder("4333332222", "12123123000100");
    }

    @Test
    public void convertFrom_deveRetornarDadosAdicionaisResponse_seParametrosAaCorretos() {
        var dataCadastro = LocalDateTime.of(2022, 01, 20, 12, 00, 00);
        var atual = SolicitacaoRamal
            .convertFrom(criaSolicitacaoRamal(1, 1), 1, dataCadastro);

        var esperado = SolicitacaoRamal
            .builder()
            .id(1)
            .quantidadeRamais(38)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .agenteAutorizadoId(1)
            .situacao(ESituacaoSolicitacao.PENDENTE)
            .dataFinalizacao(LocalDateTime.of(2022, 01, 25, 12, 00, 00))
            .tipoImplantacao(ETipoImplantacao.ESCRITORIO)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .emailTi("reanto@ti.com.br")
            .dataCadastro(dataCadastro)
            .enviouEmailExpiracao(Eboolean.valueOf(false))
            .usuariosSolicitados(List.of())
            .usuario(Usuario.builder().id(1).build())
            .telefoneTi("(18) 3322-2388")
            .build();

        assertThat(atual)
            .isEqualToComparingFieldByField(esperado);
    }

    private SolicitacaoRamalRequest criaSolicitacaoRamal(Integer id, Integer aaId) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .agenteAutorizadoId(aaId)
            .situacao(ESituacaoSolicitacao.PENDENTE)
            .tipoImplantacao(String.valueOf(ETipoImplantacao.ESCRITORIO))
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .emailTi("reanto@ti.com.br")
            .telefoneTi("(18) 3322-2388")
            .usuariosSolicitadosIds(List.of())
            .build();
    }
}
