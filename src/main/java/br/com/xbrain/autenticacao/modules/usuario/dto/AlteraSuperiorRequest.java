package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlteraSuperiorRequest {

    private List<Integer> usuarioIds;
    private Integer superiorNovo;
    private Integer superiorAntigo;
    private Integer usuarioAutenticadoId;
}