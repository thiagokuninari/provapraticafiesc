package br.com.xbrain.autenticacao.modules.feriado.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class FeriadoSingleton {

    private static FeriadoSingleton feriadoSingleton;

    private Set<LocalDate> feriados;
    private Set<LocalDate> feriadosNacionais;

    public static FeriadoSingleton getInstance() {
        if (ObjectUtils.isEmpty(feriadoSingleton)) {
            feriadoSingleton = new FeriadoSingleton(new HashSet<>(), new HashSet<>());
        }

        return feriadoSingleton;
    }
}
