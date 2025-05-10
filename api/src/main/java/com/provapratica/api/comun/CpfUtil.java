package com.provapratica.api.comun;

public class CpfUtil {

    private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int DIGITO_QUATORZE = 14;
    private static final int DIGITO_ONZE = 11;
    private static final int DIGITO_DEZ = 10;
    private static final int DIGITO_NOVE = 9;

    public static String removerCaracteresDoCpf(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }

    public static boolean isCpfValido(String cpf) {
        if (cpf == null || cpf.length() != DIGITO_ONZE && cpf.length() != DIGITO_QUATORZE) {
            return false;
        }
        cpf = cpf.trim().replace(".", "").replace("-", "");

        for (int j = 0; j < DIGITO_DEZ; j++) {
            if (padLeft(Integer.toString(j), Character.forDigit(j, DIGITO_DEZ))
                    .equals(cpf)) {
                return false;
            }
        }
        int digito1 = calcularDigito(cpf.substring(0, DIGITO_NOVE), pesoCPF);
        int digito2 = calcularDigito(cpf.substring(0, DIGITO_NOVE) + digito1, pesoCPF);
        return cpf.equals(cpf.substring(0, DIGITO_NOVE) + digito1 + digito2);
    }

    private static String padLeft(String text, char character) {
        return String.format("%11s", text).replace(' ', character);
    }

    private static int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int indice = str.length() - 1, digito; indice >= 0; indice--) {
            digito = Integer.parseInt(str.substring(indice, indice + 1));
            soma += digito * peso[peso.length - str.length() + indice];
        }
        soma = DIGITO_ONZE - soma % DIGITO_ONZE;
        return soma > DIGITO_NOVE ? 0 : soma;
    }
}
