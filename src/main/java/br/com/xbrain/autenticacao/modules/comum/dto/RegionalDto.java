package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import lombok.Data;

@Data
public class RegionalDto {

    private Integer id;
    private String nome;
    private ESituacao situacao;

}
