package br.com.xbrain.autenticacao.modules.feeder.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ECargosComPermissaoAaFeeder {

    AGENTE_AUTORIZADO_SOCIO(41),
    AGENTE_AUTORIZADO_SOCIO_SECUNDARIO(42),
    AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS(56),
    AGENTE_AUTORIZADO_BACKOFFICE_D2D(78),
    AGENTE_AUTORIZADO_BACKOFFICE_TEMP(84),
    AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS_RECEPTIVO(88),
    AGENTE_AUTORIZADO_GERENTE(47),
    AGENTE_AUTORIZADO_COORDENADOR(45);

    private final Integer valor;

    public static List<Integer> listaDeCargos() {
        return Arrays.stream(ECargosComPermissaoAaFeeder.values())
            .mapToInt(ECargosComPermissaoAaFeeder::getValor)
            .boxed()
            .collect(Collectors.toList());
    }
}
