package br.com.xbrain.autenticacao.modules.feriado.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.Set;

public class FeriadoSingleton {

    private static FeriadoSingleton feriadoSingleton;

    @Getter
    @Setter
    private Set<LocalDate> feriados;
    @Getter
    @Setter
    private Set<LocalDate> feriadosNacionais;

    public static FeriadoSingleton getInstance() {
        if (ObjectUtils.isEmpty(feriadoSingleton)) {
            feriadoSingleton = new FeriadoSingleton();
        }

        return feriadoSingleton;
    }
}
