package helpers;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;

import java.util.Arrays;
import java.util.List;

import static helpers.EstadoHelper.duasUf;
import static helpers.EstadoHelper.umaUf;

public class CidadeHelper {

    public static List<Cidade> umaListaCidade() {
        return Arrays.asList(umaCidade(), duasCidade());
    }

    public static Cidade umaCidade() {
        return Cidade
                .builder()
                .id(1)
                .nome("CIDADEUM")
                .uf(umaUf())
                .build();
    }

    public static Cidade duasCidade() {
        return Cidade
                .builder()
                .id(2)
                .nome("CIDADEDOIS")
                .uf(duasUf())
                .build();
    }
}
