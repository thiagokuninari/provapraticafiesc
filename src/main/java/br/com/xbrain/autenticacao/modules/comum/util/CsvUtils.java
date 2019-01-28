package br.com.xbrain.autenticacao.modules.comum.util;

import br.com.xbrain.autenticacao.modules.comum.enums.RelatorioNome;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CsvUtils {

    private static final Logger logger = LoggerFactory.getLogger(CsvUtils.class);

    public static boolean setCsvNoHttpResponse(String csv, String fileName, HttpServletResponse response) {
        try {
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            ByteArrayOutputStream bos = createBomStream();
            bos.write(csv.getBytes(StandardCharsets.UTF_8));
            IOUtils.copy(new ByteArrayInputStream(bos.toByteArray()), response.getOutputStream());
            return true;

        } catch (Exception exception) {
            logger.error("Erro ao setar csv no HttpServletResponse", exception);
            return false;
        }
    }

    public static String replaceCaracteres(String string) {
        return string != null
                ? string
                        .replaceAll(";", "")
                        .replaceAll(",", ".")
                        .replaceAll("\n", " ")
                : "";
    }

    @SuppressWarnings({"PMD.MagicNumber", "checkstyle:MagicNumber"})
    private static ByteArrayOutputStream createBomStream() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(239);
        byteArrayOutputStream.write(187);
        byteArrayOutputStream.write(191);
        return byteArrayOutputStream;
    }

    public static String createFileName(RelatorioNome relatorioNome) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return relatorioNome
                + "_" + LocalDateTime.now().format(formatter)
                + ".csv";
    }
}
