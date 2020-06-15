package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvFileService {

    private static final char SEPARATOR = ';';
    private static final String ERRO_LER_ARQUIVO = "Não foi possível ler o arquivo informado.";

    public List<String> readCsvFile(MultipartFile file, boolean possuiCabecalho) {
        List<String> linhas;
        try (var isr = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             var reader = new BufferedReader(isr)) {
            linhas = getCsvLines(reader, possuiCabecalho);
        } catch (IOException | UncheckedIOException ex) {
            throw new ValidacaoException(ERRO_LER_ARQUIVO, ex);
        }
        return linhas;
    }

    private List<String> getCsvLines(BufferedReader reader, boolean possuiCabecalho) {
        return reader.lines().skip(possuiCabecalho ? 1 : 0).collect(Collectors.toList());
    }
}
