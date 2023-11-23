package br.com.xbrain.autenticacao.modules.usuarioacesso.helper;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoDto;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import com.google.common.collect.Lists;

import java.util.List;

public class UsuarioAcessoHelper {
    public static PaLogadoDto umPaLogadoDto(String dataInicial, String dataFinal, Integer totalUsuariosLogados) {
        return PaLogadoDto.builder()
            .dataInicial(dataInicial)
            .dataFinal(dataFinal)
            .totalUsuariosLogados(totalUsuariosLogados)
            .build();
    }

    public static List<PaLogadoDto> umaListaPaLogadoDto() {

        return List.of(
            umPaLogadoDto(
                "2023-06-10",
                "2023-06-20",
                5),
            umPaLogadoDto(
                "2023-06-05",
                "2023-06-09",
                3),
            umPaLogadoDto(
                "2023-06-15",
                "2023-06-22",
                2)
        );
    }

    public static UsuarioLogadoRequest umUsuarioLogadoRequest() {
        return UsuarioLogadoRequest.builder()
            .periodos(umaListaPaLogadoDto())
            .cargos(Lists.newArrayList(CodigoCargo.ADMINISTRADOR))
            .organizacaoId(1)
            .usuariosIds(Lists.newArrayList(1))
            .build();
    }
}
