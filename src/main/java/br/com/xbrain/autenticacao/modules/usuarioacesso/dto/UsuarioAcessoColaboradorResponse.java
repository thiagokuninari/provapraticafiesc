package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioAcessoColaboradorResponse {

    private Integer id;
    private String nome;
}
