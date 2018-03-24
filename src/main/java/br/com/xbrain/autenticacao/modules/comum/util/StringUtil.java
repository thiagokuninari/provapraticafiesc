package br.com.xbrain.autenticacao.modules.comum.util;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StringUtil {

    public static String getOnlyNumbers(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9]", "");
    }

    public static String getDataAtualEmail() {
        return LocalDate.now().format(DateTimeFormatter
                .ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt","BR")));
    }

    public static String removerAcentos(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^\\p{ASCII}]", "");
        return str;
    }

    public static String formatarMonetario(BigDecimal valor) {
        return formatarMonetario(valor, new Locale("pt", "BR"));
    }

    public static String formatarMonetario(BigDecimal valor, Locale locale) {
        if (ObjectUtils.isEmpty(valor)) {
            return "";
        }
        NumberFormat format = NumberFormat.getNumberInstance(locale);
        format.setGroupingUsed(true);
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        return format.format(valor);
    }

    public static BigDecimal getValorMonetarioFormatado(String valorStr) {
        if (ObjectUtils.isEmpty(valorStr)) {
            return BigDecimal.ZERO;
        }
        String valor = valorStr.replace(".", "").replace(",", ".");
        return new BigDecimal(valor);
    }

    public static String getStringFormatadaCsv(Serializable serializable) {
        String valorFormatado = "";

        if (!ObjectUtils.isEmpty(serializable)) {
            if (serializable instanceof String) {
                valorFormatado = ((String) serializable);
            } else if (serializable instanceof BigDecimal) {
                valorFormatado = formatarMonetario(((BigDecimal) serializable), Locale.US);
            } else {
                throw new ValidacaoException("Tipo " + serializable.getClass() + " não suportado");
            }
        }

        return valorFormatado.replaceAll("[;|,|\t]", "");
    }

    public static String getNomeAbreviado(String nome) {
        if (StringUtils.isEmpty(nome)) {
            return "SN";
        }
        String nomeSingleSpaced = nome.replaceAll("[ ] +", " ");
        String[] array = nomeSingleSpaced.split(" ");
        int size = array.length;

        if (size <= 1) {
            return String.valueOf(nome.charAt(0));
        }

        String firstName = String.valueOf(array[0].charAt(0));
        String lastName = String.valueOf(array[size - 1].charAt(0));
        return firstName + "" + lastName;
    }
}
