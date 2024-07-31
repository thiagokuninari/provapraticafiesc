package br.com.xbrain.autenticacao.modules.feriado.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoImportacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FeriadoTest {

    @Test
    public void of_deveRetornarFeriadoCorreto_quandoChamado() {
        assertThat(Feriado.of(umFeriadoEstadualRequest(null), 1111))
            .extracting("id", "nome", "dataFeriado", "usuarioCadastro.id", "uf.id", "cidade.id", "tipoFeriado", "situacao",
                "feriadoPai.id", "feriadoNacional")
            .containsExactlyInAnyOrder(null, "FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), 1111,
                1, 1, ETipoFeriado.ESTADUAL, ESituacaoFeriado.ATIVO, null, Eboolean.F);
    }

    @Test
    public void criarFeriadoFilho_deveRetornarFeriadoComOsDadosDoFeriadoPai_quandoChamado() {
        assertThat(Feriado.criarFeriadoFilho(umaCidade(), umFeriadoEstadual()))
            .extracting("id", "nome", "dataFeriado", "usuarioCadastro.id", "uf.id", "cidade.id", "tipoFeriado", "situacao",
                "feriadoPai.id", "feriadoNacional", "dataCadastro")
            .containsExactlyInAnyOrder(null, "FERIADO SANTA CATARINA", LocalDate.of(2019, 8, 12), 1111,
                22, 4498, ETipoFeriado.ESTADUAL, ESituacaoFeriado.ATIVO, 123, Eboolean.F,
                LocalDateTime.of(2018, 11, 11, 11, 11, 11));
    }

    @Test
    public void ofFeriadoEditado_deveRetornarFeriadoEditadoComDadosCorretos_quandoChamado() {
        assertThat(Feriado.ofFeriadoEditado(umFeriadoEstadual(), umFeriadoEstadualRequest(123)))
            .extracting("id", "nome", "dataFeriado", "usuarioCadastro.id", "uf.id", "cidade.id", "tipoFeriado", "situacao",
                "feriadoPai.id", "feriadoNacional", "dataCadastro")
            .containsExactlyInAnyOrder(123, "FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), 1111,
                1, 1, ETipoFeriado.ESTADUAL, ESituacaoFeriado.ATIVO, null, Eboolean.F,
                LocalDateTime.of(2018, 11, 11, 11, 11, 11));
    }

    @Test
    public void excluir_deveAlterarSituacaoDoFeriadoParaExcluido_quandoChamado() {
        var feriadoExcluido = umFeriadoEstadual();
        feriadoExcluido.excluir();
        assertThat(feriadoExcluido)
            .extracting("id", "nome", "dataFeriado", "usuarioCadastro.id", "uf.id", "cidade.id", "tipoFeriado", "situacao",
                "feriadoPai.id", "feriadoNacional", "dataCadastro")
            .containsExactlyInAnyOrder(123, "FERIADO SANTA CATARINA", LocalDate.of(2019, 8, 12), 1111,
                22, null, ETipoFeriado.ESTADUAL, ESituacaoFeriado.EXCLUIDO, null, Eboolean.F,
                LocalDateTime.of(2018, 11, 11, 11, 11, 11));
    }

    @Test
    public void editarFeriadoFilho_deveCopiarNomeEDataFeriadoDoPai_quandoChamado() {
        var feriadoFilho = umFeriadoFilho();
        feriadoFilho.editarFeriadoFilho(umFeriadoEstadual());
        assertThat(feriadoFilho)
            .extracting("id", "nome", "dataFeriado", "usuarioCadastro.id", "uf.id", "cidade.id", "tipoFeriado", "situacao",
                "feriadoPai.id", "feriadoNacional", "dataCadastro")
            .containsExactlyInAnyOrder(222, "FERIADO SANTA CATARINA", LocalDate.of(2019, 8, 12), 1111,
                22, 4498, ETipoFeriado.ESTADUAL, ESituacaoFeriado.ATIVO, 123, Eboolean.F,
                LocalDateTime.of(2018, 11, 11, 11, 11, 11));
    }

    @Test
    public void ofFeriadoImportado_deveRetornarFeriadoComDadosCorretos_quandoChamado() {
        assertThat(Feriado.ofFeriadoImportado(umaFeriadoImportacao(), 1111))
            .extracting("id", "nome", "dataFeriado", "usuarioCadastro.id", "uf.id", "cidade.id", "tipoFeriado", "situacao",
                "feriadoPai.id", "feriadoNacional")
            .containsExactlyInAnyOrder(null, "FERIADO IMPORTADO", LocalDate.of(2019, 3, 22), 1111,
                22, null, ETipoFeriado.ESTADUAL, ESituacaoFeriado.ATIVO, null, Eboolean.F);
    }

    @Test
    public void isFeriadoEstadual_deveRetornarTrue_quandoFeriadoEstadual() {
        assertThat(umFeriadoEstadual().isFeriadoEstadual()).isTrue();
    }

    @Test
    public void isFeriadoEstadual_deveRetornarFalse_quandoFeriadoNaoEstadual() {
        assertThat(umFeriadoNacional().isFeriadoEstadual()).isFalse();
    }

    @Test
    public void setup_deveRetornarTrue_quandoFeriadoEstadual() {
        var feriado = umFeriadoNacional();
        feriado.setNome("nome");

        feriado.setup();

        assertThat(feriado.getNome()).isEqualTo("NOME");
    }

    private FeriadoRequest umFeriadoEstadualRequest(Integer id) {
        return FeriadoRequest.builder()
            .id(id)
            .nome("FERIADO ESTADUAL")
            .tipoFeriado(ETipoFeriado.ESTADUAL)
            .estadoId(1)
            .cidadeId(1)
            .dataFeriado("12/07/2019")
            .build();
    }

    private Feriado umFeriadoEstadual() {
        return Feriado.builder()
            .id(123)
            .nome("FERIADO SANTA CATARINA")
            .tipoFeriado(ETipoFeriado.ESTADUAL)
            .uf(Uf.builder()
                .id(22)
                .build())
            .dataCadastro(LocalDateTime.of(2018, 11, 11, 11, 11, 11))
            .dataFeriado(LocalDate.of(2019, 8, 12))
            .usuarioCadastro(new Usuario(1111))
            .feriadoNacional(Eboolean.F)
            .situacao(ESituacaoFeriado.ATIVO)
            .build();
    }

    private Cidade umaCidade() {
        return Cidade.builder()
            .id(4498)
            .nome("CHAPECO")
            .uf(Uf.builder()
                .id(22)
                .build())
            .build();
    }

    private Feriado umFeriadoFilho() {
        return Feriado.builder()
            .id(222)
            .nome("ANIVERSARIO DO ESTADO")
            .tipoFeriado(ETipoFeriado.ESTADUAL)
            .uf(Uf.builder()
                .id(22)
                .build())
            .dataCadastro(LocalDateTime.of(2018, 11, 11, 11, 11, 11))
            .dataFeriado(LocalDate.of(2019, 10, 12))
            .usuarioCadastro(new Usuario(1111))
            .feriadoNacional(Eboolean.F)
            .situacao(ESituacaoFeriado.ATIVO)
            .cidade(Cidade.builder()
                .id(4498)
                .build())
            .feriadoPai(Feriado.builder()
                .id(123)
                .build())
            .build();
    }

    private Feriado umFeriadoNacional() {
        return Feriado.builder()
            .id(1234)
            .nome("FERIADO NACIONAL")
            .dataFeriado(LocalDate.of(2019, 9, 23))
            .dataCadastro(LocalDateTime.of(2018, 11, 11, 11, 11, 11))
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .build();
    }

    private FeriadoImportacao umaFeriadoImportacao() {
        return FeriadoImportacao.builder()
            .tipoFeriado(ETipoFeriado.ESTADUAL)
            .dataFeriado(LocalDate.of(2019, 3, 22))
            .nome("FERIADO IMPORTADO")
            .motivoNaoImportacao(List.of())
            .uf(Uf.builder()
                .id(22)
                .build())
            .build();
    }
}
