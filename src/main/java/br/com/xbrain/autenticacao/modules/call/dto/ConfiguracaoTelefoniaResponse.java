package br.com.xbrain.autenticacao.modules.call.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoTelefoniaResponse {

    private Integer id;
    private String nome;
    private String ip;

    public String getText() {
        return ip != null && nome != null ?  ip.concat(" - ").concat(nome) : null;
    }
}
