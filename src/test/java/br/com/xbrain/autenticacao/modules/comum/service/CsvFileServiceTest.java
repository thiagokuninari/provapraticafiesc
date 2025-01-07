package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CsvFileServiceTest {

    @Test
    public void readCsvFile_deveLerLinhas_quandoSolicitado() {
        var linhas = CsvFileService.readCsvFile(umFileFeriado(), false);
        assertThat(linhas.size()).isEqualTo(5);

        assertThat(linhas.get(0)).isEqualTo("TIPO DO FERIADO;CIDADE;UF;DATA DO FERIADO;NOME DO FERIADO");
        assertThat(linhas.get(1)).isEqualTo("NACIONAL;LONDRINA;PR;12/10/2019;FERIADO CORRETO");
        assertThat(linhas.get(2)).isEqualTo("NA;LONDRINA;PR;2019-10-12;FERIADO INCORRETO");
        assertThat(linhas.get(3)).isEqualTo("MUNICIPAL;LONDRINA;PR;12/10/2019;FERIADO INCORRETO");
        assertThat(linhas.get(4)).isEqualTo("NACIONAL;LONDRINA;PR;12/10/2019;FERIADO EXISTENTE");
    }

    @Test
    public void readCsvFile_deveLancarExcecaoQuandoIOExceptionOcorrer() throws IOException {
        var file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException());
        assertThatCode(() ->
            CsvFileService.readCsvFile(file, false))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Não foi possível ler o arquivo informado.");

    }

    private MockMultipartFile umFile(byte[] bytes, String nome) {
        return new MockMultipartFile("file", LocalDateTime.now().toString().concat(nome) + ".csv",
            "text/csv", bytes);
    }

    private MockMultipartFile umFile(String file) {
        byte[] bytes = file.getBytes(StandardCharsets.UTF_8);
        String nome = "teste_arquivo";
        return umFile(bytes, nome);
    }

    private MockMultipartFile umFileFeriado() {
        String file = "TIPO DO FERIADO;CIDADE;UF;DATA DO FERIADO;NOME DO FERIADO\n"
            + "NACIONAL;LONDRINA;PR;12/10/2019;FERIADO CORRETO\n"
            + "NA;LONDRINA;PR;2019-10-12;FERIADO INCORRETO\n"
            + "MUNICIPAL;LONDRINA;PR;12/10/2019;FERIADO INCORRETO\n"
            + "NACIONAL;LONDRINA;PR;12/10/2019;FERIADO EXISTENTE\n";

        return umFile(file);
    }
}
