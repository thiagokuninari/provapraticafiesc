package br.com.xbrain.autenticacao.modules.feriado.helper;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.helper.UfHelper;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoImportacaoRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FeriadoHelper {

    public static FeriadoImportacaoRequest umFeriadoImportacaoRequest() {
        return FeriadoImportacaoRequest.builder()
            .anoReferencia(2019)
            .build();
    }

    public static FeriadoRequest umFeriadoRequest() {
        return FeriadoRequest.builder()
            .nome("FERIADO TESTE")
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .dataFeriado("12/11/2019")
            .build();
    }

    public static MockMultipartFile umFile(byte[] bytes, String nome) {
        return new MockMultipartFile("file", LocalDateTime.now().toString().concat(nome) + ".csv", "text/csv", bytes);
    }

    public static Feriado umFeriadoAnoNovo() {
        return Feriado.builder()
            .id(1)
            .dataCadastro(LocalDateTime.of(2023, 1, 30, 10, 30))
            .dataFeriado(LocalDate.of(2024, 1, 1))
            .feriadoNacional(Eboolean.V)
            .nome("Ano Novo")
            .cidade(null)
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .uf(null)
            .situacao(ESituacaoFeriado.ATIVO)
            .feriadoPai(null)
            .usuarioCadastro(null)
            .build();
    }

    public static Feriado umFeriadoMunicipalCidadeBarueri() {
        return Feriado.builder()
            .id(33154)
            .dataCadastro(LocalDateTime.of(2023, 1, 30, 11, 20))
            .dataFeriado(LocalDate.of(2023, 7, 9))
            .feriadoNacional(Eboolean.F)
            .nome("Revolução Constitucionalista")
            .cidade(CidadeHelper.cidadeBarueri())
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .uf(UfHelper.ufSaoPaulo())
            .situacao(ESituacaoFeriado.ATIVO)
            .feriadoPai(null)
            .usuarioCadastro(null)
            .build();
    }

    public static Feriado umFeriadoMunicipalCidadeLondrina() {
        return Feriado.builder()
            .id(13853)
            .dataCadastro(LocalDateTime.of(2023, 1, 27, 16, 45))
            .dataFeriado(LocalDate.of(2023, 12, 10))
            .feriadoNacional(Eboolean.F)
            .nome("Aniversário da cidade")
            .cidade(CidadeHelper.cidadeLondrina())
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .uf(UfHelper.ufParana())
            .situacao(ESituacaoFeriado.ATIVO)
            .feriadoPai(null)
            .usuarioCadastro(null)
            .build();
    }

    public static Feriado umFeriadoMunicipalDistritoAldeia() {
        return Feriado.builder()
            .id(34015)
            .dataCadastro(LocalDateTime.of(2023, 1, 27, 17, 30))
            .dataFeriado(LocalDate.of(2023, 7, 9))
            .feriadoNacional(Eboolean.F)
            .nome("Revolução Constitucionalista")
            .cidade(CidadeHelper.distritoAldeia())
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .uf(UfHelper.ufSaoPaulo())
            .situacao(ESituacaoFeriado.ATIVO)
            .feriadoPai(null)
            .usuarioCadastro(null)
            .build();
    }

    public static Feriado umFeriadoMunicipalDistritoJardimBelval() {
        return Feriado.builder()
            .id(33864)
            .dataCadastro(LocalDateTime.of(2023, 1, 30, 11, 20))
            .dataFeriado(LocalDate.of(2023, 7, 9))
            .feriadoNacional(Eboolean.F)
            .nome("Revolução Constitucionalista")
            .cidade(CidadeHelper.distritoJardimBelval())
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .uf(UfHelper.ufSaoPaulo())
            .situacao(ESituacaoFeriado.ATIVO)
            .feriadoPai(null)
            .usuarioCadastro(null)
            .build();
    }

    public static Feriado umFeriadoMunicipalDistritoJardimSilveira() {
        return Feriado.builder()
            .id(33867)
            .dataCadastro(LocalDateTime.of(2023, 1, 30, 11, 20))
            .dataFeriado(LocalDate.of(2023, 7, 9))
            .feriadoNacional(Eboolean.F)
            .nome("Revolução Constitucionalista")
            .cidade(CidadeHelper.distritoJardimSilveira())
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .uf(UfHelper.ufSaoPaulo())
            .situacao(ESituacaoFeriado.ATIVO)
            .feriadoPai(null)
            .usuarioCadastro(null)
            .build();
    }

    public static List<Feriado> umaListaFeriadosCompleta() {
        return List.of(
            umFeriadoMunicipalDistritoAldeia(),
            umFeriadoMunicipalCidadeBarueri(),
            umFeriadoMunicipalDistritoJardimBelval(),
            umFeriadoMunicipalDistritoJardimSilveira(),
            umFeriadoMunicipalCidadeLondrina()
        );
    }

    public static Page<Feriado> umaPageFeriadosCompleta() {
        return new PageImpl<>(umaListaFeriadosCompleta());
    }
}
