package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
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
    private UsuarioAutenticado usuarioAutenticado;
}