package helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;

import java.util.List;

public class DepartamentoHelper {

    public static Departamento umDepartamento(Integer id, String nome) {
        return Departamento.builder()
            .id(id)
            .nome(nome)
            .nivel(umNivel())
            .situacao(ESituacao.A)
            .codigo(CodigoDepartamento.COMERCIAL)
            .build();
    }

    public static List<Departamento> umaListaDepartamentos() {
        return List.of(umDepartamento(1, "Departamento 1"), umDepartamento(2, "Departamento 2"));
    }

    public static Nivel umNivel() {
        return Nivel.builder()
            .id(200)
            .nome("Nivel teste")
            .codigo(CodigoNivel.MSO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

}
