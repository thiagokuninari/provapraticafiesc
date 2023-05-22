package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.service.CsvFileService;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoImportacaoRequest;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.mailing.service.MailingService;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.feriado.util.FeriadoPlanilhaLayoutUtil.DELIMITADOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/feriado-repository-test.sql"})
public class FeriadoImportacaoServiceTest {

    @Autowired
    private FeriadoImportacaoService importacaoService;
    @Autowired
    private FeriadoService feriadoService;
    @Autowired
    private FeriadoRepository feriadoRepository;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private FeriadoHistoricoService feriadoHistoricoService;
    @MockBean
    private CallService callService;
    @MockBean
    private MailingService mailingService;

    MockMultipartFile mockMultipartFile;

    @Before
    @SneakyThrows
    public void setUp() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado.csv"));
        mockMultipartFile = umFile(bytes) ;

        when(autenticacaoService.getUsuarioId()).thenReturn(1111);
    }

    @Test
    @SneakyThrows
    public void importarFeriadoArquivo_deveLancarException_quandoNaoTiverTodasColunasObrigatoriaNoArquivo() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado_sem_uf.csv"));
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> importacaoService.importarFeriadoArquivo(umFile(bytes), umFeriadoImportacaoRequest()))
            .withMessage("O cabeçalho do arquivo não pode ser diferente do exemplo.");
    }

    @Test
    @SneakyThrows
    public void importarFeriadoArquivo_deveLancarExcepion_quandoOrdemDasColunasDiferenteDoLayoutObrigatorio() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado_ordem_invertida.csv"));
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> importacaoService.importarFeriadoArquivo(umFile(bytes), umFeriadoImportacaoRequest()))
            .withMessage("O cabeçalho do arquivo não pode ser diferente do exemplo.");
    }

    @Test
    @SneakyThrows
    public void importarFeriadoArquivo_deveLancarException_quandoArquivoVazio() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado_vazio.csv"));
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> importacaoService.importarFeriadoArquivo(umFile(bytes), umFeriadoImportacaoRequest()))
            .withMessage("O arquivo não pode ser vazio.");
    }

    @Test
    @SneakyThrows
    public void importarFeriadoArquivo_deveLancarException_quandoArquivoTiverLinhasVazias() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado_com_linhas_vazias.csv"));
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> importacaoService.importarFeriadoArquivo(umFile(bytes), umFeriadoImportacaoRequest()))
            .withMessage("O arquivo não pode ser vazio.");
    }

    @Test
    public void importarFeriadoArquivo_deveFiltrarLinhasVazias_quandoArquivoContemLinhasSemDados() {
        assertThat(importacaoService.importarFeriadoArquivo(mockMultipartFile, umFeriadoImportacaoRequest()))
            .hasSize(14);
    }

    @Test
    public void importarFeriadoArquivo_deveSalvarFeriados_quandoDadosCorretos() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);

        importacaoService.importarFeriadoArquivo(mockMultipartFile, umFeriadoImportacaoRequest());

        assertThat(feriadoRepository.findAll())
            .hasSize(30);
    }

    @Test
    public void importarFeriado_deveRetornarErroDeTipoFeriado_quandoTipoFeriadoInvalido() {
        var linha = umaLinha(8);
        assertThat(importacaoService.importarFeriado(linha, 2019))
            .extracting("tipoFeriado", "motivoNaoImportacao", "nome", "dataFeriado", "estadoId",
                "cidadeId", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(null, List.of("Falha ao recuperar Tipo do Feriado."), "FERIADO COM TIPO ERRADO",
                LocalDate.of(2019, 9, 20), null, null, Eboolean.F);
    }

    @Test
    public void importarFeriado_deveRetornarErroDaCidade_quandoCidadeNaoExiste() {
        var linha = umaLinha(9);
        assertThat(importacaoService.importarFeriado(linha, 2019))
            .extracting("tipoFeriado", "motivoNaoImportacao", "nome", "dataFeriado", "estadoId",
                "cidadeId", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(ETipoFeriado.MUNICIPAL, List.of("Falha ao recuperar Cidade."),
                "FERIADO COM CIDADE NAO EXISTINDO", LocalDate.of(2019, 9, 20), 1, null, Eboolean.F);
    }

    @Test
    public void importarFeriado_deveRetornarErroDoUf_quandoUfNaoExiste() {
        var linha = umaLinha(10);
        assertThat(importacaoService.importarFeriado(linha, 2019))
            .extracting("tipoFeriado", "motivoNaoImportacao", "nome", "dataFeriado", "estadoId",
                "cidadeId", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(ETipoFeriado.ESTADUAL, List.of("Falha ao recuperar UF."),
                "FERIADO COM UF NAO EXISTINDO", LocalDate.of(2019, 3, 22), null, null, Eboolean.F);
    }

    @Test
    public void importarFeriado_deveRetornarErroDaData_quandoFormatoDaDataDiferenteoLayout() {
        var linha = umaLinha(11);
        assertThat(importacaoService.importarFeriado(linha, 2019))
            .extracting("tipoFeriado", "motivoNaoImportacao", "nome", "dataFeriado", "estadoId",
                "cidadeId", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(ETipoFeriado.NACIONAL, List.of("Feriado está com data inválida."),
                "FERIADO NACIONAL COM FORMATO DE DATA ERRADO", null, null, null, Eboolean.F);
    }

    @Test
    public void importarFeriado_deveRetornarErroDaData_quandoDataDoFeriadoForaDoAnoReferencia() {
        var linha = umaLinha(12);
        assertThat(importacaoService.importarFeriado(linha, 2019))
            .extracting("tipoFeriado", "motivoNaoImportacao", "nome", "dataFeriado", "estadoId",
                "cidadeId", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(ETipoFeriado.NACIONAL, List.of("A data do feriado não está no ano de referência."),
                "FERIADO DO ANO 2018", LocalDate.of(2018, 9, 23), null, null, Eboolean.F);
    }

    @Test
    public void importarFeriado_deveRetornarErroDoNome_quandoNomeDoFeriadoMaisLongoDoQueTamanhoMaximo() {
        var linha = umaLinha(13);
        assertThat(importacaoService.importarFeriado(linha, 2019))
            .extracting("tipoFeriado", "motivoNaoImportacao", "dataFeriado", "estadoId",
                "cidadeId", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(ETipoFeriado.NACIONAL, List.of("Feriado está com nome inválido."),
                LocalDate.of(2019, 9, 23), null, null, Eboolean.F);
    }

    @Test
    public void importarFeriado_deveRetornarErroDoFeriadoExistente_quandoFeriadoJaSalvoNoBanco() {
        var linha = umaLinha(14);
        assertThat(importacaoService.importarFeriado(linha, 2019))
            .extracting("tipoFeriado", "motivoNaoImportacao", "nome", "dataFeriado", "estadoId",
                "cidadeId", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(ETipoFeriado.NACIONAL, List.of("Feriado já cadastrado."),
                "FERIADO NACIONAL DO LUIS", LocalDate.of(2019, 7, 30), null, null, Eboolean.F);
    }

    @Test
    public void importarFeriado_deveRetornarTodosErros_quandoTiverMaisErrosNaImportacao() {
        var linha = umaLinha(16);
        assertThat(importacaoService.importarFeriado(linha, 2019))
            .extracting("tipoFeriado", "motivoNaoImportacao", "dataFeriado", "estadoId",
                "cidadeId", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(ETipoFeriado.MUNICIPAL, List.of("Falha ao recuperar UF.", "Falha ao recuperar Cidade.",
                "Feriado está com data inválida.", "Feriado está com nome inválido."),
                null, null, null, Eboolean.F);
    }

    @Test
    public void importarFeriado_deveRetornarFeriadoSalvoNoBanco_quandoDadosSaoCorretos() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        var linha = umaLinha(6);
        assertThat(importacaoService.importarFeriado(linha, 2019))
            .extracting("tipoFeriado", "motivoNaoImportacao", "nome", "dataFeriado", "estadoId",
                "cidadeId", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(ETipoFeriado.MUNICIPAL, List.of(), "FERIADO NOVO DE LONDRINA",
                LocalDate.of(2019, 9, 20), 1, 5578, Eboolean.V);
        assertThat(feriadoRepository.findAll())
            .hasSize(21);
    }

    @Test
    public void importarFeriado_deveSalvarFeriadoEFeriadoFilhos_quandoFeriadoImportadoEEstadual() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);

        var linha = umaLinha(4);
        var feriadoImportado = importacaoService.importarFeriado(linha, 2019);

        assertThat(feriadoImportado)
            .extracting("tipoFeriado", "motivoNaoImportacao", "nome", "dataFeriado", "estadoId",
                "cidadeId", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(ETipoFeriado.ESTADUAL, List.of(), "FERIADO ESTADUAL DA VIOLA",
                LocalDate.of(2019, 3, 22), 22, null, Eboolean.V);

        assertThat(feriadoRepository.findAll())
            .hasSize(27);

        assertThat(feriadoRepository.findAll(
            new FeriadoPredicate()
                .comFeriadoPaiId(feriadoImportado.getId())
                .build()))
            .hasSize(6);
    }

    private MockMultipartFile umFile(byte[] bytes) {
        return new MockMultipartFile("file", "feriado.csv", "text/csv", bytes);
    }

    private InputStream getFileInputStream(String file) throws Exception {
        return new ByteArrayInputStream(
            Files.readAllBytes(Paths.get(
                getClass().getClassLoader().getResource(file)
                    .getPath())));
    }

    private FeriadoImportacaoRequest umFeriadoImportacaoRequest() {
        return FeriadoImportacaoRequest.builder()
            .anoReferencia(2019)
            .build();
    }

    private String[] umaLinha(int index) {
        return CsvFileService.readCsvFile(mockMultipartFile, true)
            .get(index)
            .split(DELIMITADOR);
    }
}
