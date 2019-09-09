package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CargoComNivelResponse {

    private Integer id;
    private String nome;

    public static CargoComNivelResponse of(Cargo cargo) {
        return CargoComNivelResponse.builder()
                .id(cargo.getId())
                .nome(cargo.getNome().concat(" - ")
                        .concat(cargo.getNivel().getNome()))
                .build();
    }
}
