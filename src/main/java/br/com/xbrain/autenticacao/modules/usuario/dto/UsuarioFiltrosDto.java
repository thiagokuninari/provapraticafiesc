package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioFiltrosDto {

    private List<Integer> empresasIds = new ArrayList<>();
    private List<Integer> unidadesNegocioIds = new ArrayList<>();
    private Eboolean ativo;
    private List<Integer> usuariosIds = new ArrayList<>();
    private List<Integer> nivelIds = new ArrayList<>();
    private List<Integer> cargoIds = new ArrayList<>();
    private List<Integer> departamentoIds = new ArrayList<>();
    private List<Integer> cidadesIds = new ArrayList<>();

}
