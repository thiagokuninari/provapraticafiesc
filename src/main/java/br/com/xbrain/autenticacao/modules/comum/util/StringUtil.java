package br.com.xbrain.autenticacao.modules.comum.util;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.xbrainutils.MoneyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class StringUtil {

    private static final int RADIX = 36;

    public static String getDataAtualEmail() {
        return LocalDate.now().format(DateTimeFormatter
                .ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt","BR")));
    }

    public static String removerAcentos(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^\\p{ASCII}]", "");
        return str;
    }

    public static String getStringFormatadaCsv(Serializable serializable) {
        String valorFormatado = "";

        if (!ObjectUtils.isEmpty(serializable)) {
            if (serializable instanceof String) {
                valorFormatado = (String) serializable;
            } else if (serializable instanceof BigDecimal) {
                valorFormatado = MoneyUtils.formatarMonetario((BigDecimal) serializable, Locale.US);
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
        String nomeSingleSpaced = nome.trim().replaceAll("[ ] +", " ");
        String[] array = nomeSingleSpaced.split(" ");
        int size = array.length;

        if (size <= 1) {
            return String.valueOf(nome.trim().charAt(0));
        }

        String firstName = String.valueOf(array[0].charAt(0));
        String lastName = String.valueOf(array[size - 1].charAt(0));
        return firstName + "" + lastName;
    }

    public static String getSenhaRandomica(int size) {
        String tag = Long.toString(Math.abs(new Random().nextLong()), RADIX);
        return tag.substring(0, size);
    }
}
