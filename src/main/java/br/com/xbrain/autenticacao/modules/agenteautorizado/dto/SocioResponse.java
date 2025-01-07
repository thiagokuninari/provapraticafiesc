package br.com.xbrain.autenticacao.modules.agenteautorizado.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocioResponse {

    private Integer id;
    private String cpf;
    private String nome;

}
