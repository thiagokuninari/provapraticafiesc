package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UsuariosAlvoComunicadosFiltros {
    private boolean todoCanalD2d;
    private boolean todoCanalAa;
    private List<Integer> usuariosId;
    private List<Integer> cargosId;
    private List<Integer> cidadesId;
    private List<Integer> niveisId;
}
