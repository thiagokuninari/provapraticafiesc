package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.*;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Canal {

    private Integer usuarioId;
    private ECanal canal;

}
