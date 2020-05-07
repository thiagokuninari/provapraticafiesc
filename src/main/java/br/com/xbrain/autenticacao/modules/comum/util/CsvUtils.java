package br.com.xbrain.autenticacao.modules.comum.util;

import br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

import static org.thymeleaf.util.StringUtils.concat;

public class CsvUtils {

    private static final Logger log = LoggerFactory.getLogger(CsvUtils.class);

    private static final Integer PARAMETROS_UM = 239;
    private static final Integer PARAMETROS_DOIS = 187;
    private static final Integer PARAMETROS_TRES = 191;

    public static ByteArrayOutputStream createBomStream() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(PARAMETROS_UM);
        byteArrayOutputStream.write(PARAMETROS_DOIS);
        byteArrayOutputStream.write(PARAMETROS_TRES);
        return byteArrayOutputStream;
    }

    public static boolean setCsvNoHttpResponse(String csv, String fileName, HttpServletResponse response) {
        try {
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            ByteArrayOutputStream bos = createBomStream();
            bos.write(csv.getBytes("UTF-8"));
            IOUtils.copy(new ByteArrayInputStream(bos.toByteArray()), response.getOutputStream());
            return true;

        } catch (Exception exception) {
            log.error("Erro ao setar csv no HttpServletResponse", exception);
            return false;
        }
    }

    public static String replaceCaracteres(String string) {
        return !ObjectUtils.isEmpty(string)
            ? string
            .replaceAll(";", "")
            .replaceAll(",", ".")
            .replaceAll("\n", " ")
            .replaceAll("\r", "")
            .replaceAll("\t", "")
            : "";
    }

    public static String createFileName(String relatorioNome, Integer id) {
        return concat("_",
            DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_NOT_FORMAT,
                LocalDateTime.now()), "_",
            id.toString(), ".csv");
    }
}
