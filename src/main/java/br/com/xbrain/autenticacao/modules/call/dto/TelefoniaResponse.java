package br.com.xbrain.autenticacao.modules.call.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelefoniaResponse {

    private Integer id;
    private String nome;
}
