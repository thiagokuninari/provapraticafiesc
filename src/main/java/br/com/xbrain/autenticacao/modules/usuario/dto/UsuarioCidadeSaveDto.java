package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UsuarioCidadeSaveDto {

    @NotNull
    private Integer usuarioId;
    @NotNull
    private List<Integer> cidadesId;

}
