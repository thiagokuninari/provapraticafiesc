package br.com.xbrain.autenticacao.modules.comum.util;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.importacaousuario.util.EmailUtil;
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

import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_VALIDAR_EMAIL_CADASTRADO;

public class StringUtil {

    private static final int RADIX = 36;
    private static final String ARROBA = "@";
    private static final Integer INDEX_ZERO = 0;
    private static final Integer INDEX_UM = 1;
    private static final String INATIVO_ARROBA = ".INATIVO@";

    public static String getDataAtualEmail() {
        return LocalDate.now().format(DateTimeFormatter
            .ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR")));
    }

    public static String getOnlyNumbers(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9]", "");
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

        return valorFormatado.replaceAll("[\\[\\];|,|\t]", "");
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

    public static String extrairNumerosELetras(String str) {
        return removerAcentos(str).replaceAll("[\\W|_]", "");
    }

    public static boolean existeSemelhancaEntreNomes(String nomeOrigem, String nomeDestino) {
        return extrairNumerosELetras(nomeOrigem)
            .equalsIgnoreCase(extrairNumerosELetras(nomeDestino));
    }

    public static String atualizarEmailInativo(String email) {
        if (!EmailUtil.validarEmail(email)) {
            throw new ValidacaoException(ERRO_VALIDAR_EMAIL_CADASTRADO.getDescricao());
        }
        var emailSplit = email.split(ARROBA);
        return emailSplit[INDEX_ZERO].concat(INATIVO_ARROBA).concat(emailSplit[INDEX_UM]);
    }
}
