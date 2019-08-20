package br.com.xbrain.autenticacao.modules.feriado.importacao;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.isEmpty;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class FeriadoImportacaoTest {

    private static final String CAMINHO_ARQUIVO_PROCESSADO = "/home/xbrain/feriados_processados.sql";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_SQL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String DELIMITADOR = ";";
    private static final int INDICE_ESTRUTURA = 1;
    private static final int INDICE_COD_IBGE = 2;
    private static final int INDICE_INICIO_DADOS = 3;
    private static final int LINHA_CABECALHO = 0;
    private static final int LINHA_INICIO_DADOS = 2;
    private static final Double VALOR_SABADO = 0.7;

    @Autowired
    private CidadeRepository cidadeRepository;

    @Value("classpath:feriados/feriados.csv")
    Resource resource;

    private List<LocalDate> datasJaExistentes = Stream.of("09/07/2019", "10/12/2019", "11/02/2019", "11/15/2019", "12/25/2019")
            .map(this::parseDate).collect(Collectors.toList());

    /**
     * Importa um arquivo CSV com datas de feriados
     * O Arquivo é fornecido pela Claro/NET, e foram feitas algumas alterações no
     * arquivo original antes da importação:
     *  - Foram removidas todas as colunas cujo dia não fosse mais relevante
     *  - Foram removidos todos os colunas cujo dia fosse um Domingo (pois é bloqueado o contato no domingo)
     *  - Foram removidos todas as linhas que não representassem uma Cidade (exceto a linha que possue as datas e o cabeçalho)
     *  - Após, o arquivo foi convertido para CSV (originalmente um XLSB)
     * Para tratar o arquivo foram usadas as seguintes regras:
     *  - Se o Dia for um domingo, ignorar;
     *  - Se o Dia já estiver cadastrado no banco como feriado, ignorar;
     *  - Se o Valor dá célula for menor que 0.7 (geralmente 0.5) é um feriado. (Obs: os Domingos são representados por 0.20)
     * Obs: Ao gerar o .sql para realizar o INSERT é preciso ajustar o formato do mês para inserir no oracle.
     */
    @Test
    @Ignore
    public void importarCsv() throws IOException {
        var linhas = getCsvColumns(readCsvFile(resource.getFile()), DELIMITADOR);

        if (isEmpty(linhas)) {
            throw new ValidacaoException("O arquivo não pode estar vazio!");
        }

        var cabecalho = linhas.get(LINHA_CABECALHO);

        var registros = IntStream.range(0, linhas.size())
                .skip(LINHA_INICIO_DADOS)
                .mapToObj(i -> obterRegistros(i, linhas.get(i), cabecalho))
                .flatMap(List::stream)
                .filter(Registro::isDiaValido)
                .filter(this::isDataPermitida)
                .filter(registro -> registro.valor < VALOR_SABADO)
                .map(Registro::toInsert)
                .collect(Collectors.toList());

        System.out.println("Total de registros: " + registros.size());

        escreverArquivoProcessado(registros);
    }

    private void escreverArquivoProcessado(List<String> registros) throws IOException {
        var file = new File(CAMINHO_ARQUIVO_PROCESSADO);
        if (file.createNewFile()) {
            System.out.println("Arquivo criado: " + file.getAbsoluteFile());
        } else {
            System.out.println("Sobreescrevendo o arquivo: " + file.getAbsoluteFile());
        }
        try (var writter = new BufferedWriter(new FileWriter(file))) {
            for (var linha : registros) {
                writter.write(linha);
                writter.newLine();
            }
        }
        System.out.println("Arquivo exportado com sucesso!");
    }

    private List<Registro> obterRegistros(int numLinha, String[] linha, String[] cabecalho) {
        if (linha.length < INDICE_INICIO_DADOS) {
            throw new ValidacaoException("A linha " + numLinha + "possui menos colunas que o necessário!");
        }

        var cidade = linha[INDICE_ESTRUTURA];
        var codigoIbge = linha[INDICE_COD_IBGE];
        var fkCidade = cidadeRepository.findCidadeByCodigoIbge(codigoIbge);

        return IntStream.range(INDICE_INICIO_DADOS, linha.length)
                .mapToObj(coluna -> {
                    var valor = linha[coluna];
                    var data = cabecalho[coluna];
                    return new Registro(cidade, codigoIbge, parseDate(data), parseValor(valor),
                            fkCidade.map(Cidade::getId).orElse(-1));
                })
                .collect(Collectors.toList());
    }

    private boolean isDataPermitida(Registro registro) {
        return !datasJaExistentes.contains(registro.getDataFeriado());
    }

    private LocalDate parseDate(String date) {
        return LocalDate.parse(date, DATE_TIME_FORMATTER);
    }

    private Double parseValor(String valor) {
        return Double.parseDouble(valor);
    }

    private List<String> readCsvFile(File file) {
        try (var reader = new BufferedReader(new FileReader(file))) {
            return getCsvLines(reader, true);
        } catch (Exception ex) {
            return List.of();
        }
    }

    private List<String> getCsvLines(BufferedReader reader, boolean possuiCabecalho) {
        return reader.lines().skip(possuiCabecalho ? 1 : 0).collect(Collectors.toList());
    }

    private List<String[]> getCsvColumns(List<String> lines, String delimitador) {
        return lines.stream()
                .map(s -> s.split(getEscape(delimitador), -1))
                .collect(Collectors.toList());
    }

    private static String getEscape(String regex) {
        var res = StringEscapeUtils.escapeJava(regex);
        if ("|".equals(regex)) {
            res = "\\|";
        }
        return res;
    }

    @Data
    @AllArgsConstructor
    public class Registro {
        private String cidade;
        private String codigoIbge;
        private LocalDate dataFeriado;
        private Double valor;
        private Integer fkCidade;

        public boolean isDiaValido() {
            return dataFeriado.getDayOfWeek() != DayOfWeek.SUNDAY;
        }

        public String toInsert() {
            var sql = "INSERT INTO AUTENTICACAO.FERIADO (ID, DATA_CADASTRO, DATA_FERIADO, FERIADO_NACIONAL, NOME, FK_CIDADE) "
                    + "VALUES (SEQ_FERIADO.NEXTVAL, SYSDATE, '" + dataFeriado.format(DATE_TIME_FORMATTER_SQL) + "', 'F', '', "
                    + fkCidade + "); -- " + cidade + " - " + valor;
            return sql;
        }
    }
}
