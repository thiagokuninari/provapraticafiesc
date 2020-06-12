package helpers;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;

public class EstadoHelper {

    public static Uf umaUf() {
        return Uf
                .builder()
                .id(1)
                .nome("UFUM")
                .uf("UF1")
                .build();
    }

    public static Uf duasUf() {
        return Uf
                .builder()
                .id(2)
                .nome("UFDOIS")
                .uf("UF2")
                .build();
    }
}
