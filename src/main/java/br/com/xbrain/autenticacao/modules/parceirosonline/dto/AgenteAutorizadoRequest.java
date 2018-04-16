package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AgenteAutorizadoRequest implements Serializable {

    private String cnpj;
}
