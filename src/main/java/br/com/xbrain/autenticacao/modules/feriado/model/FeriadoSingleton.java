package br.com.xbrain.autenticacao.modules.feriado.model;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.Set;

@Data
public class FeriadoSingleton {

    private static FeriadoSingleton feriadoSingleton;

    private Set<LocalDate> feriados;
    private Set<LocalDate> feriadosNacionais;

    public static FeriadoSingleton getInstance() {
        if (ObjectUtils.isEmpty(feriadoSingleton)) {
            feriadoSingleton = new FeriadoSingleton();
        }

        return feriadoSingleton;
    }
}
