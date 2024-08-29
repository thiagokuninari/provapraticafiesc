package br.com.xbrain.autenticacao.modules.feriado.helper;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.helper.UfHelper;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.dto.*;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeriadoHelper {

    private static final String STRING_COM_256_CARACTERES = "i".repeat(256);

    public static FeriadoImportacaoRequest umFeriadoImportacaoRequest() {
        return FeriadoImportacaoRequest.builder()
            .anoReferencia(2019)
            .build();
    }

    public static FeriadoImportacao umFeriadoImportacao() {
        return FeriadoImportacao.builder()
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .nome("TESTE")
            .dataFeriado(LocalDate.of(2019, 10, 12))
            .motivoNaoImportacao(new ArrayList<>())
            .build();
    }

    public static FeriadoImportacao umFeriadoImportacao(String nome) {
        return FeriadoImportacao.builder()
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .nome(nome)
            .dataFeriado(LocalDate.of(2019, 10, 12))
            .motivoNaoImportacao(new ArrayList<>())
            .build();
    }

    public static FeriadoRequest umFeriadoRequest() {
        return FeriadoRequest.builder()
            .nome("FERIADO TESTE")
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .dataFeriado("12/11/2019")
            .build();
    }

    public static FeriadoResponse umFeriadoResponse() {
        return FeriadoResponse.builder()
            .nome("FERIADO TESTE")
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .cidadeId(1)
            .cidadeNome("LONDRINA")
            .estadoNome("PR")
            .estadoId(1)
            .anoReferencia(2023)
            .dataFeriado(LocalDate.of(2023, 10, 12))
            .build();
    }

    public static Page<Feriado> umaPaginaFeriado() {
        var lista = List.of(umFeriado());

        return new PageImpl<>(lista, new PageRequest(), 0);
    }

    public static Feriado umFeriado() {
        return Feriado.builder()
            .nome("FERIADO TESTE")
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .cidade(Cidade.builder()
                .id(1)
                .nome("LONDRINA")
                .build())
            .uf(Uf.builder()
                .id(1)
                .nome("PR")
                .build())
            .dataFeriado(LocalDate.of(2023, 10, 12))
            .usuarioCadastro(Usuario.builder()
                .id(89)
                .build())
            .build();
    }

    public static Feriado umFeriado(String nome) {
        return Feriado.builder()
            .nome(nome)
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .cidade(Cidade.builder()
                .id(1)
                .nome("LONDRINA")
                .build())
            .uf(Uf.builder()
                .id(1)
                .nome("PR")
                .build())
            .dataFeriado(LocalDate.of(2023, 10, 12))
            .usuarioCadastro(Usuario.builder()
                .id(89)
                .build())
            .build();
    }

    public static FeriadoCidadeEstadoResponse umFeriadoCidadeEstadoResponse() {
        return FeriadoCidadeEstadoResponse.builder()
            .cidade("LODNRINA")
            .estado("PR")
            .build();
    }

    public static Iterable<Feriado> umIterableFeriado() {
        List<Feriado> listaDeFeriados = new ArrayList<>();

        listaDeFeriados.add(umFeriado());

        return listaDeFeriados;
    }

    public static FeriadoMesAnoResponse umFeriadoMesAnoResponse() {
        return FeriadoMesAnoResponse.builder()
            .ano(2023)
            .mes(3)
            .qtdFeriadosNacionais(3L)
            .build();
    }

    public static MockMultipartFile umFile(byte[] bytes, String nome) {
        return new MockMultipartFile("file", LocalDateTime.now().toString().concat(nome) + ".csv",
            "text/csv", bytes);
    }

    public static MockMultipartFile umFile(String file) {
        byte[] bytes = file.getBytes(StandardCharsets.UTF_8);
        String nome = "teste_arquivo";
        return umFile(bytes, nome);
    }

    public static MockMultipartFile umFileFeriado() {
        String file = "TIPO DO FERIADO;CIDADE;UF;DATA DO FERIADO;NOME DO FERIADO\n"
            + "NACIONAL;LONDRINA;PR;12/10/2019;FERIADO CORRETO\n"
            + "NA;LONDRINA;PR;2019-10-12;" + STRING_COM_256_CARACTERES + "\n"
            + "MUNICIPAL;LONDRINA;PR;12/10/2019;FERIADO INCORRETO\n"
            + "NACIONAL;LONDRINA;PR;12/10/2019;FERIADO EXISTENTE\n";

        return umFile(file);
    }

    public static List<FeriadoImportacaoResponse> umaListaFeriadoImportacaoResponse() {
        return List.of(
            FeriadoImportacaoResponse.builder()
                .nome("FERIADO CORRETO")
                .dataFeriado(LocalDate.of(2023, 10, 12))
                .cidadeNome("LONDRINA")
                .cidadeId(1)
                .estadoId(1)
                .estadoNome("PR")
                .motivoNaoImportacao(List.of())
                .feriadoImportadoComSucesso(Eboolean.V)
                .tipoFeriado(ETipoFeriado.NACIONAL)
                .build(),
            FeriadoImportacaoResponse.builder()
                .nome(STRING_COM_256_CARACTERES)
                .motivoNaoImportacao(List.of("Falha ao recuperar Tipo do Feriado.",
                    "Feriado está com data inválida.", "Feriado está com nome inválido."))
                .feriadoImportadoComSucesso(Eboolean.F)
                .build(),
            FeriadoImportacaoResponse.builder()
                .nome("FERIADO INCORRETO")
                .dataFeriado(LocalDate.of(2019, 10, 12))
                .motivoNaoImportacao(List.of("Falha ao recuperar UF.", "Falha ao recuperar Cidade."))
                .feriadoImportadoComSucesso(Eboolean.F)
                .tipoFeriado(ETipoFeriado.MUNICIPAL)
                .build(),
            FeriadoImportacaoResponse.builder()
                .nome("FERIADO EXISTENTE")
                .dataFeriado(LocalDate.of(2019, 10, 12))
                .motivoNaoImportacao(List.of("Feriado já cadastrado."))
                .feriadoImportadoComSucesso(Eboolean.F)
                .tipoFeriado(ETipoFeriado.NACIONAL)
                .build()
        );
    }

    public static FeriadoImportacaoResponse umFeriadoImportacaoResponse() {
        return FeriadoImportacaoResponse.builder()
            .nome("FERIADO TESTE")
            .dataFeriado(LocalDate.of(2023, 10, 12))
            .cidadeNome("LONDRINA")
            .cidadeId(1)
            .estadoId(1)
            .estadoNome("PR")
            .motivoNaoImportacao(List.of())
            .feriadoImportadoComSucesso(Eboolean.V)
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .build();
    }

    public static Feriado umFeriadoNacional() {
        return Feriado.builder()
            .id(1234)
            .nome("FERIADO NACIONAL")
            .dataFeriado(LocalDate.of(2019, 9, 23))
            .dataCadastro(LocalDateTime.of(2018, 11, 11, 11, 11, 11))
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .build();
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
